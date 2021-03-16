/**
 * Created by Administrator on 2020/6/19.
 */


Ext.define('Reservation.view.LookEvidenceTextView', {
    entryid: undefined,     //条目主键ID
    entrytype: '',   //数据类型（采集、管理、利用）

    extend: 'Ext.panel.Panel',
    xtype: 'electronicPro',
    layout: 'border',
    bodyBorder: false,
    defaults: {
        split: true
    },
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
                text: '附件'
            }
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
                    var win = Ext.create('Comps.view.UploadView', {
                        entrytype: view.entrytype,
                        entryid: view.entryid,
                        treepanel:view.down('treepanel')
                    });
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
                            this.down('treepanel').getStore().reload();
                        }
                    }, view);
                    win.show();
                }
                },
                {
                    xtype: 'button', text: '删除', handler: function () {
                    var view = this.findParentByType('electronicPro');
                    view.del();
                }
                },
                {
                    xtype: 'button', text: '返回', handler: function () {
                    var view = this.findParentByType('electronicPro');
                    var trees = view.down('treepanel').getRootNode().childNodes;
                    window.wform.query('[itemId=mediacount]')[0].setText('共' + trees.length + '份');
                    var mediatext = '';
                    var eleids = [];
                    for (var i = 0; i < trees.length; i++) {
                        mediatext += ',' + trees[i].data.text;
                        eleids.push(trees[i].data.fnid);
                    }

                    if (mediatext != '') {
                        mediatext = mediatext.substring(1);
                    }
                    window.wform.query('[itemId=media]')[0].setValue(mediatext);
                    view.findParentByType('window').close();
                    window.wmedia = eleids;
                }
                }
            ]
        }],
        listeners: {
            beforeload: function () {
                var view = this.findParentByType('electronicPro');
                this.getStore().proxy.url = '/electronic/electronics/tree/management' + '/' + view.entryid + '/' + window.wmedia;
            },
            afterrender: function () {
                this.expandAll();
            },
            itemclick: function (view, record, item, index, e, eOpts) {
                if (!record.get('leaf'))
                    return;
                if(e.getTarget('.x-tree-checkbox',1,true)){
                    return;
                }
                var mediaFrame = document.getElementById('mediaFrame');
                var filename = record.get('text');
                mediaFrame.setAttribute('src', '/electronic/media?entrytype=management&eleid=' + record.get('fnid') + '&filetype=' + filename.substring(filename.lastIndexOf('.') + 1));
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

    initData: function (entryid) {
        this.entryid = entryid;
        this.down('treepanel').getStore().reload();
    },

    getEleids: function () {
        var ids = [];
        var records = this.down('treepanel').getStore().getRoot().childNodes;
        for (var i = 0; i < records.length; i++) {
            ids.push(records[i].data.fnid);
        }
        return ids.join(",");
    },

    del: function () {
        var records = this.down('treepanel').getView().getChecked();
        if (records.length == 0) {
            XD.msg('请至少选择一条需要删除的数据');
            return;
        }
        var treeEleids = this.getEleids().split(',');
        XD.confirm('确定要删除这' + records.length + '条数据吗', function () {
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
                    for (var i = 0; i < eleids.length; i++) {
                        for (var j = 0; j < treeEleids.length; j++) {
                            if (treeEleids[j] == eleids[i]) {
                                treeEleids.splice(j, 1);
                                j = j - 1;
                            }
                        }
                    }
                    window.wmedia = treeEleids;
                    this.down('treepanel').getStore().reload();
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
                server: '/electronic/electronicsBorrow',
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
                    url: '/electronic/ztelectronics/borrow/management' + (typeof(win.entryid) == 'undefined' ? '' : '/' + win.entryid) + '/' + file.name + '/',
                    success: function (response, opts) {
                        var data = Ext.decode(response.responseText).data;
                        if (!win.destroyed) {
                            var grid = win.down('grid');
                            var record = grid.getStore().getById(file.id);
                            grid.getView().getRow(record).style.backgroundColor = '#87CEFA';
                            record.set('eleid', data.eleid);
                            window.wmedia.push(data.eleid);
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
