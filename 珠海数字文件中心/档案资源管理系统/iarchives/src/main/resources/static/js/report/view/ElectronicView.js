/**
 * Created by RonJiang on 2018/03/06
 */
Ext.define('Report.view.ElectronicView', {
    extend: 'Ext.panel.Panel',
    xtype: 'electronicRep',
    layout: 'border',
    bodyBorder: false,
    defaults: {
        split: true
    },
    items: [{
        region: 'center',
        width: 350,
        xtype: 'treepanel',
        itemId:'reportFileTreeId',
        header: false,
        hideHeaders: true,
        store: {
            extend: 'Ext.data.TreeStore',
            proxy: {
                type: 'ajax',
                url: '/report/reports/tree/',
                reader: {
                    type: 'json',
                    expanded: false
                }
            },
            root: {
                text: '报表样式文件'
                ,expanded: true
                // ,checked: false
            }
        },
        autoScroll: true,
        rootVisible: true,
        checkPropagation: 'both',
        dockedItems: [{
            xtype: 'toolbar',
            dock: 'top',
            items: [
                {xtype: 'button', text: '上传', handler: function () {
                    var view = this.findParentByType('electronicRep');
                    var records = view.down('treepanel').getView().getStore().data;
                    if (records.length > 1) {//除了根节点外还有其它数据
                        Ext.MessageBox.alert("提示", '报表样式文件已存在，如需重新上传样式文件，请先删除当前文件', function () {});
                        return;
                    }
                    var win = Ext.create('Report.view.ReportUploadView', {
                        reportid: view.reportid
                    });
                    win.show();
                    win.on('close', function () {
                        //在点击开始上传按钮时已经判断是否选择单个文件，此处暂时取消判断（若选择了一个以上的文件，但文件并未上传，也会弹出提示，不友好）
                        // var store = win.down('grid').getStore();
                        // var recordCount = store.getCount();
                        // if(recordCount>1){
                        //     XD.msg('仅允许上传一个报表样式文件');
                        //     return;
                        // }
                        var store = win.down('grid').getStore();
                        var eleid = store.getAt(0).data.eleid;
                        this.down('treepanel').getStore().proxy.extraParams = {reportid:view.reportid,eleid:eleid,xtType:window.xtType};
                        this.down('treepanel').getStore().reload();
                    }, view);
                }
            },{
                xtype: 'button', text: '下载', itemId:'download', handler: function () {
                    var view = this.findParentByType('electronicRep');
                    var records = view.down('treepanel').getView().getStore().data.items;
                    if (records.length == 1) {//除了根节点外没有其它数据
                        XD.msg('没有可下载的报表样式文件');
                        return;
                    }
                    view.download(records[1]);
                }
            },{
                xtype: 'button', text: '删除', handler: function () {
                    var view = this.findParentByType('electronicRep');
                    var records = view.down('treepanel').getView().getStore().data.items;
                    if (records.length == 1) {//除了根节点外没有其它数据
                        XD.msg('没有可删除的报表样式文件');
                        return;
                    }
                    view.del(records[1]);
                }
            }]
        }],
        listeners: {
            beforerender: function () {
                var view = this.findParentByType('electronicRep');
                this.getStore().proxy.extraParams = {reportid:view.reportid};
            },
            afterrender: function () {
                this.expandAll();
            }
            // ,itemclick: function (view, record, item, index, e, eOpts) {
            //     if (!record.get('leaf'))
            //         return;
            //     if(e.getTarget('.x-tree-checkbox',1,true)){
            //         return;
            //     }
            //     var mediaFrame = document.getElementById('mediaFrame');
            //     var view = this.findParentByType('electronicRep');
            //     var filename = record.get('text');
            //     mediaFrame.setAttribute('src', '/electronic/media?entrytype=management&eleid=' + record.get('fnid') + '&filetype=' + filename.substring(filename.lastIndexOf('.') + 1));
            // }
        }
    }
    // ,{
    //     region: 'east',
    //     layout: 'border',
    //     items: [
    //         {
    //             region: 'center',
    //             width: '100%',
    //             height: '100%',
    //             html: '<iframe id="mediaFrame" src=""  width="100%" height="100%" style="border:0px;"></iframe>'
    //         }
    //     ]
    // }
    ],

    getEleids: function () {
        var ids = [];
        var records = this.down('treepanel').getStore().getRoot().childNodes;
        for (var i = 0; i < records.length; i++) {
            ids.push(records[i].data.fnid);
        }
        return ids.join(",");
    },

    download: function (record) {
        Ext.Ajax.request({
            method: 'GET',
            url: '/report/ifFileExist/' + record.get('fnid'),
            scope: this,
            success: function (response) {
                var responseText = Ext.decode(response.responseText);
                if(responseText.success==true){
                    Ext.defer(function () {
                        location.href = '/report/reports/download/'+ record.get('fnid');
                        // var downloadForm = document.createElement('form');
                        // document.body.appendChild(downloadForm);
                        // downloadForm.action = '/report/reports/download/'+ record.get('fnid');
                        // downloadForm.submit();
                    }, 300);
                }else{
                    XD.msg('下载失败！<br />' + responseText.msg);
                    return;
                }
            }
        })
    },

    del: function (record) {
        XD.confirm('确定要删除报表样式文件吗', function () {
            Ext.Ajax.request({
                method: 'DELETE',
                url: '/report/deleteRepElectronic/' + record.get('fnid'),
                scope: this,
                success: function (response) {
                    var responseText = Ext.decode(response.responseText);
                    if(responseText.success==true){
                        XD.msg(responseText.msg);
                        this.down('treepanel').getStore().proxy.extraParams = {reportid:''};
                        this.down('treepanel').getStore().reload();
                        // var mediaFrame = document.getElementById('mediaFrame');
                        // mediaFrame.setAttribute('src', '');
                    }else{
                        XD.msg(responseText.msg);
                    }
                }
            })
        }, this);
    }
});

