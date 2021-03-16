/**
 * Created by SunK on 2018/7/31 0031.
 */
var NodeIds = "";//保存nodeid
var filecount = 0;//计算文件数
var filepath = "";//后台传输的文件路径
Ext.define('Import.view.ImportGridView', {
    extend: 'Comps.view.EntryGridView',
    xtype: 'importgrid',
    dataUrl: '/management/entries',
    region: 'north',
    height: 40,
    tbar: [
        {
            itemId: 'importSipBtnID',
            text: '导入xml、excel、zip',
            handler: function (btn) {
                var view = this.findParentByType('importgrid');
                var tree = btn.up('importFormAndGrid').down('treepanel');
                NodeIds = tree.selModel.getSelected().items[0].get('fnid');
                //var grid = btn.up('importgrid');


                var win = Ext.create('Comps.view.UploadView', {});
                win.on('close', function () {
                    view.notResetInitGrid();
                }, view);
                win.show();
            },
            iconCls: 'fa fa-floppy-o'
        }
    ],
    title: '当前节点：',
    searchstore: {
        proxy: {
            type: 'ajax',
            url: '/template/queryName',
            extraParams: {nodeid: 0},
            reader: {
                type: 'json',
                rootProperty: 'content',
                totalProperty: 'totalElements'
            }
        }
    },
    hasSelectAllBox: true
});


//文件上传弹出框
Ext.define('Comps.view.UploadView', {
    extend: 'Ext.window.Window',
    xtype: 'uploadview',
    uploader: null,
    modal: true,
    width: 800,
    height: 400,
    title: '文件上传(<span style="color: #e50e0e">支持格式：xml,excel,zip压缩包</span>)',
    layout: 'fit',
    closeToolText: '关闭',
    parentByType: null,
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
                    url: '/electronic/chunk/' + encodeURIComponent(record.get('name')) + '/'
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
                Ext.Msg.wait('正在进行文件上传，请耐心等候……','正在操作');
                win.uploader.upload();
            }
        }]
    },
    listeners: {
        render: function (win) {
            //注册分片上传处理事件，用于断点续传
            WebUploader.Uploader.register({
                name: 'inform',
                'before-send': 'checkchunk'
            }, {
                checkchunk: function (block) {
                    var deferred = WebUploader.Deferred();
                    Ext.Ajax.request({
                        url: '/electronic/chunk/' +  encodeURIComponent(block.file.name) + '/' + block.chunks + '/' + block.chunk,
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
                server: '/import/importFileTransfer/'+win.entryid,
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
                fileSingleSizeLimit: 5242880000

            });
            //监听文件选择事件，将选中的文件信息添加到列表中

            win.uploader.on('filesQueued', function (files) {
                for (var i = 0; i < files.length; i++) {
                    var fileName = files[i].name;

                    var suffix = fileName.substring(fileName.indexOf(".") + 1).toLowerCase();

                    if ((suffix == "xml" || suffix == "xls" || suffix == "xlsx" || suffix == "zip")&&filecount == 0) {
                        filecount = 1;
                        win.down('grid').getStore().add({
                            id: files[i].id,
                            name: files[i].name,
                            size: Math.floor(files[i].size / 10240) / 100,
                            progress: 0
                        });
                    } else {
                        XD.msg('导入文件只支持单个文件上传,文件类型:'+ '<br>'+'Excel,Xml,zip格式的文件');
                        return;
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
                var fileName = file.name
                //这里是文件已经保存完毕---进行解析文件请求
                Ext.Ajax.request({
                    method: 'POST',
                    url: '/import/importExcel',
                    timeout: XD.timeout,
                    scope: this,
                    async: true,
                    params:{
                        NodeIds:NodeIds,
                        filename:fileName
                    },
                    success: function (response, opts) {
                        Ext.MessageBox.hide();
                        Ext.Ajax.request({
                            method: 'POST',
                            //url: '/import/isimport/'+ (typeof(win.informid) == 'undefined' ? '' : '/' + win.informid) + '/' + file.name + '/',
                            url: '/import/isimport',
                            timeout: XD.timeout,
                            scope: this,
                            async: true,
                            success: function (response, opts) {
                                filecount = 0;
                                var data = Ext.decode(response.responseText).data;
                                var importXmlFailureCount = data.importXmlFailureCount;
                                var importExcelFailureCount = data.importExcelFailureCount;
                                var importZipFailureCount = data.importZipFailureCount;
                                var failureCount = importXmlFailureCount + importExcelFailureCount + importZipFailureCount
                                if (failureCount > 0) {
                                    Ext.MessageBox.confirm('导入失败结果', '存在导入失败条目'+'<br>'+'导入失败数:' + failureCount+ '<br>' + '点击“是”下载条目失败信息', function (btn) {
                                        if (btn == 'yes') {
                                            window.open("/import/downloadImportFailure")
                                        }
                                        if (btn == 'no') {
                                            Ext.Ajax.request({
                                                method: 'POST',
                                                params: {
                                                    confirm: 'confirm'
                                                },
                                                url: '/import/deleteFailureFile',
                                                success: function () {
                                                }
                                            });
                                        }
                                    })
                                }
                                if (failureCount == 0) {
                                    XD.msg("数据导入成功");
                                }
                            },
                            failure: function () {
                                Ext.Msg.show({
                                    title: '导入结果提示',
                                    message: '获取结果数失败',
                                    buttons: Ext.Msg.OKCANCEL,
                                    buttonText: {ok: '确认'},
                                    fn: function (btn) {
                                        if (btn === 'ok') {

                                        }
                                    }
                                });
                            }
                        });
                    },
                    failure: function () {
                        Ext.MessageBox.hide();
                        Ext.Msg.show({
                            title: '导入结果提示',
                            message: '文件解析失败，请检查文件格式是否正确。',
                            buttons: Ext.Msg.OKCANCEL,
                            buttonText: {ok: '确认'},
                            fn: function (btn) {
                                if (btn === 'ok') {

                                }
                            }
                        });
                    }
                })

            });
            //监听所有文件上传完毕，提示用户
            win.uploader.on('uploadFinished', function () {

                //Ext.MessageBox.hide();
                //XD.msg('所有文件上传完毕');

            });
        },
        beforeclose: function (win) {
            filecount=0;
            win.uploader.stop(true);
            win.uploader.reset();
            win.uploader.destroy();
            WebUploader.Uploader.unRegister('inform');
        }
    }
});

