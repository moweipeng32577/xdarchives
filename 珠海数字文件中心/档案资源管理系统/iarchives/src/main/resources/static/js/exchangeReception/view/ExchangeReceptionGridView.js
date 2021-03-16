/**
 * Created by yl on 2017/11/2.
 */
Ext.define('ExchangeReception.view.ExchangeReceptionGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'exchangeReceptionGridView',
    region: 'center',
    searchstore: [{item: "filename", name: "文件名称"}],
    tbar: [{
        itemId: 'importXlsBtnID',
        text: '下载excel模版',
        iconCls:'fa fa-download'
    }, '-', {
        itemId: 'importSipBtnID',
        text: '导入sip、excel', handler: function () {
            var view = this.findParentByType('exchangeReceptionGridView');
            var win = Ext.create('Comps.view.UploadView', {});
            win.on('close', function () {
                view.initGrid();
            }, view);
            win.show();
        },
        iconCls:'fa fa-floppy-o'
    }, '-', {
        itemId: 'deleteBtnID',
        text: '删除',
        iconCls:'fa fa-trash-o'
    }],
    store: 'ExchangeReceptionGridStore',
    columns: [
        {text: '文件名称', dataIndex: 'filename', width: 200, menuDisabled: true},
        {text: 'MD5校验值', dataIndex: 'filemd5', width: 250, menuDisabled: true},
        {text: '文件大小(KB)', dataIndex: 'filesize', width: 200, menuDisabled: true},
        {text: '导入时间', dataIndex: 'filetime', width: 200, menuDisabled: true}
    ]
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
            getClass: function (v, metadata, r, rowIndex, colIndex, store) {
                if (typeof(r.data.eleid) != 'undefined') {
                    return "x-hidden";
                } else {
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
            text: '电子档案检查',
            dataIndex: 'check',
            width: 100
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
                win.uploader.upload();
            }
        }],
        listeners:{
            itemclick :function  ( view, record, item, index, e, eOpts ){
                if(e.position.colIdx == view.grid.columns.length-3 & typeof(record.data.check) != 'undefined'){
                    var win = Ext.create('ExchangeReception.view.ExchangeReceptionValidate');
                    win.title = '电子档案检查：' + record.data.name;
                    win.down('[itemId=closeBtn]').on('click',function(){win.close()});
                    win.down('[itemId=count]').html = record.data.checksip.count;
                    win.down('[itemId=quality]').html = record.data.checksip.quality;
                    win.down('[itemId=norm]').html = record.data.checksip.norm;
                    win.show();
                }
            }
        }
    },
    listeners: {
        render: function (win) {
            var msgs = '';
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
                server: '/exchangeReception/uploadSipfiles',
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
                accept: {
                    title: 'Sips',
                    extensions: 'sip,xls,xlsx',
                    mineTypes: 'sip,xls,xlsx/*'
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
                    url: '/exchangeReception/analysisSipfile',
                    params: {
                        fileName: file.name,
                        fileSize: file.size
                    },
                    sync: false,
                    success: function (response, opts) {
                        var respText = Ext.decode(response.responseText);
                        if (!win.destroyed) {
                            var grid = win.down('grid');
                            var record = grid.getStore().getById(file.id);
                            record.set('checksip', respText.data);
                            msgs += respText.msg + '<br>';
                            if (respText.success == false) {
                                record.set('check', "不通过");
                                grid.getView().getRow(record).style.backgroundColor = '#F08080';
                            } else {
                                record.set('eleid', file.id);
                                record.set('check', "通过");
                            }
                        }
                    }
                });
            });
            //监听所有文件上传完毕，提示用户
            win.uploader.on('uploadFinished', function () {
                setTimeout(function () {
                    if ('' == msgs) {
                        XD.msg('上传成功');
                    } else {
                        XD.msg(msgs);
                    }
                }, 1500);
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