//文件上传弹出框
Ext.define('Report.view.ReportUploadView', {
    extend: 'Ext.window.Window',
    xtype: 'reportuploadview',
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
                var uploader = grid.findParentByType('reportuploadview').uploader;
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
                if(typeof(r.data.reportid) != 'undefined') {
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
        columns: [{xtype: 'rownumberer'}, {hidden: true, dataIndex: 'id'},
            {
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
                var win = this.findParentByType('reportuploadview');
                if (win.down('grid').getStore().getCount() == 0) {
                    XD.msg('未选择文件');
                    return;
                }
                if (win.down('grid').getStore().getCount() > 1) {
                    XD.msg('报表样式上传只能选择单个文件');
                    return;
                }
                var record = win.down('grid').getStore().data.items[0];
                var ifFileExistAtState;
                    Ext.Ajax.request({
                        method: 'GET',
                        url: '/report/ifFileExistAt/' + record.get('name').split('.')[0],
                        async: false,
                        success: function (response) {
                            var responseText = Ext.decode(response.responseText);
                            if (responseText.success == true) {
                                Ext.MessageBox.alert("提示", responseText.msg, function () {
                                });//文件已存在，提示报表所处节点及报表名称
                                return;
                            } else {
                                ifFileExistAtState = '文件不存在';
                            }
                        }
                    });
                if(!ifFileExistAtState){//文件已存在
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
                name: 'electronicRep',
                'before-send': 'checkchunk'
            }, {
                checkchunk: function (block) {
                    var deferred = WebUploader.Deferred();
                    Ext.Ajax.request({
                        url: '/electronic/chunk/' + block.file.name + '/' + block.chunks + '/' + block.chunk,
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
                server: '/report/electronicsReport',
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
                //单文件大小限制，50M
                fileSingleSizeLimit: 52428800,
                accept:{
                    title:'Sips',
                   // extensions:'cpt',
                 //   mineTypes:'cpt'
                }
            });
            //监听文件选择事件，将选中的文件信息添加到列表中
            win.uploader.on('filesQueued', function (files) {
                for (var i = 0; i < files.length; i++) {
                    win.down('grid').getStore().add({
                        id: files[i].id,
                        name: files[i].name,
                        size: Math.floor(files[i].size / 10240) / 100,
                        progress: 0
                    });
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
                    url: '/report/uploadReport/'+ (typeof(win.reportid) == 'undefined' || win.reportid=='' ? null : '/' + win.reportid) + '/' + file.name.split('.')[0],
                    success: function (response, opts) {
                        var data = Ext.decode(response.responseText).data;
                        if (!win.destroyed) {
                            var grid = win.down('grid');
                            var record = grid.getStore().getById(file.id);
                            grid.getView().getRow(record).style.backgroundColor = '#87CEFA';
                            // record.set('reportid', data.reportid);
                            record.set('eleid', data.eleid);
                            window.wmedia=data.eleid;
                        }
                    }
                });
            });
            //监听所有文件上传完毕，提示用户
            win.uploader.on('uploadFinished', function () {
                XD.msg('文件上传完毕');
            });
        },
        beforeclose: function (win) {
            win.uploader.stop(true);
            win.uploader.reset();
            win.uploader.destroy();
            WebUploader.Uploader.unRegister('electronicRep');
        }
    }
});