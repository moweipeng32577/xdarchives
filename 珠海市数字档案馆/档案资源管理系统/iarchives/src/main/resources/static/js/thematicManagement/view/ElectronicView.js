/**
 * Created by Rong on 2017/11/15.
 */
Ext.define('ThematicProd.view.ElectronicView', {
    entryid: undefined,     //条目主键ID
    entrytype: '',   //数据类型（采集、管理、利用）

    extend: 'Ext.panel.Panel',
    xtype: 'electronicPro',
    layout: 'border',
    bodyBorder: false,
    defaults: {
        split: true
    },
    //isImportThematicZip:"false",
    // listeners:{
    //     activate:function () {
    //         var tree = Ext.ComponentQuery.query('electronic treepanel')[0];
    //         tree.expandAll();
    //         var eleStore = tree.getStore();
    //         eleStore.proxy.extraParams.fileClassId = null;
    //     }
    // },
    items: [{
        region: 'west',
        width: 350,
        xtype: 'treepanel',
        header: false,
        hideHeaders: true,
        store: {
            extend: 'Ext.data.TreeStore',
            proxy: {
                type: 'ajax',
                url: '/electronic/electronics/tree/',
                reader: {
                    type: 'json',
                    expanded: false
                }
            },
            root: {
                text: '专题文件'
            },
            // listeners:{
            //     beforeload:function(node){
            //         //this.proxy.extraParams.nodeid = window.nodeid;
            //     },
            //     nodebeforeexpand:function(node, deep, animal) {
            //         if((node.raw)){
            //             this.proxy.extraParams.fileClassId = node.raw.fnid;
            //             this.proxy.extraParams.entryid = this.entryid;
            //         }
            //     }
            // }
        },
        autoScroll: true,
        rootVisible: true,
        checkPropagation: 'both',
        dockedItems: [{
            xtype: 'toolbar',
            dock: 'top',
            items: [
                {
                    xtype: 'button', text: '上传', handler: function () {
                    var view = this.findParentByType('electronicPro');
                    window.isImportThematicZip="false";
                    view.openUploadUi();
                }
                },
                {
                    xtype: 'button', text: '下载', handler: function () {
                    var view = this.findParentByType('electronicPro');
                    var records = view.down('treepanel').getView().getChecked();
                    view.download(records);
                }
                },
                {
                    xtype: 'button', text: '全部下载', handler: function () {
                    var view = this.findParentByType('electronicPro');
                    var records = view.down('treepanel').getStore().getRoot().childNodes;
                    view.download(records);
                }
                },
                {
                    xtype: 'button', text: '删除', handler: function () {
                    var view = this.findParentByType('electronicPro');
                    view.del();
                }
                },
                {
                    xtype: 'button', text: '导入专题包', handler: function () {
                        var view = this.findParentByType('electronicPro');
                        window.isImportThematicZip="true"
                        view.openUploadUi();
                    }
                },
                {
                    xtype: 'button', text: '文件夹重命名', handler: function () {
                        var view = this.findParentByType('electronicPro');
                        var win = Ext.create('Comps.view.UpdateFolderNameWin', {
                            entrytype: view.entrytype,
                        });
                        var nodes = view.down('treepanel').getRootNode().childNodes;
                        var name="";
                        for(var i = 0; i <nodes.length; i++) {
                            if (nodes[i].get('cls') == 'folder'){//表示当前电子文件存在分类
                                try{
                                    var fc = view.down('treepanel').getSelectionModel().getSelected().items[0].data.fileClassId
                                    if (fc) {//表示选中的是非root节点，如果是root节点fileClassId=null
                                        name = view.down('treepanel').getSelectionModel().getSelected().items[0].data.text;
                                        win.eleId=view.down('treepanel').getSelectionModel().getSelected().items[0].data.fnid;
                                    }
                                }catch (e) {
                                    XD.msg('未选择分类目录');
                                    return;
                                }
                                break;
                            }
                        }
                        if(""==name){
                            XD.msg('请选择文件夹');
                            return;
                        }
                        win.on('close', function () {
                            view.initData(view.entryid);
                        });
                        win.down("form").down('[itemId=names]').setValue(name);
                        win.show();
                    }
                },
                {
                    xtype: 'button', text: '返回', handler: function () {
                    var view = this.findParentByType('electronicPro');
                    window.wmedia = view.getEleids();
                    if(window.wform) {
                        window.wform.query('[itemId=mediacount]')[0].setText('共' + window.wmediaName.length + '份');
                        window.wform.query('[itemId=media]')[0].setValue(window.wmediaName.join());
                    }
                    view.findParentByType('window').close();
                }
                }
            ]
        }],
        listeners: {
            beforeload: function () {
                var view = this.findParentByType('electronicPro');
                this.getStore().proxy.url = '/electronic/electronics/tree/thematic' + '/' + view.entryid + '/undefined';
            },
            afterrender: function () {
                this.expandAll();
            },
            itemclick: function (view, record, item, index, e, eOpts) {
                if (!record.get('leaf'))
                    return;
                // if(e.getTarget('.x-tree-checkbox',1,true)){
                //     return;
                // }
                var mediaFrame = document.getElementById('mediaFrame');
                var filename = record.get('text');
                mediaFrame.setAttribute('src', '/electronic/media?entrytype=management&eleid=' + record.get('fnid') + '&filetype=' + filename.substring(filename.lastIndexOf('.') + 1));
            },
            load:function(node) {
                var view = this.findParentByType('electronicPro');
                // this.expandAll();
                 var nodeItems = node.data.items;
                // this.getStore().proxy.extraParams.fileClassId = null;
                // this.getStore().proxy.extraParams.entryid = view.entryid;
                var record;
                for (var i = 0; i < nodeItems.length; i++) {//遍历去寻找root下的第一个文件
                    if (nodeItems[i].get('cls') == 'file') {
                        record = nodeItems[i];
                        break;
                    }
                }
                if (record){//表示root下有文件，选中第一个文件并且预览文件。
                    this.getSelectionModel().select(record);//默认选中root下面的第一个节点
                    Ext.defer(function () {//需要等mediaFrame页面加完后再执行以下方法。
                        view.openFile(record);
                    },800);
                }else{//表示root下没文件，如果root下有分类默认选中第一个分类，如果没有就不选
                    this.getSelectionModel().select(nodeItems[1]);
                }
            }
        }
    }, {
        region: 'center',
        layout: 'border',
        items: [
            {
                region: 'center',
                width: '100%',
                height: '100%',
                html: '<iframe id="mediaFrame" src=""  width="100%" height="100%" style="border:0px;"></iframe>'
            }
        ]
    }],
    openFile:function(record){
        var mediaFrame = getmediaFrame(this);
        if (!mediaFrame){//表示mediaFrame还未加载出来
            return;
        }
        var filename=record.get('text');
        var start=filename.indexOf("n>")+2;
        filename=filename.substring(start,filename.length);
        filename=encodeURIComponent(filename);
        mediaFrame.setAttribute('src', '/electronic/media?entrytype=management&eleid=' + record.get('fnid') + '&filetype=' + filename.substring(filename.lastIndexOf('.') + 1));
    },
    openUploadUi:function(){
        var tree = this.down('treepanel');
        var fileClassId = null;
        var nodes = tree.getRootNode().childNodes;
        for(var i = 0; i <nodes.length; i++) {
            if (nodes[i].get('cls') == 'folder'){//表示当前电子文件存在分类
                try{
                    var fc = tree.getSelectionModel().getSelected().items[0].data.fileClassId
                    if (fc) {//表示选中的是非root节点，如果是root节点fileClassId=null
                        fileClassId = tree.getSelectionModel().getSelected().items[0].data.fnid;
                    }
                }catch (e) {
                    XD.msg('未选择分类目录');
                    return;
                }
                break;
            }
        }
        var win = Ext.create('Comps.view.UploadView', {
            entrytype: this.entrytype,
            entryid: this.entryid,
            treepanel:this.down('treepanel'),
            isImportThematicZip:window.isImportThematicZip,
            fileClassId:fileClassId,
        });
        window.isUploadsuccess="";
        win.on('close', function () {
            if (typeof(this.entryid) == 'undefined') {
                var store = win.down('grid').getStore();
                for (var i = 0; i < store.getCount(); i++) {
                    var data = store.getAt(i).data;
                    if (data.progress == 1) {
                        this.down('treepanel').getRootNode().appendChild({
                            fnid: data.eleid,
                            text: data.name,
                            checked: false,
                            leaf: true
                        });
                    }
                }
            } else {
                if(window.isUploadsuccess=='true') {
                    this.initData(this.entryid);
                }
            }
        }, this);
        win.show();
    },
    initData: function (entryid) {
        this.entryid = entryid;
        var treeNode = this.down('treepanel');
        var rootNode = treeNode.getRootNode();
        //在加载树列表数据前先清空原有的树列表数据。
        while (rootNode.hasChildNodes()) {
            rootNode.removeChild(rootNode.firstChild);
        }
        var treeStore = this.down('treepanel').getStore();
        var eles = this.getEleids();
        if (eles == "" || eles == "undefined") {//修改时没有电子文件存在
            eles = [];
        }else{//修改时有电子文件存在
            eles = eles.split(',');
        }
        treeNode.getRootNode().set({'text':'原文列表  （总数：'+eles.length+'）',checked:false});
        treeStore.proxy.extraParams.fileClassId = null;
        treeStore.load({
            callback:function(r,options,success){
                var mediaFrame = getmediaFrame(treeNode.findParentByType('electronicPro'));
                if(mediaFrame){
                    mediaFrame.setAttribute('src',"");  //刷新显示的图片
                }
            }
        });
    },

    getEleids: function () {
        var ids = [];
        window.wmediaName=[];
        //通过实体id去后台找到其所有对应的电子文件，防止树节点采用懒加载时无法获取全部电子文件id
        Ext.Ajax.request({
            method: 'GET',
            url: '/electronic/getEles',
            params:{entrytype:this.entrytype,entryid:this.entryid},
            scope: this,
            async:false,
            success: function (response) {
                var data = Ext.decode(response.responseText).data;
                Ext.each(data,function (item) {
                    window.wmediaName.push(item.filename);
                    ids.push(item.eleid);
                })
            }
        });
        return ids.join();
    },

    download: function (records) {
        for (var i = 0; i < records.length; i++) {
            setTimeout(function (entrytype, eleid) {
                var downloadForm = document.createElement("form");
                document.body.appendChild(downloadForm);
                downloadForm.action = '/electronic/electronics/download/' + entrytype + '/' + eleid;
                downloadForm.submit();
            }, i * 300, this.entrytype, records[i].data.fnid);
        }
    },

    del: function () {
        var records = this.down('treepanel').getView().getChecked();
        if (records.length == 0) {
            XD.msg('请至少选择一条需要删除的数据');
            return;
        }
        var tipStr="";
        for (var i = 0; i < records.length; i++) {
            if(records[i].data.cls == 'folder'){
                tipStr="删除文件夹，文件夹内的文件也会被删除，";
                break;
            }
        }
        XD.confirm(tipStr+"确定要删除选定的数据吗", function () {
            var eleids = [];
            for (var i = 0; i < records.length; i++) {
                eleids.push(records[i].data.fnid);
            }
            Ext.Ajax.request({
                method: 'DELETE',
                url: '/electronic/ztelectronics/management/' + this.entryid + '/' + eleids.join(","),
                scope: this,
                success: function (response) {
                    XD.msg(Ext.decode(response.responseText).msg);
                    window.wmedia = this.getEleids();
                    this.initData(this.entryid);
                    var mediaFrame = document.getElementById('mediaFrame');
                    mediaFrame.setAttribute('src', '');
                }
            })
        }, this);
    }
});

