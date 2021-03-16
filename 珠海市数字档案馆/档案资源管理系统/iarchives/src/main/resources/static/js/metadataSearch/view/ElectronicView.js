/**
 * Created by Rong on 2017/11/15.
 */
Ext.define('MetadataSearch.view.ElectronicView', {
    entryid: '',     //条目主键ID
    entrytype: '',   //数据类型（采集、管理、利用）
    timer:null,     //上移下移定时器
    extend: 'Ext.panel.Panel',
    xtype: 'electronicPro',
    layout: 'border',
    operateFlag:'',
    bodyBorder: false,
    defaults: {
        split: true
    },
    items: [{
        region: 'west',
        // width: 400,
        xtype: 'panel',
        header: false,
        hideHeaders: true,
        width: 400,
        autoScroll:true,
        items: [{
            xtype: 'treeLabelView',
            itemId: 'treeLabelView',
            // width: 399,
            height: 730
        }]
    }, {
        region: 'center',
        layout: 'border',
        items: [
            {
                region: 'center',
                width: '100%',
                height: '100%',
                html: '<div id="loadingDiv" style="display: none; "><div id="over" style=" position: absolute;top: 0;left: 0; width: 100%;height: 100%; background-color: #f5f5f5;opacity:0.5;z-index: 1000;"></div><div id="layout" style="position: absolute;top: 10%; left: 35%;width: 10%; height: 10%;  z-index: 1001;text-align:center;"><img src="../img/Picloading.gif" /></div></div>'+
                    '<iframe id="mediaFrame" src=""  width="100%" height="100%" style="border:0px;"></iframe>'

            }
        ]
    }],

    initData: function (entryid) {
        this.entryid = entryid;
        window.remainEleids = 'undefined';
        var treeStore = this.down('treepanel').getStore();
        if(typeof(entryid) == 'undefined'){
            this.down('treepanel').getRootNode().removeAll();
            return;
        }
        var treeNode = this.down('treepanel')
        Ext.defer(function () {
            treeStore.load({
                callback:function(r,options,success){
                    treeNode.getRootNode().set('text','原文列表  （总数：'+r.length+'）');
                }
            });
            var allMediaFrame = document.querySelectorAll('#mediaFrame');
            var mediaFrame;
            //创建electronicview需要指定是著录还是修改类型，经调试，著录的iframe是第一个，修改的是最后一个
            if (allMediaFrame.length > 0 && this.operateFlag == 'add') {
                mediaFrame = allMediaFrame[allMediaFrame.length - 1];
            } else {
                mediaFrame = allMediaFrame[0];
            }
            mediaFrame.setAttribute('src', '');
        },300);
    },

    getEleids: function () {
        var ids = [];
        var root = this.down('treepanel').getStore().getRoot();
        if(root != null){
            var records = root.childNodes;
            for (var i = 0; i < records.length; i++) {
                ids.push(records[i].get('fnid'));
            }
        }
        return ids.join(",");
    },

    download: function (records) {
        var ids = [];
        for (var i = 0; i < records.length; i++) {
            if(records[i].get('fnid')!==''){
                ids.push(records[i].get('fnid'));
            }
        }
        var idsStr = ids.join(',');
        Ext.Ajax.request({
            method: 'GET',
            url: '/electronic/ifFileExist/' + this.entrytype+ '/' + idsStr,
            scope: this,
            success: function (response) {
                var responseText = Ext.decode(response.responseText);
                if(responseText.success==true){
                    if(ids.length == 1){
                        location.href = '/electronic/electronics/download/' + this.entrytype+ '/' + ids[0];
                    }else{
                        location.href = '/electronic/electronics/downloads/' + this.entrytype + '/'+ idsStr;
                    }
                }else{
                    XD.msg('下载失败！'+responseText.msg);
                    return;
                }
            }
        });
    },

    del: function () {
        var eleTree = this.down('treepanel');
        var records = eleTree.getView().getChecked();
        var allEleids = this.getEleids().split(',');
        var view = this;
        if (records.length == 0) {
            XD.msg('请至少选择一条需要删除的数据');
            return;
        }
        XD.confirm('确定要删除选定的数据吗', function () {
            var deleteEleids = [];
            for (var i = 0; i < records.length; i++) {
                deleteEleids.push(records[i].get('fnid'));
            }
            Ext.Ajax.request({
                method: 'DELETE',
                url: '/electronic/electronics/' + this.entrytype + '/' + this.entryid + '/' + deleteEleids,
                scope: this,
                success: function (response) {
                    var responseText = Ext.decode(response.responseText);
                    XD.msg(responseText.msg);
                    for (var i = 0; i < deleteEleids.length; i++) {
                        for (var j = 0; j < allEleids.length; j++) {
                            if (allEleids[j] == deleteEleids[i]) {
                                allEleids.splice(j, 1);
                                j = j - 1;
                            }
                        }
                    }
                    if(allEleids.length==0){
                        window.remainEleids = 'undefined';
                    } else{
                        window.remainEleids = allEleids;
                    }
                    eleTree.getStore().reload();

                    // 原始文件被删除后刷新利用文件列表
                    var solidTree = null;
                    if (this.findParentByType('acquisitionform') != 'undefined') {
                        solidTree = this.findParentByType('acquisitionform').down('solid').down('treepanel');
                    } else {
                        solidTree = this.findParentByType('managementform').down('solid').down('treepanel');
                    }
                    solidTree.getStore().reload();
                    var allMediaFrame = document.querySelectorAll('#mediaFrame');
                    if(allMediaFrame){
                        for (var i = 0; i < allMediaFrame.length; i++) {
                            allMediaFrame[i].setAttribute('src','');
                        }
                    }
                }
            })
        }, this);
    },
    moveup: function () {
        var eleTree = this.down('treepanel');
        var records = eleTree.getView().getChecked();
        var allEleids = this.getEleids().split(',');
        if (records.length != 1) {
            XD.msg('请选择一条需要移动的数据');
            return;
        }

        var record = records[0];
        var parentNode = record.parentNode;
        var length = parentNode.childNodes.length;
        var currentIndex = 0;

        //获取当前节点
        for(var i =0; i < length; i++){
            if(parentNode.childNodes[i] === record){
                currentIndex = i;
            }
        }

        //操作
        if(currentIndex <=0){
            currentIndex = 1;
        }else{
            currentIndex = currentIndex - 1;
        }

        parentNode.insertChild(currentIndex,record);
        if(!this.timer){
            clearTimeout(this.timer);//清除计时器
        }
        var that = this;
        this.timer = setTimeout(function () {
            that.changeSort(parentNode.childNodes);
        },500);
    },

    movedown: function () {
        var eleTree = this.down('treepanel');
        var records = eleTree.getView().getChecked();
        var allEleids = this.getEleids().split(',');
        if (records.length != 1) {
            XD.msg('请选择一条需要移动的数据');
            return;
        }

        var record = records[0];
        //当前节点父节点
        var parentNode = record.parentNode;
        var length = parentNode.childNodes.length;
        var currentIndex= 0;

        // 获取当前节点的索引
        for(var i = 0; i < length; i++){
            if(parentNode.childNodes[i] === record){
                currentIndex = i;
            }
        }

        // 计算移除当前节点后，插入的目标节点索引
        var targetIndex = (currentIndex>=(length-2))? (length-1) : currentIndex+1;

        // 删除当前节点
        parentNode.childNodes[currentIndex].remove();

        // 插入当前节点，至计算的目标位置
        parentNode.insertChild(targetIndex,record);

        if(!this.timer){
            clearTimeout(this.timer);//清除计时器
        }
        var that = this;
        this.timer = setTimeout(function () {
            that.changeSort(parentNode.childNodes);
        },500);

    },

    changeSort : function(nodes){
        var eleids = [];
        for(var i in nodes){
            eleids[i] = nodes[i].data.fnid;
        }

        Ext.Ajax.request({
            params: {
                eleids:eleids,
                entryType:this.entrytype
            },
            url: '/electronic/mediaFileSort',
            method: 'POST',
            scope: this,
            success: function (response) {
            },
            failure:function(response){
                XD.msg('操作失败');
            }
        });
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
            var eleView = win.eleview;
            var formview ;
            if(eleView.findParentByType("acquisitionform")){
                formview = eleView.findParentByType("acquisitionform");
            }else if(eleView.findParentByType("managementform")){
                formview = eleView.findParentByType("managementform");
            }else if(eleView.findParentByType("AuditFormView")){
                formview = eleView.findParentByType("AuditFormView");
            }else if(eleView.findParentByType("duplicateCheckingEntryView")){
                formview = eleView.findParentByType("duplicateCheckingEntryView");
            }
            var dynamicform = formview.down('dynamicform');
            //初始化文件上传组件
            win.uploader = WebUploader.create({
                // swf文件路径
                swf: '/js/Uploader.swf',
                // 文件接收服务端。
                server: typeof(win.entryid) == 'undefined'?'/electronic/serelectronics/'+win.entrytype:'/electronic/serelectronics/'+win.entrytype+"/"+win.entryid,
                // 选择文件的按钮。可选。
                // 内部根据当前运行是创建，可能是input元素，也可能是flash.
                pick: {id: '#picker', label: '选择文件'},
                //是否要分片处理大文件上传(断点续传)
                chunked: true,
                //大图片是否进行压缩
                compress:false,
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
                var repeatIdAndNames = [];
                var records = win.treepanel.getStore().getRoot().childNodes;
                for (var i = 0; i < records.length; i++) {
                    treeFileNames.push(records[i].data.text);
                }
                if(dynamicform.operate=='modify'){
                    for (var i = 0; i < files.length; i++) {
                        win.down('grid').getStore().add({
                            id: files[i].id,
                            name: files[i].name,
                            size: Math.floor(files[i].size / 10240) / 100,
                            progress: 0
                        });
                        if($.inArray(files[i].name,treeFileNames)!=-1){
                            var idAndName = [];
                            idAndName['id'] = files[i].id;
                            idAndName['name'] = files[i].name;
                            repeatIdAndNames.push(idAndName);
                            msgs += "'"+files[i].name +"'"+ "文件已存在<br>";
                        }
                    }
                    if (msgs.length>0) {
                        var notes = '';
                        var html = '<img class="x-box-icon" title = "帮助与支持" src="../img/icon/help_icon.png" onclick= XD.openHelp(' + notes + ')>' + "存在同名文件，是否确定要保存版本"  ;
                        Ext.MessageBox.confirm('确认信息',html, function (btn) {
                            if (btn == 'yes') {
                                win.uploader.upload();//直接保存版本
                            }
                            if (btn == 'no') {
                                var grid = win.down('grid');
                                var store = grid.getStore();
                                var removeSelect = [];
                                for(var i=0;i<store.getCount();i++){
                                    for(var j=0;j<repeatIdAndNames.length;j++){
                                        if(store.getAt(i).get('id')==repeatIdAndNames[j].id){
                                            removeSelect.push(store.getAt(i));
                                            break;
                                        }
                                    }
                                }
                                for(var i=0;i<removeSelect.length;i++){
                                    //从上传队列中删除文件
                                    var uploader = grid.findParentByType('uploadview').uploader;
                                    uploader.removeFile(removeSelect[i].get('id'), true);
                                    //从列表中删除文件
                                    grid.getStore().remove(removeSelect[i]);
                                    //清除服务器上的缓存
                                    Ext.Ajax.request({
                                        method: 'DELETE',
                                        url: '/electronic/chunk/' + removeSelect[i].get('name') + '/'
                                    });
                                }
                            }
                        }, this);
                        Ext.MessageBox.setIcon('');
                    }
                }else{
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
                    url: '/electronic/electronics/' + win.entrytype + (typeof(win.entryid) == 'undefined' ? '' : '/' + win.entryid) + '/' + encodeURIComponent(file.name) + '/',
                    success: function (response, opts) {
                        var data = Ext.decode(response.responseText).data;
                        if (!win.destroyed) {
                            var grid = win.down('grid');
                            var record = grid.getStore().getById(file.id);
                            if(grid.getView().getRow(record)){
                                grid.getView().getRow(record).style.backgroundColor = '#87CEFA';
                            }
                            record.set('eleid', data.eleid);  //电子文件id
                        }
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