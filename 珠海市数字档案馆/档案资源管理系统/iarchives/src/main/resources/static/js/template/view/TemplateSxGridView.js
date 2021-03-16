/**
 * Created by tanly on 2017/11/8 0024.
 */
Ext.define('Template.view.TemplateTableView', {
    extend: 'Ext.tab.Panel',
    xtype: 'TemplateTableView',
    //标签页靠左配置--start
    tabPosition: 'top',
    tabRotation: 0,
    //标签页靠左配置--end
    activeTab: 0,
    items: [{
        title: '件',
        itemId:'pieceId',
        xtype: 'panel',
        layout: 'fit',
        items: [
            {
                itemId:'publicPanel',
                layout:'fit',
                items:{xtype:'templateSxGridView',dataurl:'/photoOpen/getSecurityGrid'}
            }
        ]
    },{
        title: '组',
        itemId:'groupId',
        xtype: 'panel',
        layout: 'fit',
        items: [
            {
                itemId:'publicPanel2',
                layout:'fit',
                items:{xtype:'templateSxGridView',dataurl:'/photoOpen/getSecurityGrid'}
            }
        ]
    },{
        title: '案卷',
        itemId:'volumeId',
        xtype: 'panel',
        layout: 'fit',
        items: [
            {
                itemId:'publicPanel3',
                layout:'fit',
                items:{xtype:'templateSxGridView',dataurl:'/photoOpen/getSecurityGrid'}
            }
        ]
    }]
});

var filecount = 0;//计算文件数
Ext.define('Template.view.TemplateSxGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'templateSxGridView',
    region: 'center',
    itemId: 'templateSxGridViewID',
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
        },'-', {
            xtype: 'button',
            text: '字段描述',
            iconCls:'fa fa-pencil-square-o',
            itemId: 'fieldcodeDesc'
        },  '-', {
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
        }, '-', {
            xtype: 'button',
            text: '同步模板到',
            iconCls:'fa fa-refresh',
            itemId: 'synctemplatebtnid',
            menu: [{
                text: '首层子节点',
                iconCls: 'x-ctxmenu-firstChild-icon',
                itemId: 'firstChild',
                menu: [{
                    text: '包含档号设置',
                    itemId: 'firstChildWithCode'
                }, {
                    text: '不包含档号设置',
                    itemId: 'firstChildWithoutCode'
                }]
            }, {
                text: '所有子节点',
                iconCls: 'x-ctxmenu-allChild-icon',
                itemId: 'allChild',
                menu: [{
                    text: '包含档号设置',
                    itemId: 'allChildWithCode'
                }, {
                    text: '不包含档号设置',
                    itemId: 'allChildWithoutCode'
                }]
            }]
        }, '-', {
            xtype: 'button',
            text: '档号设置',
            iconCls:'fa fa-cog',
            itemId: 'templateCodeSettingId'
        }, '-', {
        	xtype: 'button',
        	text: '锁定模板',
            iconCls:'fa fa-lock',
        	itemId: 'luckTemplate'
        }, '-', {
        	xtype: 'button',
        	text: '解锁模板',
            iconCls:'fa fa-unlock',
        	itemId: 'unluckTemplate'
        }],
