/**
 * Created by Rong on 2017/11/15.
 */
Ext.define('FocusImg.view.ElectronicView',{
    entryid:'',     //条目主键ID
    entrytype:'',   //数据类型（采集、管理、利用）

    extend:'Ext.panel.Panel',
    xtype:'electronic',
    layout: 'border',
    bodyBorder: false,
    defaults: {
        split: true
    },
    items: [{
        region: 'west',
        width: 350,
        xtype: 'treepanel',
        header:false,
        hideHeaders: true,
        store: {
            extend: 'Ext.data.TreeStore',
            proxy: {
                type: 'ajax',
                url: '/electronic/electronicsFocusTree',
                reader: {
                    type: 'json',
                    expanded: false
                }
            },
            root: {
                text: '焦点图列表',
                checked: false
            }
        },
        autoScroll: true,
        rootVisible:true,
        checkPropagation: 'both',
        dockedItems: [{
            xtype: 'toolbar',
            dock: 'top',
            items: [
                {xtype: 'button', text:'上传',handler:function(){
                    var view = this.findParentByType('electronic');
                    var win = Ext.create('Comps.view.UploadView',{
                        entrytype:view.entrytype,
                        entryid:view.entryid
                    });
                    win.on('close',function(){
                        if(typeof(this.entryid) == 'undefined'){
                            var store = win.down('grid').getStore();
                            for(var i=0;i<store.getCount();i++){
                                var data = store.getAt(i).data;
                                if(data.progress == 1){
                                    this.down('treepanel').getRootNode().appendChild({
                                        fnid:data.eleid,
                                        text:data.name,
                                        checked:false,
                                        leaf:true
                                    });
                                }
                            }
                        }else{
                            this.down('treepanel').getStore().reload();
                        }
                    },view);
                    win.show();
                }},
                {xtype: 'button', text:'下载',handler:function(){
                    var view = this.findParentByType('electronic');
                    var records = view.down('treepanel').getView().getChecked();
                    view.download(records);
                }},
                {xtype: 'button', text:'全部下载',handler:function(){
                    var view = this.findParentByType('electronic');
                    var records = view.down('treepanel').getStore().getRoot().childNodes;
                    view.download(records);
                }},
                {xtype: 'button', text:'删除', handler:function(){
                    var view = this.findParentByType('electronic');
                    view.del();
                }},
                {xtype: 'button', text:'上移', handler:function(){
                    var view = this.findParentByType('electronic');
                    view.TakeUp();
                }},
                {xtype: 'button', text:'下移', handler:function(){
                    var view = this.findParentByType('electronic');
                    view.TakeDown();
                }}
            ]
        }],
        listeners:{
            beforeload : function(){
                var view = this.findParentByType('electronic');
                this.getStore().proxy.url = '/electronic/electronicsFocusTree';
            },
            afterrender : function() {
                this.expandAll();
            },
            itemclick:function(view, record, item, index, e, eOpts){
                if(!record.get('leaf'))
                    return;
                if(e.getTarget('.x-tree-checkbox',1,true)){
                    return;
                }
                var mediaFrame = document.getElementById('mediaFrame');
                var view = this.findParentByType('electronic');
                var filename = record.get('text');
                mediaFrame.setAttribute('src','/electronic/media?eleid='+record.get('fnid')+'&filetype='+filename.substring(filename.lastIndexOf('.')+1));
                //alert(view.entrytype + '/'+ view.entryid);
            }
        }
    },{
        region: 'center',
        layout:'border',
        items:[
            {
                region: 'center',
                width:'100%',
                height:'100%',
                html:'<iframe id="mediaFrame" src=""  width="100%" height="100%" style="border:0px;"></iframe>'
            }
        ]
    }],

    initData:function(entryid){
        this.entryid = entryid;
        this.down('treepanel').getStore().reload();
    },

    getEleids:function(){
        var ids = [];
        var records = this.down('treepanel').getStore().getRoot().childNodes;
        for(var i=0;i<records.length;i++){
            ids.push(records[i].data.fnid);
        }
        return ids.join(",");
    },

    download:function(records) {
        for(var i=0;i<records.length;i++){
            setTimeout(function(entrytype, eleid){
                var downloadForm = document.createElement("form");
                document.body.appendChild(downloadForm);
                downloadForm.action = '/electronic/electronics/download/' + entrytype + '/' + eleid;
                downloadForm.submit();
            }, i*300, this.entrytype, records[i].data.fnid);
        }
    },

    del:function(){
        var records = this.down('treepanel').getView().getChecked();
        if (records.length == 0) {
            XD.msg('请至少选择一条需要删除的数据');
            return;
        }
        Ext.MessageBox.confirm('确认信息', '确定要删除这' + records.length + '条数据吗?', function (btn) {
            if (btn == 'yes') {
                var eleids = '';
                for (var i = 0; i < records.length; i++) {
                    if (i == 0) {
                        eleids += records[i].data.fnid;
                    } else {
                        eleids += ',' + records[i].data.fnid;
                    }
                }

                Ext.Ajax.request({
                    method: 'DELETE',
                    url:'/electronic/electronicsFocusDel?eleids='+eleids,
                    scope:this,
                    success: function (response) {
                        XD.msg(Ext.decode(response.responseText).msg);
                        this.down('treepanel').getStore().reload();
                        var mediaFrame = document.getElementById('mediaFrame');
                        mediaFrame.setAttribute('src','');
                    }
                })
            }
        }, this);
    },
    TakeUp:function () {
        var eleTree = this.down('treepanel');
        var records = eleTree.getView().getChecked();
        var allEleids = this.getEleids().split(',');
        if(records.length!=1){
            XD.msg('请选择一条需要移动的数据');
            return;
        }
        var record = records[0];
        var parentNode = record.parentNode;
        var length = parentNode.childNodes.length;
        var currentCount = 0;

        //获取当前节点
        for(var i=0;i<length;i++){
            if(parentNode.childNodes[i]===record){
                currentCount = i;
            }
        }
        if(currentCount<=0){
            XD.msg('当前数据是第一条，无法上移');
            return;
        }else{
            currentCount = currentCount-1;
        }
        //上移
        parentNode.insertChild(currentCount,record);
        if(!this.timer){
            clearTimeout(this.timer);//清楚计时器
        }
        var that = this;
        this.timer = setTimeout(function () {
            that.ChangeSort(parentNode.childNodes);
        },500);

    },
    TakeDown:function () {
        var eleTree = this.down('treepanel');
        var records = eleTree.getView().getChecked();
        var allEleids = this.getEleids().split(',');
        if(records.length!=1){
            XD.msg('请选择一条需要移动的数据');
            return;
        }
        var record = records[0];
        var parentNode = record.parentNode;
        var length = parentNode.childNodes.length;
        var currentCount = 0;
        var currentTagert;

        //获取当前节点
        for(var i=0;i<length;i++){
            if(parentNode.childNodes[i]===record){
                currentCount = i;
            }
        }
        if(currentCount>=length-1){
            XD.msg('当前数据是最后一条，无法下移');
            return;
        }else{
            currentTagert = currentCount+1;
        }
        // 删除当前节点
        parentNode.childNodes[currentCount].remove();
        //下移
        parentNode.insertChild(currentTagert,record);
        if(!this.timer){
            clearTimeout(this.timer);//清楚计时器
        }
        var that = this;
        this.timer = setTimeout(function () {
            that.ChangeSort(parentNode.childNodes);
        },500);

    },
    ChangeSort:function (nodes) {
        var eleids=[];
        for(var i in nodes){
            eleids[i] = nodes[i].data.fnid;
        }
        Ext.Ajax.request({
            url: '/electronic/electronicsFocusChange?eleids='+eleids,
            method:'POST',
            scope:this,
            success: function (response) {
            },
            failure:function(response){
                XD.msg('操作失败');
            }
        });
    }

});