//文件上传弹出框
Ext.define('Comps.view.UploadView', {
    extend: 'Ext.window.Window',
    xtype: 'uploadview',
    uploader: null,
    modal: true,
    width: 800,
    height: 400,
    title: '文件上传',
    layout: 'fit',
    closeToolText: '关闭',
    actions: {
        del: {
            iconCls: 'x-action-upload-delete-icon',
            tooltip: '删除',
            handler: function (view, row) {
                var grid = view.grid;
                var record = grid.getStore().getAt(row);
                //从上传队列中删除文件
                var uploader = grid.findParentByType('uploadview').uploader;
                uploader.removeFile(record.get('id'), true);
                //从列表中删除文件
                grid.getStore().remove(record);
                //清除服务器上的缓存
                Ext.Ajax.request({
                    method: 'DELETE',
                    url: '/electronic/chunk/' + record.get('name') + '/'
                });
            },
            getClass: function(v, metadata, r, rowIndex, colIndex, store) {
                if(typeof(r.data.eleid) != 'undefined') {
                    return "x-hidden";
                }else{
                    return "x-action-upload-delete-icon";
                }
            }
        }
    },
    items: {
        xtype: 'grid',
        store: [],
        border: false,
        scrollable: true,
        columns: [{xtype: 'rownumberer'}, {hidden: true, dataIndex: 'id'}, {
            header: '文件名称',
            dataIndex: 'name',
            flex: 1
        }, {
            text: '文件大小',
            dataIndex: 'size',
            width: 80,
            renderer: function (value) {
                return value + 'MB';
            }
        }, {
            text: '上传进度',
            xtype: 'widgetcolumn',
            width: 150,
            widget: {
                bind: '{record.progress}',
                xtype: 'progressbarwidget',
                textTpl: ['{percent:number("1")}%']
            }
        }, {
            xtype: 'actioncolumn',
            width: 30,
            items: ['@del']
        }],
        tbar: [{
            xtype: 'displayfield',
            id: 'picker',
            width: 82,
            height: 37
        }, {
            xtype: 'button',
            text: '开始上传',
            width: 90,
            height: 37,
            handler: function () {
                var win = this.findParentByType('uploadview');
                if(window.isImportThematicZip=="true"){
                    var name=win.down('grid').getStore().data.items[0].data.name;
                    var fileType=name.substring(name.lastIndexOf('.') + 1);
                    fileType=fileType.toLowerCase();
                    if(fileType!="zip"&&fileType!="rar"&&fileType!="jar"&&fileType!="gzip"&&fileType!="tar"&&fileType!="cab"&&
                        fileType!="uue"&&fileType!="arj"&&fileType!="bz2"&&fileType!="lzh"&&fileType!="ace"&&fileType!="iso"&&
                        fileType!="7-zip"&&fileType!="z"&&fileType!="7z") {
                        XD.msg("请选择压缩包格式");
                        return;
                    }
                }
                if (win.down('grid').getStore().getCount() == 0) {
                    XD.msg('未选择文件');
                    return;
                }

                win.uploader.upload();
            }
        }]
    },
    listeners: {
        render: function (win) {
            //注册分片上传处理事件，用于断点续传
            WebUploader.Uploader.register({
                name: 'electronic',
                'before-send': 'checkchunk'
            }, {
                checkchunk: function (block) {
                    var deferred = WebUploader.Deferred();
                    Ext.Ajax.request({
                        url: '/electronic/chunk/' + encodeURIComponent(block.file.name) + '/' + block.chunks + '/' + block.chunk,
                        aysnc: false,
                        success: function (response, opts) {
                            if (response.responseText == "true") {
                                deferred.reject();
                            } else {
                                deferred.resolve();
                            }
                        }
                    });
                    return deferred.promise();
                }
            });
            //初始化文件上传组件
            win.uploader = WebUploader.create({
                // swf文件路径
                swf: '/js/Uploader.swf',
                // 文件接收服务端。
                server: '/electronic/electronicsThematic',
                // 选择文件的按钮。可选。
                // 内部根据当前运行是创建，可能是input元素，也可能是flash.
                pick: {id: '#picker', label: '选择文件'},
                //是否要分片处理大文件上传(断点续传)
                chunked: true,
                //文件分片大小，5M
                chunkSize: 5242880,
                //某个分片由于网络问题出错，自动重传次数
                chunkRetry: 3,
                //上传并发数
                threads: 3,
                //单文件大小限制，500M
                fileSingleSizeLimit: 524288000

            });
            //监听文件选择事件，将选中的文件信息添加到列表中
            win.uploader.on('filesQueued', function (files) {
                var msgs = '';
                var treeFileNames=[];
                var records = win.treepanel.getStore().getRoot().childNodes;
                for (var i = 0; i < records.length; i++) {
                    treeFileNames.push(records[i].data.text);
                }
                for (var i = 0; i < files.length; i++) {
                    if($.inArray(files[i].name,treeFileNames)==-1){
                        win.down('grid').getStore().add({
                            id: files[i].id,
                            name: files[i].name,
                            size: Math.floor(files[i].size / 10240) / 100,
                            progress: 0
                        });
                    }else{
                        msgs += "'"+files[i].name +"'"+ "文件已存在<br>";
                    }
                }
                if (msgs.length>0) {
                    Ext.Msg.alert('提示', msgs);
                }
            });
            //监听文件上传进度，更新列表中上传进度条
            win.uploader.on('uploadProgress', function (file, progress) {
                if (!win.destroyed) {
                    var record = win.down('grid').getStore().getById(file.id);
                    record.set('progress', progress);
                }
            });
            //监听文件上传成功，提示用户
            win.uploader.on('uploadSuccess', function (file, response) {
                Ext.Ajax.request({
                    method: 'POST',
                    url: '/electronic/ztelectronics/management' + (typeof(win.entryid) == 'undefined' ? '' : '/' + win.entryid) + '/' + encodeURIComponent(file.name) + '/',
                    params:{
                        isImportThematicZip:win.isImportThematicZip,
                        fileClassId:win.fileClassId
                    },
                    success: function (response) {
                        window.isUploadsuccess="true";
                        var data = Ext.decode(response.responseText).data;
                        if (!win.destroyed&&win.isImportThematicZip=="false") {
                            if(window.wmedia){
                                window.wmedia=[];
                            }
                            var grid = win.down('grid');
                            var record = grid.getStore().getById(file.id);
                            grid.getView().getRow(record).style.backgroundColor = '#87CEFA';
                            record.set('eleid', data.eleid);
                            window.wmedia.push(data.eleid);
                        }
                        //console.log(data);
                    }
                });
            });
            //监听所有文件上传完毕，提示用户
            win.uploader.on('uploadFinished', function () {
                XD.msg('所有文件上传完毕');
            });
        },
        beforeclose: function (win) {
            win.uploader.stop(true);
            win.uploader.reset();
            win.uploader.destroy();
            WebUploader.Uploader.unRegister('electronicPro');
        }
    }
});
//文件上传弹出框
Ext.define('Comps.view.UpdateFolderNameWin', {
    extend: 'Ext.window.Window',
    xtype: 'updateFolderNameWin',
    title: '修改文件夹名称',
    width: 400,
    height: 170,
    closeToolText: '关闭',
    modal: true,
    resizable: false,
    layout: 'fit',
    items: [{
        xtype: 'form',
        autoScroll: true,
        layout: {
            type: 'vbox',
            align: 'stretch'
        },
        fieldDefaults: {
            labelWidth: 100
        },
        bodyPadding: 20,
        items: [{
            layout: 'column',
            items: [{
                columnWidth: 1,
                items: [{
                    xtype: 'textfield',
                    fieldLabel: '文件夹名称<span style="color: #CC3300; padding-right: 2px;">*</span>',
                    allowBlank: false,
                    blankText: '该输入项为必输项',
                    name: 'names',
                    itemId:"names",
                    style: 'width: 100%'
                }]
            }]
        }]
    }],
    buttons: [{
        itemId: 'saveId',
        text: '保存',
        handler: function () {
            var win = this.findParentByType('updateFolderNameWin');
            var name=win.down("form").down('[itemId=names]').getValue();
            Ext.Ajax.request({
                method: 'POST',
                url: '/electronic/updateFolderName',
                params:{
                    eleId:win.eleId,
                    name:name
                },
                aysnc: false,
                scope: this,
                success: function (response) {
                    var text = Ext.decode(response.responseText);
                    if (text.success) {
                        win.close();
                    }else {
                        XD.msg(text.msg);
                    }
                }
            });
        }
    }]
});
function getmediaFrame(view) {
    var allMediaFrame = document.querySelectorAll('#mediaFrame');
    var mediaFrame;
    //创建electronicview需要指定operateFlag类型，经调试，著录（add）的iframe是二个，修改(modify),查看(look)的是第一个
    if(allMediaFrame.length == 1){
        mediaFrame =allMediaFrame[0];
    }
    else if (allMediaFrame.length > 1 && view.operateFlag == 'add') { //创建了多个iframe时候， 著录的iframe是第二个
        mediaFrame = allMediaFrame[1];
    }
    else if(allMediaFrame.length >1 && (view.operateFlag == 'modify' || view.operateFlag == 'look')){  //创建了多个iframe时候， 修改和查看的iframe是第一个
        mediaFrame = allMediaFrame[0];
    }
    else {
        mediaFrame = allMediaFrame[allMediaFrame.length - 1];
    }
    return mediaFrame;
}