//        , '-',{
//            xtype: 'button',
//            text: '快速调整',
//            iconCls:'fa fa-cog',
//            itemId: 'templatefastsetbtnid',
//            menu: [{
//                text: '列表字段',
//                iconCls: 'fa fa-setlistfield',
//                itemId: 'listfield',
//                menu: [{
//                    text: '全设为是',
//                    itemId: 'isListField'
//                }, {
//                    text: '全设为否',
//                    itemId: 'isnotListField'
//                }, {
//                    text: '顺序上调',
//                    itemId: 'ListFieldSequentialup'
//                },{
//                    text: '顺序下调',
//                    itemId: 'ListFieldSequentialdown'
//                }]
//            }, {
//                text: '检索字段',
//                iconCls: 'fa fa-setretrievefield',
//                itemId: 'retrievefield',
//                menu: [{
//                    text: '全设为是',
//                    itemId: 'isRetrieveField'
//                }, {
//                    text: '全设为否',
//                    itemId: 'isnotRetrieveField'
//                }, {
//                    text: '顺序上调',
//                    itemId: 'RetrieveFieldSequentialup'
//                },{
//                    text: '顺序下调',
//                    itemId: 'RetrieveFieldSequentialdown'
//                }]
//        }, {
//                text: '表单字段',
//                iconCls: 'fa fa-setretrievefield',
//                itemId: 'formfield',
//                menu: [{
//                    text: '全设为是',
//                    itemId: 'isFormField'
//                }, {
//                    text: '全设为否',
//                    itemId: 'isnotFormField'
//                }, {
//                    text: '顺序上调',
//                    itemId: 'FormFieldSequentialup'
//                }, {
//                    text: '顺序下调',
//                    itemId: 'FormFieldSequentialdown'
//                }]
//         }]
//    }],
        overflowHandler:'scroller'
    },
    store: 'TemplateSxGridStore',
    columns: [
        {text: '所属表', dataIndex: 'fieldtable', flex: 2, menuDisabled: false},
        {text: '字段编码', dataIndex: 'fieldcode', flex: 3, menuDisabled: true},
        {text: '字段描述', dataIndex: 'fieldname', flex: 2, menuDisabled: true},
        {
            text: '列表字段',
            dataIndex: 'gfield',
            flex: 2,
            menuDisabled: true,
            renderer:function(value, metaData, record){
                value = "";
                if(record.get('gfield')){
                    value = "是";
                }
                return value;
            }
        },
        {text: '列表顺序', dataIndex: 'gsequence', flex: 2, menuDisabled: true},
        {
            text: '检索字段',
            dataIndex: 'qfield',
            flex: 2,
            menuDisabled: true,
            renderer:function(value, metaData, record){
                value = "";
                if(record.get('qfield')){
                    value = "是";
                }
                return value;
            }
        },
        {text: '检索排序', dataIndex: 'qsequence', flex: 2, menuDisabled: true},
        {
            text: '表单字段',
            dataIndex: 'ffield',
            flex: 2,
            menuDisabled: true,
            renderer:function(value, metaData, record){
                value = "";
                if(record.get('ffield')){
                    value = "是";
                }
                return value;
            }
        },
        {text: '表单排序', dataIndex: 'fsequence', flex: 2, menuDisabled: true},
        {text: '默认值', dataIndex: 'fdefault', flex: 2, menuDisabled: true},
        {text: '字段长度', dataIndex: 'fieldlength', flex: 2, menuDisabled: true},
        {
            text: '字段类型',
            dataIndex: 'ftype',
            flex: 2,
            menuDisabled: true,
            renderer:function(value, metaData, record){
                if(record.get('ftype') === 'string'){
                    return "字符型";
                }else if(record.get('ftype') === 'date'){
                    return "日期型"
                }else if(record.get('ftype') === 'keyword'){
                    return "主题词型";
                }else if(record.get('ftype') === 'enum'){
                    return "枚举型";
                }else if(record.get('ftype') === 'calculation') {
                    return "统计型";
                }else{
                    return "日期范围型";
                }
            }
        },
        {text: '枚举值', dataIndex: 'fenums', flex: 2, menuDisabled: true,},
        {
            text: '枚举编辑',
            dataIndex: 'fenumsedit',
            flex: 2,
            menuDisabled: true,
            renderer:function(value, metaData, record){
                value = "";
                if(record.get('fenumsedit')){
                    value = "是";
                }
                return value;
            }
        },
        {
            text: '必填',
            dataIndex: 'frequired',
            flex: 2,
            menuDisabled: true,
            renderer:function(value, metaData, record){
                value = "";
                if(record.get('frequired')){
                    value = "是";
                }
                return value;
            }
        },
        {
            text: '只读',
            dataIndex: 'freadonly',
            flex: 2,
            menuDisabled: true,
            renderer:function(value, metaData, record){
                value = "";
                if(record.get('freadonly')){
                    value = "是";
                }
                return value;
            }
        },

        {
            text: '其他项',
            dataIndex: 'inactiveformfield',
            flex: 2,
            menuDisabled: true,
            renderer:function(value, metaData, record){
                value = "";
                if(record.get('inactiveformfield')){
                    value = "是";
                }
                return value;
            }
        }
    ]
});
//文件上传弹出框
Ext.define('Comps.view.TepmSxUploadView', {
    extend: 'Ext.window.Window',
    xtype: 'uploadview',
    uploader: null,
    modal: true,
    width: 800,
    height: 400,
    title: '文件上传(<span style="color: #e50e0e">支持格式：xlsx</span>)',
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

                    if (suffix == "xlsx" &&filecount == 0) {
                        filecount = 1;
                        win.down('grid').getStore().add({
                            id: files[i].id,
                            name: files[i].name,
                            size: Math.floor(files[i].size / 10240) / 100,
                            progress: 0
                        });
                    } else {
                        XD.msg('导入文件只支持单个文件上传,文件类型:'+ '<br>'+'xlsx格式的文件');
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
                    url: '/template/importFieldModel',
                    timeout: XD.timeout,
                    scope: this,
                    async: true,
                    params:{
                        NodeIdf:win.NodeIdf,
                        filename:fileName,
                        xtType:window.xtType
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
                                SxRealoadtemplateView.down('templateSxGridView').getStore().reload();
                            }
                        })
                        // Ext.Ajax.request({
                        //     method: 'POST',
                        //     //url: '/import/isimport/'+ (typeof(win.informid) == 'undefined' ? '' : '/' + win.informid) + '/' + file.name + '/',
                        //     url: '/import/isimport',
                        //     timeout: '3600000',
                        //     scope: this,
                        //     async: true,
                        //     success: function (response, opts) {
                        //         filecount = 0;
                        //         var data = Ext.decode(response.responseText).data;
                        //         var importXmlFailureCount = data.importXmlFailureCount;
                        //         var importExcelFailureCount = data.importExcelFailureCount;
                        //         var importZipFailureCount = data.importZipFailureCount;
                        //         var failureCount = importXmlFailureCount + importExcelFailureCount + importZipFailureCount
                        //         if (failureCount > 0) {
                        //             Ext.MessageBox.confirm('导入失败结果', '存在导入失败条目'+'<br>'+'导入失败数:' + failureCount+ '<br>' + '点击“是”下载条目失败信息', function (btn) {
                        //                 if (btn == 'yes') {
                        //                     window.open("/import/downloadImportFailure")
                        //                 }
                        //                 if (btn == 'no') {
                        //                     Ext.Ajax.request({
                        //                         method: 'POST',
                        //                         params: {
                        //                             confirm: 'confirm'
                        //                         },
                        //                         url: '/import/deleteFailureFile',
                        //                         success: function () {
                        //                         }
                        //                     });
                        //                 }
                        //             })
                        //         }
                        //         if (failureCount == 0) {
                        //             XD.msg("数据导入成功");
                        //         }
                        //     },
                        //     failure: function () {
                        //         Ext.Msg.show({
                        //             title: '导入结果提示',
                        //             message: '获取结果数失败',
                        //             buttons: Ext.Msg.OKCANCEL,
                        //             buttonText: {ok: '确认'},
                        //             fn: function (btn) {
                        //                 if (btn === 'ok') {
                        //
                        //                 }
                        //             }
                        //         });
                        //     }
                        // });
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