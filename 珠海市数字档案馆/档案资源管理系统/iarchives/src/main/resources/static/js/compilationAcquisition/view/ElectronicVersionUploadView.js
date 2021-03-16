/**
 * Created by Administrator on 2019/4/19.
 */


Ext.define('CompilationAcquisition.view.ElectronicVersionUploadView', {
    extend: 'Ext.window.Window',
    xtype: 'electronicVersionUploadView',
    entryid: '',
    entrytype: '',
    uploader: null,
    modal: true,
    width: 800,
    height: 400,
    title: '文件上传',
    layout: 'fit',
    closeToolText: '关闭',
    version: '',
    remark: '',
    isOk:'true',
    actions: {
        del: {
            iconCls: 'x-action-upload-delete-icon',
            tooltip: '删除',
            handler: function (view, row) {
                var grid = view.grid;
                var record = grid.getStore().getAt(row);
                //从上传队列中删除文件
                var uploader = grid.findParentByType('electronicVersionUploadView').uploader;
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
                if(typeof(r.data.filename) != 'undefined') {
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
        }
            , {
                xtype: 'actioncolumn',
                width: 30,
                items: ['@del']
            }
        ],
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
                var win = this.findParentByType('electronicVersionUploadView');
                if (win.down('grid').getStore().getCount() == 0) {
                    XD.msg('未选择文件');
                    return;
                }
                if (win.down('grid').getStore().getCount() != 1) {
                    XD.msg('只能选择一份选择文件');
                    return;
                }
                win.uploader.upload();
            }
        }]
    },
    initComponent: function () {
        var me = this;
        me.listeners = {
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
                    server: '/electronic/serelectronics/version',
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
                    fileSingleSizeLimit: 524288000,
                    accept:{
                        title: 'Images',
                        extensions: 'jpg,jpeg,png,tif',
                        miniTypes: 'image/*'
                    }//电子文件现在类型
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
                        async: false,
                        url: '/electronic/electronics/saveVersion/'+win.entrytype+'/' + file.name + '/',
                        params: {
                            version: win.version,
                            remark: win.remark,
                            eleid: win.eleid,
                            entryid:win.entryid
                        },
                        success: function (response, opts) {
                            if (!win.destroyed) {
                                var grid = win.down('grid');
                                var record = grid.getStore().getById(file.id);
                                grid.getView().getRow(record).style.backgroundColor = '#87CEFA';
                                record.set('filename',record.data.name);
                            }

                        }
                    });
                });
                // 监听所有文件上传完毕，提示用户
                win.uploader.on('uploadFinished', function (response, opts) {
                        XD.msg('成功保存历史版本');
                });
            },
            beforeclose: function (win) {
                win.uploader.stop(true);
                win.uploader.reset();
                win.uploader.destroy();
                WebUploader.Uploader.unRegister('electronic');
            },
            close: function (win) {
             win.eletree.getStore().reload();
            }
        };
        this.callParent();
    }
});
