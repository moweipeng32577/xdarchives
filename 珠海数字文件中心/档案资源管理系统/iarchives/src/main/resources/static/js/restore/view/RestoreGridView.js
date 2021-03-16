/**
 * Created by tanly on 2018/1/26 0026.
 */
Ext.define('Restore.view.RestoreGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'restoreGridView',
    hasPageBar: false,
    hasSearchBar: false,
    hasCheckColumn:false,
    store: 'RestoreGridStore',
    tbar: [{
        xtype: 'button',
        text: '上传文件',
        iconCls:'fa fa-upload',
        itemId: 'upload'
    }, '-', {
        xtype: 'button',
        iconCls:'fa fa-refresh',
        text: '刷新',
        itemId: 'refresh'
    }, '->', {
        xtype: 'textfield',
        fieldLabel: '当前备份项',
        width: 500,
        itemId: 'gridsel',
        readOnly: true
    }],
    columns: [
        {text: '文件名', dataIndex: 'filename', flex: 6, menuDisabled: true},
        {text: '文件大小(MB)', dataIndex: 'filesize', flex: 2, menuDisabled: true},
        {text: '文件时间', dataIndex: 'filetime', flex: 3, menuDisabled: true}
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
                var win = this.findParentByType('uploadview');
                if(win.down('grid').getStore().data.length==0){
                    XD.msg('请选择上传文件!');
                    return;
                }
                win.uploader.upload();
            }
        }]
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
                server: '/backupRestore/uploadZipFiles',
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
                    title: 'Zips',
                    extensions: 'zip',
                    mineTypes: 'zip/*'
                }

            });
            //监听文件选择事件，将选中的文件信息添加到列表中
            win.uploader.on('filesQueued', function (files) {
                var filename = [];
                for (var i = 0; i < files.length; i++) {
                    filename.push(files[i].name)
                }
                Ext.Ajax.request({
                    url: '/backupRestore/validateZip',
                    params: {fileName: filename},
                    success: function (response) {
                        var respText = Ext.decode(response.responseText);
                        var str = [];
                        if (respText.success == false) {
                            XD.msg(respText.msg);
                            str = respText.data.split(',')
                        }
                        for (var i = 0; i < files.length; i++) {
                            var isexist = false;
                            for (var j = 0; j < str.length; j++) {
                                if (str[j] === files[i].name) {
                                    isexist = true;
                                    break;
                                }
                            }
                            if (!isexist) {
                                win.down('grid').getStore().add({
                                    id: files[i].id,
                                    name: files[i].name,
                                    size: Math.floor(files[i].size / 10240) / 100,
                                    progress: 0
                                });
                            } else {
                                var uploader = win.down('grid').findParentByType('uploadview').uploader;
                                uploader.removeFile(files[i].id, true);
                            }
                        }
                    }
                });

            });
            //监听文件上传进度，更新列表中上传进度条
            win.uploader.on('uploadProgress', function (file, progress) {
                if (!win.destroyed) {
                    var record = win.down('grid').getStore().getById(file.id);
                    record.set('progress', progress);
                }
            });
            //监听文件上传成功，提示用户
            // win.uploader.on('uploadSuccess',function(file){
            //
            // });
            //监听所有文件上传完毕，提示用户
            win.uploader.on('uploadFinished', function () {
                setTimeout(function () {
                    if ('' == msgs) {
                        XD.msg('上传成功');
                    } else {
                        XD.msg(msgs);
                    }
                }, 1500);//todo
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
