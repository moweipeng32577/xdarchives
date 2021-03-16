/**
 * Created by Rong on 2017/11/15.
 */
Ext.define('Comps.view.ElectronicView', {
    entryid: '',     //条目主键ID
    entrytype: '',   //数据类型（采集、管理、利用）
    timer:null,     //上移下移定时器
    extend: 'Ext.panel.Panel',
    xtype: 'electronic',
    layout: 'border',
    operateFlag:'',
    bodyBorder: false,
    elesId:[],//记录电子文件id数组
    defaults: {
        split: true
    },
    items: [{
        region: 'west',
        width: 400,
        xtype: 'treepanel',
        header: false,
        hideHeaders: true,
        store: {
            extend: 'Ext.data.TreeStore',
            autoLoad:false,
            proxy: {
                type: 'ajax',
                url: '/electronic/electronics/tree/',
                reader: {
                    type: 'json',
                    expanded: false
                }
            },
            root: {
                text: '原文列表',
                checked: false
            }
        },
        autoScroll: true,
        rootVisible: true,
        checkPropagation: 'both',
        dockedItems: [{
            xtype: 'toolbar',
            overflowHandler: 'scroller',
            dock: 'top',
                items: [{
                    xtype: 'button', text: '上传', handler: function () {
                        var view = this.findParentByType('electronic');
                        var win = Ext.create('Comps.view.UploadView', {
                            entrytype: view.entrytype,
                            entryid: view.entryid,
                            treepanel: view.down('treepanel'),
                            eleview:view
                        });
                        window.UploadView = win;
                        win.on('close', function () {
                            var sortEleName = win.down('[itemId=sortEleNameId]').getValue();
                            if (typeof(this.entryid) == 'undefined') {
                                // var treeNodesNum = this.down('treepanel').getStore().getRoot().childNodes.length;//树节点的文件数量
                                // var uploadNum = 0;
                                var store = win.down('grid').getStore();
                                for (var i = 0; i < store.getCount(); i++) {
                                    var data = store.getAt(i).data;
                                    this.getEleidsNoEntryid(data.eleid);
                                    if (data.progress == 1) {
                                        // this.down('treepanel').getRootNode().appendChild({
                                        //     fnid: data.eleid,
                                        //     text: data.name,
                                        //     checked: false,
                                        //     leaf: true
                                        // });
                                    }
                                }
                                if(sortEleName){  //勾选按文件名排序
                                    this.updateSortElectronics();
                                }
                                var store1 = this.down('treepanel').getStore();
                                window.remainEleids = this.getEleidsNoEntryid() == "" ? 'undefined' : this.getEleidsNoEntryid();
                                store1.load();
                            } else {
                                if(sortEleName){  //勾选按文件名排序
                                    this.updateSortElectronics();
                                }
                                this.down('treepanel').getStore().reload();
                            }
                        }, view);
                        win.show();
                    }
                }, {
                    xtype: 'button', text: '下载', handler: function () {
                        var view = this.findParentByType('electronic');
                        var treeview = view.down('treepanel');
                        var records = treeview.getView().getChecked();
                        if (records.length == 0) {
                            XD.msg('未勾选下载文件');
                            return;
                        }
                        if (treeview.getView().getStore().data.length == records.length) {
                            records.splice(0, 1);
                        }
                        view.download(records);
                    }
                }, {
                    xtype: 'button', text: '全部下载', handler: function () {
                        var view = this.findParentByType('electronic');
                        var records = view.down('treepanel').getStore().getRoot().childNodes;
                        if (records.length == 0) {
                            XD.msg('没有可下载文件');
                            return;
                        }
                        view.download(records);
                    }
                }, {
                    xtype: 'button', text: '删除', handler: function () {
                        var view = this.findParentByType('electronic');
                        view.del();
                    }
                }, {
                    xtype: 'button', text: '上移', handler: function () {
                        var view = this.findParentByType('electronic');
                        view.moveup();
                    }
                }, {
                    xtype: 'button', text: '下移', handler: function () {
                        var view = this.findParentByType('electronic');
                        view.movedown();
                    }
                }, {
                    xtype: 'button', text: '打印',itemId:'print',
                    handler: function () {
                        var view = this.findParentByType('electronic');
                        var treeview = view.down('treepanel');
                        var records = treeview.getView().getChecked();
                        var texts = [];
                        var eleid = [];
                        var fType = 0;
                        for (var i = 0; i < records.length; i++) {
                            var t = records[i].get('text');
                            if (records[i].get('fnid')) {//过滤文件夹
                                texts.push(t);
                                eleid.push(records[i].get('fnid'));
                                if (t.toLowerCase().lastIndexOf(".png") > 0 || t.toLowerCase().lastIndexOf(".jpg") > 0 || t.toLowerCase().lastIndexOf(".jpeg") > 0 || t.toLowerCase().lastIndexOf(".bmp") > 0) {
                                    fType = 1;
                                }
                                else if (t.toLowerCase().lastIndexOf(".pdf") > 0) {
                                    fType = 0;
                                }
                                else {
                                    XD.msg('暂时只支持常用图片和PDF格式的电子文件打印！');
                                    return;
                                }
                            }
                        }
                        if (texts.length != 1) {
                            XD.msg('请选择1条数据进行操作！');
                            return;
                        }
                        var filename = texts[0];
                        var url = '/electronic/media?entrytype=' + view.entrytype + '&eleid=' + eleid[0] + '&filename=' + filename + '&filetype=' + filename.substring(filename.lastIndexOf('.') + 1);
                        var newurl = encodeURI(url);

                        if (document.getElementById("print-iframe")) {
                            document.getElementById("print-iframe").parentNode.removeChild(document.getElementById("print-iframe"));
                        }

                        //iframe代表主页面的dom元素，iframe.contentWindow代表iframe这个页面的window对象
                        var iframe = document.createElement('IFRAME');
                        iframe.setAttribute("id", "print-iframe");
                        iframe.setAttribute('style', 'position:absolute;width:"100%";height:100%;left:-500px;top:-500px;');
                        iframe.setAttribute('src', newurl);
                        document.body.appendChild(iframe);
                        iframe.contentWindow.focus();
                        iframe.onload = function () {
                            var _this = this;
                            if (fType == 1) {
                                setTimeout(function () {
                                    var iframe = _this;
                                    var img = iframe.contentDocument.getElementById('photo_img');
                                    var width = img.scrollWidth;
                                    var height = img.scrollHeight;
                                    img.setAttribute('style', 'width:100%;height:100%;display:block;')//铺满整个页面
                                    iframe.contentDocument.body.innerHTML=img.outerHTML;
                                    //横向打印
                                    if (parseInt(width) > parseInt(height)) {
                                        var style = document.createElement('style');
                                        style.setAttribute("type", "text/css");
                                        style.setAttribute("media", "print");
                                        style.innerHTML = '@page { size: landscape; }';
                                        iframe.contentDocument.head.appendChild(style);
                                    }
                                    iframe.contentWindow.print();
                                }, 200);
                            } else {
                                var iframe = document.getElementById("print-iframe");
                                iframe.setAttribute('src',  '/electronic/printYWMedia?entrytype='+view.entrytype+'&btnType=YW&eleid='+eleid[0]);
                                iframe.onload=function () {
                                    iframe.contentWindow.print();
                                };
                            }
                        }
                    }
                    },{
                        xtype: 'button', text: '批量打印',itemId:'batchprint',
                        handler: function () {
                            var view = this.findParentByType('electronic');
                            var treeview = view.down('treepanel');
                            var records = treeview.getView().getChecked();
                            var eleid = [];
                            for (var i = 0; i < records.length; i++) {
                                var t = records[i].get('text');
                                if (records[i].get('fnid')) {//过滤文件夹
                                    eleid.push(records[i].get('fnid'));
                                    if (t.toLowerCase().lastIndexOf(".png") > 0 || t.toLowerCase().lastIndexOf(".jpg") > 0 || t.toLowerCase().lastIndexOf(".jpeg") > 0 || t.toLowerCase().lastIndexOf(".pdf") > 0) {
                                        continue;
                                    }
                                    else {
                                        XD.msg('暂时只支持常用图片和pdf文件批量打印！');
                                        return;
                                    }
                                }
                            }

                            if (eleid.length < 1) {
                                XD.msg('请选择数据！');
                                return;
                            }
                            if (document.getElementById("print-iframe")) {
                                document.getElementById("print-iframe").parentNode.removeChild(document.getElementById("print-iframe"));
                            }

                            var url = '/electronic/batchprint?datatype=' + view.entrytype + '&eleid=' + eleid ;
                            var newurl = encodeURI(url);
                            var iframe = document.createElement('IFRAME');
                            iframe.setAttribute("id", "print-iframe");
                            iframe.setAttribute('style', 'position:absolute;width:"100%";height:100%;left:-500px;top:-500px;');
                            iframe.setAttribute('src', newurl);
                            document.body.appendChild(iframe);
                            Ext.MessageBox.wait('正在处理中...请稍后....','提示');
                            iframe.contentWindow.focus();
                            iframe.onload = function () {
                                // iframe.contentWindow.origin  = window.location.protocol + "//" + "localhost" + (window.location.port ? ':' + window.location.port: '');
                                iframe.contentWindow.print();
                                Ext.MessageBox.hide();
                            };
                        }
                },{
                    xtype: 'button', text: '查看历史版本', itemId: 'getEleVersion', hidden: true
                }]
        }],
        buttons:{
            xtype:'label',
            itemId:'etips',
            hidden: true,
            html:' <i class="fa fa-info-circle"></i>  温馨提示：支持常用图片和PDF格式的电子文件预览，其它格式可下载到本地查看',
            style:{color:'red','padding-left':'1em'}
        },
        listeners: {
            beforeload: function () {
                var view = this.findParentByType('electronic');
                if (window.dataSourceType=="capture"){
                    view.entrytype="capture";
                }
                this.getStore().proxy.url = '/electronic/electronics/tree/' + view.entrytype + '/' + view.entryid + '/'+window.remainEleids;
            },
            afterrender: function () {
                this.expandAll();
            },
            itemclick: function ( view, record, item, index, e, eOpts )  {
                if (!record.get('leaf')){
                    return;
                }
                // if(e.getTarget('.x-tree-checkbox',1,true)){
                //     return;
                // }
                var view = this.findParentByType('electronic');
                // var mediaFrame = document.getElementById('mediaFrame');
                //当采集、管理模块在未归已归、案卷、卷内点击著录或修改时，会创建多个相同ID的iframe
                //document.getElementById只会拿第一个，导致下面的src对应不了正确显示的那个iframe
                var allMediaFrame = document.querySelectorAll('#mediaFrame');
                var mediaFrame;
                //创建electronicview需要指定是著录还是修改类型，经调试，著录的iframe是第一个，修改的是最后一个
                if (allMediaFrame.length > 0 && view.operateFlag == 'add') {
                    mediaFrame = allMediaFrame[allMediaFrame.length - 1];
                } else {
                    mediaFrame = allMediaFrame[0];
                }
                var filename = record.get('text');
                if (view.isJy) {
                    mediaFrame.setAttribute('src', '/electronic/jyMedia?entrytype=' + view.entrytype + '&eleid=' + record.get('fnid') + '&filetype=' + filename.substring(filename.lastIndexOf('.') + 1));
                } else {
                    mediaFrame.setAttribute('src', '/electronic/media?entrytype=' + view.entrytype + '&eleid=' + record.get('fnid') + '&filetype=' + filename.substring(filename.lastIndexOf('.') + 1));
                }
            },
            select:function (view, record, item, index, e) {
                if (!record.get('leaf')){
                    return;
                }
                // if(e.getTarget('.x-tree-checkbox',1,true)){
                //     return;
                // }
                var view = this.findParentByType('electronic');
                // var mediaFrame = document.getElementById('mediaFrame');
                //当采集、管理模块在未归已归、案卷、卷内点击著录或修改时，会创建多个相同ID的iframe
                //document.getElementById只会拿第一个，导致下面的src对应不了正确显示的那个iframe
                var allMediaFrame = document.querySelectorAll('#mediaFrame');
                var mediaFrame;
                //创建electronicview需要指定是著录还是修改类型，经调试，著录的iframe是第一个，修改的是最后一个
                if (allMediaFrame.length > 0 && view.operateFlag == 'add') {
                    mediaFrame = allMediaFrame[allMediaFrame.length - 1];
                } else {
                    mediaFrame = allMediaFrame[0];
                }
                var filename = record.get('text');
                var subname = filename.substring(filename.lastIndexOf('.') + 1);
                var islook = ['png','jpg','pdf','jpeg','img'];
                if (view.isJy) {
                    if(islook.indexOf(subname)!=-1) {
                        document.getElementById("loadingDiv").style.display="block";
                        mediaFrame.setAttribute('src', '/electronic/jyMedia?entrytype=' + view.entrytype + '&eleid=' + record.get('fnid') + '&filetype=' + filename.substring(filename.lastIndexOf('.') + 1));
                    }
                } else {
                    if(islook.indexOf(subname)!=-1) {
                        document.getElementById("loadingDiv").style.display="block";
                        mediaFrame.setAttribute('src', '/electronic/media?entrytype=' + view.entrytype + '&eleid=' + record.get('fnid') + '&filetype=' + filename.substring(filename.lastIndexOf('.') + 1));
                    }
                }
                //加载结束--隐藏loading.gif
                if (mediaFrame.attachEvent) {
                    mediaFrame.attachEvent("onload", function() {
                        document.getElementById("loadingDiv").style.display="none";
                    });
                } else {
                    mediaFrame.onload = function() {
                        document.getElementById("loadingDiv").style.display="none";
                    };
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
                html: '<div id="loadingDiv" style="display: none; "><div id="over" style=" position: absolute;top: 0;left: 0; width: 100%;height: 100%; background-color: #f5f5f5;opacity:0.5;z-index: 1000;"></div><div id="layout" style="position: absolute;top: 10%; left: 35%;width: 10%; height: 10%;  z-index: 1001;text-align:center;"><img src="../img/Picloading.gif" /></div></div>'+
                      '<iframe id="mediaFrame" src=""  width="100%" height="100%" style="border:0px;"></iframe>'

            }
        ]
    }],
    getEleidsNoEntryid:function(eleid){//无entryid时使用的
        if (eleid){
            Ext.Array.push(this.elesId,eleid);
        }
        return this.elesId;
    },

    updateSortElectronics:function() {//上传完修改电子文件排序号
        Ext.Ajax.request({
            params: {
                entrytype: this.entrytype,
                entryid: this.entryid,
                remainEleids: this.getEleidsNoEntryid() == "" ? 'undefined' : this.getEleidsNoEntryid()
            },
            url: '/electronic/updateSortElectronics',
            method: 'POST',
            sync: false,
            success: function (resp) {
            },
            failure: function () {
                XD.msg('按文件名排序失败');
            }
        });
    },
    initData: function (entryid) {
        this.entryid = entryid;
        window.remainEleids = 'undefined';
        var treeStore = this.down('treepanel').getStore();
        if(typeof(entryid) == 'undefined'){
            this.elesId = new Array();
            this.down('treepanel').getRootNode().removeAll();
            return;
        }
        var treeNode = this.down('treepanel')
        Ext.defer(function () {
            treeStore.load({
                callback:function(r,options,success){
                    if(r!=null)
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
            if(mediaFrame){
                mediaFrame.setAttribute('src', '');
            }
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
        Ext.MessageBox.wait('正在下载请稍后...', '提示');
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
                    Ext.MessageBox.hide();
                    XD.msg('下载失败！'+responseText.msg);
                    return;
                }
                Ext.MessageBox.hide();
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
        },{
            xtype: 'checkbox',
            itemId:'sortEleNameId',
            margin:'10 10 0 10',
            inputValue : true,
            boxLabel: '按文件名排序',
            checked:true
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
            WebUploader.Uploader.unRegister('electronic');
        }
    }
});