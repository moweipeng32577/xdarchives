/**
 * Created by yl on 2017/11/1.
 */
var Store = Ext.create('Ext.data.Store', {
    fields: ['Name', 'Value'],
    data: [{
        Name: '年鉴',
        Value: '年鉴'
    }, {
        Name: '方志馆',
        Value: '方志馆'
    }, {
        Name: '地方志',
        Value: '地方志'
    }, {
        Name: '党史',
        Value: '党史'
    }, {
        Name: '展览',
        Value: '展览'
    }, {
        Name: '年报',
        Value: '年报'
    }, {
        Name: '其他',
        Value: '其他'
    }]
});
Ext.define('ThematicProd.view.ThematicProdAddWindow', {
    extend: 'Ext.window.Window',
    xtype: 'thematicProdAddWindow',
    title: '增加',
    width: 500,
    height: 450,
    closeToolText: '关闭',
    modal: true,
    resizable: false,
    layout: 'fit',
    items: [
        {
            xtype: 'form',
            layout: 'column',
            bodyPadding: 20,
            items: [{
                xtype: 'textfield',
                fieldLabel: '专题名称<span style="color: #CC3300; padding-right: 2px;">*</span>',
                columnWidth: 1,
                allowBlank: false,
                blankText: '该输入项为必输项',
                name: 'title',
                labelWidth: 100
            }, {
                xtype: 'textarea',
                height: 135,
                fieldLabel: '专题描述<span style="color: #CC3300; padding-right: 2px;">*</span>',
                columnWidth: 1,
                allowBlank: false,
                blankText: '该输入项为必输项',
                name: 'thematiccontent',
                labelWidth: 100,
                margin: '10 0 0 0'
            }, {
                xtype : 'combobox',
                name:'thematictypes',
                store: Ext.create('Ext.data.Store',{
                    fields: ['code', 'code'],
                    proxy: {
                        type:'ajax',
                        url:'/systemconfig/findConfigByConfigcode',
                        extraParams:{configcode:'方志馆'},
                        reader: {
                            type:'json'
                        }
                    },
                    autoLoad:true
                }),
                fieldLabel:'专题类型<span style="color: #CC3300; padding-right: 2px;">*</span>',
                columnWidth: 1,
                blankText:'该输入项为必输项',
                editable:false,
                allowBlank: false,
                margin: '10 0 0 0',
                displayField: "code",
                valueField: "code"
            },{
                columnWidth: 0.27,
                xtype: 'label',
                text: '背景图:',
                margin: '20 0 0 0'
            },
                {
                    xtype: 'component', //或者xtype: 'component',
                    width: 65, //图片宽度
                    height: 65, //图片高度
                    itemId:'component',
                    autoEl: {
                        tag: 'img',    //指定为img标签
                        src: '/img/icon/thematic_def.png'    //指定url路径
                    },
                    margin: '10 0 0 0',
                    backgroundpath:null,
                    listeners: {
                        el:{
                            click:function(){
                                var component =this.component;
                                var win = Ext.create('Comps.view.UploadBackgroundView', {parentByType:component});
                                win.on('close', function (view) {
                                    if(component.backgroundpath==null){
                                        component.getEl().dom.src ="/thematicProd/getBackground?url="+'/static/img/icon/thematic_def.png';
                                    }else{
                                        component.getEl().dom.src = "/thematicProd/getBackground?url=" + encodeURIComponent(component.backgroundpath);
                                    }
                                }, this);
                                win.show();
                            }
                        }
                    }
                },{
                    columnWidth: 0.6,
                    xtype: 'label',
                    text: '(点击图片可以进行上传，如果不上传，默认以当前图片为背景图)',
                    margin: '25 0 0 10'
                }
            ]
        }
    ],
    buttons: [{
        itemId: 'thematicProSaveBtnID',
        text: '保存'
    }, {
        itemId: 'thematicProBackBtnID',
        text: '返回'
    }]
});
//文件上传弹出框
Ext.define('Comps.view.UploadBackgroundView', {
    extend: 'Ext.window.Window',
    xtype: 'uploadview',
    uploader: null,
    modal: true,
    width: 800,
    height: 400,
    title: '文件上传',
    layout: 'fit',
    closeToolText: '关闭',
    parentByType:null,
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
                if(typeof(r.data.backgroundpath) != 'undefined') {
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
                }else if(win.down('grid').getStore().getCount() >1){
                    XD.msg('只能选择一张图片');
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
                name: 'inform',
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
                server: '/thematicProd/electronicsBackground',
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
                accept: {
                title: 'Thematic',
                    extensions: 'jpg,png,jpeg',
                    mineTypes: 'jpg,png,jpeg/*'
            }

            });
            //监听文件选择事件，将选中的文件信息添加到列表中
            win.uploader.on('filesQueued', function (files) {
                //只能上传一张图片
                if(files.length > 1){
                    for (var i = 0; i < files.length; i++) {
                        //从上传队列中删除文件
                        win.uploader.removeFile(files[i].id, true);
                        //清除服务器上的缓存
                        Ext.Ajax.request({
                            method: 'DELETE',
                            url: '/electronic/chunk/' + files[i].name + '/'
                        });
                    }
                    XD.msg("只能选择一张图片");
                }else{
                    for (var i = 0; i < files.length; i++) {
                        win.down('grid').getStore().add({
                            id: files[i].id,
                            name: files[i].name,
                            size: Math.floor(files[i].size / 10240) / 100,
                            progress: 0
                        });
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
                    url: '/thematicProd/electronics/'+ file.name + '/',
                    success: function (response, opts) {
                        var record = win.down('grid').getStore().getById(file.id);
                        var data = Ext.decode(response.responseText).data;
                        win.parentByType.backgroundpath=data;
                        record.set('backgroundpath', data);
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
            WebUploader.Uploader.unRegister('inform');
        }
    }
});