//文件上传弹出框
Ext.define('Comps.view.UploadView',{
    extend:'Ext.window.Window',
    xtype:'uploadview',
    uploader:null,
    modal:true,
    width:800,
    height:400,
    title:'文件上传',
    layout:'fit',
    closeToolText:'关闭',
    actions:{
        del:{
            iconCls:'x-action-upload-delete-icon',
            tooltip:'删除',
            handler:function(view,row){
                var grid = view.grid;
                var record = grid.getStore().getAt(row);
                //从上传队列中删除文件
                var uploader = grid.findParentByType('uploadview').uploader;
                uploader.removeFile(record.get('id'), true);
                //从列表中删除文件
                grid.getStore().remove(record);
                //清除服务器上的缓存
                Ext.Ajax.request({
                    method:'DELETE',
                    url:'/electronic/chunk/'+record.get('name')+'/'
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
    items:{
        xtype:'grid',
        store:[],
        border:false,
        scrollable:true,
        columns:[{ xtype: 'rownumberer' },{hidden:true,dataIndex:'id'},{
            header:'文件名称',
            dataIndex:'name',
            flex:1
        },{
            text:'文件大小',
            dataIndex:'size',
            width:80,
            renderer:function(value){ return value + 'MB'; }
        },{
            text:'上传进度',
            xtype:'widgetcolumn',
            width:150,
            widget : {
                bind:'{record.progress}',
                xtype: 'progressbarwidget',
                textTpl:['{percent:number("1")}%']
            }
        },{
            xtype:'actioncolumn',
            width:30,
            items:['@del']
        }],
        tbar:[{
            xtype:'displayfield',
            id:'picker',
            width:82,
            height:37
        },{
            xtype:'button',
            text:'开始上传',
            width:90,
            height:37,
            handler:function(){
                var win = this.findParentByType('uploadview');
                win.uploader.upload();
            }
        }]
    },
    listeners:{
        render:function(win){
            //注册分片上传处理事件，用于断点续传
            WebUploader.Uploader.register({
                name:'electronic',
                'before-send': 'checkchunk'
            }, {
                checkchunk: function( block ) {
                    var deferred = WebUploader.Deferred();
                    Ext.Ajax.request({
                        url:'/electronic/chunk/'+block.file.name+'/'+block.chunks+'/'+block.chunk,
                        aysnc:false,
                        success:function(response,opts){
                            if(response.responseText == "true"){
                                deferred.reject();
                            }else{
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
                server: '/electronic/electronicsFocus',
                // 选择文件的按钮。可选。
                // 内部根据当前运行是创建，可能是input元素，也可能是flash.
                pick: { id:'#picker', label:'选择文件'},
                //是否要分片处理大文件上传(断点续传)
                chunked: true,
                //文件分片大小，5M
                chunkSize:5242880,
                //某个分片由于网络问题出错，自动重传次数
                chunkRetry:3,
                //上传并发数
                threads:3,
                //单文件大小限制，500M
                fileSingleSizeLimit:524288000,
                accept:{
                    title:'Images',
                    extensions:'jpg,jpeg,png',
                    mineTypes:'image/*'
                }

            });
            //监听文件选择事件，将选中的文件信息添加到列表中
            win.uploader.on('filesQueued',function(files){
                for(var i = 0;i < files.length;i++) {
                    win.down('grid').getStore().add({
                        id: files[i].id,
                        name: files[i].name,
                        size: Math.floor(files[i].size / 10240) / 100,
                        progress: 0
                    });
                }
            });
            //监听文件上传进度，更新列表中上传进度条
            win.uploader.on('uploadProgress',function(file,progress){
                if(!win.destroyed){
                    var record = win.down('grid').getStore().getById(file.id);
                    record.set('progress',progress);
                }
            });
            //监听文件上传成功，提示用户
            win.uploader.on('uploadSuccess',function(file,response){
                if(!win.destroyed){
                    var grid = win.down('grid');
                    var record = grid.getStore().getById(file.id);
                    grid.getView().getRow(record).style.backgroundColor='#87CEFA';
                    record.set('eleid',file.id);
                }
            });
            //监听所有文件上传完毕，提示用户
            win.uploader.on('uploadFinished',function(){
                XD.msg('所有文件上传完毕');
            });
        },
        beforeclose:function(win){
            win.uploader.stop(true);
            win.uploader.reset();
            win.uploader.destroy();
            WebUploader.Uploader.unRegister('electronic');
        }
    }
});