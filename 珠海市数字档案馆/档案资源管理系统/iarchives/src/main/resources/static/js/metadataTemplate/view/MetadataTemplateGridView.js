/**
 * Created by tanly on 2017/11/8 0024.
 */
var filecount = 0;//计算文件数
Ext.define('MetadataTemplate.view.MetadataTemplateGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'atemplateGridView',
    region: 'center',
    itemId: 'templateGridViewID',
    searchstore: [{item: "fieldcode", name: "字段编码"}, {item: "fieldname", name: "字段描述"}],
    tbar: {
        items:[{
            xtype: 'button',
            text: '字段管理',
            iconCls:'fa fa-columns',
            itemId: 'setfieldbtnid'
        }, '-', {
            xtype: 'button',
            text: '修改字段',
            iconCls:'fa fa-pencil-square-o',
            itemId: 'updatefieldbtnid'
        }, '-', {
            xtype: 'button',
            text: '复制模板',
            iconCls:'fa fa-clone',
            itemId: 'copytemplatebtnid'
        }, '-', {
            xtype: 'button',
            text: '删除模板',
            iconCls:'fa fa-trash-o',
            itemId: 'deletetemplatebtnid'
        }, '-', {
        	xtype: 'button',
        	text: '模板预览',
        	iconCls:'fa fa-eye',
        	itemId: 'resultPreviewbtnid'
        }, '-', {
            iconCls: '',
            itemId: "exportID",
            menu: [
                {
                    text: '导出模板',
                    itemId: 'exportTemplate',
                    iconCls: 'fa fa-download'
                }, '-', {
                    text: '导入模板',
                    itemId: 'importTemplate',
                    iconCls: 'fa fa-floppy-o'
                }
            ],
            text: '导入导出'
        }, '-',{
            xtype: 'button',
            text: '分组管理',
            iconCls:'fa fa-columns',
            itemId: 'groupManagement'
        }],
        overflowHandler:'scroller'
    },
    store: 'MetadataTemplateGridStore',
    columns: [
        {text: '所属表', dataIndex: 'fieldtable', flex: 2, menuDisabled: false},
        {text: '字段编码', dataIndex: 'fieldcode', flex: 2, menuDisabled: true},
        {text: '元数据编码', dataIndex: 'metadatacode', flex: 2, menuDisabled: true},
        {text: '元数据名称', dataIndex: 'fieldname', flex: 2, menuDisabled: true},
        {
            text: '列表字段',
            dataIndex: 'gfield',
            flex: 2,
            menuDisabled: true,
            renderer:function(value, metaData, record){
                value = "否";
                if(record.get('gfield')){
                    value = "是";
                }
                return value;
            }
        },
        {text: '列表字段顺序', dataIndex: 'gsequence', flex: 2, menuDisabled: true},
        {
            text: '检索字段',
            dataIndex: 'qfield',
            flex: 2,
            menuDisabled: true,
            renderer:function(value, metaData, record){
                value = "否";
                if(record.get('qfield')){
                    value = "是";
                }
                return value;
            }
        },
        {text: '检索字段排序', dataIndex: 'qsequence', flex: 2, menuDisabled: true},
        {
            text: '表单字段',
            dataIndex: 'ffield',
            flex: 2,
            menuDisabled: true,
            renderer:function(value, metaData, record){
                value = "否";
                if(record.get('ffield')){
                    value = "是";
                }
                return value;
            }
        },
        {text: '表单字段排序', dataIndex: 'fsequence', flex: 2, menuDisabled: true}
    ]
});
//文件上传弹出框
Ext.define('Comps.view.TepmUploadView', {
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

                    if ((suffix == "xls"||suffix == "xlsx") &&filecount == 0) {
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
                console.log(fileName)
                //这里是文件已经保存完毕---进行解析文件请求
                Ext.Ajax.request({
                    method: 'POST',
                    url: '/metadataTemplate/importFieldModel',
                    timeout: XD.timeout,
                    scope: this,
                    async: true,
                    params:{
                        NodeIdf:NodeIdf,
                        filename:fileName
                    },
                    success: function (opts) {
                        Ext.MessageBox.hide();
                        XD.msg("导入成功！")
                        Ext.Ajax.request({
                            method: 'POST',
                            url: '/template/deleteUploadFieldModel',
                            params:{
                                filename:fileName
                            },
                            success: function (opts){
                                RealoadtemplateView.down('atemplateGridView').getStore().reload();
                            }
                        })
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