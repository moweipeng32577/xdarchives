/**
 * Created by Administrator on 2017/10/23 0023.
 */
var ztStore = Ext.create("Ext.data.Store",{
    fields:["text","value"],
    data:[{
        text:"正常",value:"0"},
        {text:"已满",value:"1"},
        {text:"维护中",value:"2"}
    ]
});
Ext.define('Showroom.view.ShowroomAddForm', {
    informid:'',
    extend: 'Ext.window.Window',
    xtype: 'showroomAddForm',
    itemId:'showroomAddFormId',
    frame: true,
    resizable: true,
    flag:'',
    width: '80%',
    minWidth: 610,
    height:'90%',
    // minHeight: 640,
    modal:true,
    closeToolText:'关闭',
    requires: [
        'Ext.layout.container.Border'
    ],
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    defaults: {
        layout: 'form',
        xtype: 'container',
        defaultType: 'textfield',
        style: 'width: 50%'
    },
    items: [
        {
            xtype: 'form',
            modelValidation: true,
            layout:'column',
            scrollable:true,
            bodyPadding:10,
            height:180,
            items: [
                {columnWidth: 1, fieldLabel: '', name: 'showroomid', hidden: true},
                {columnWidth: .98, fieldLabel: '展厅名称', name: 'title', allowBlank: false}
                ,{
                    columnWidth: .02,
                    xtype : 'displayfield',
                    value: '<label style="color:#ff0b23;!important;">*</label>',
                    margin:'0 10 0 10'
                },
                /*{
                    columnWidth: 1,
                    fieldLabel: '到期时间',
                    name: 'limitdate',
                    xtype: 'datefield',
                    value: new Date(),
                    format: 'Ymd',
                    allowBlank: false,
                    margin: '10 0 0 0'
                },*/
                {
                    columnWidth:.98,
                    xtype: 'combo',
                    fieldLabel:'展厅状态',
                    name:'flag',
                    margin:'10 1 10 0',
                    store: ztStore,
                    queryMode:'all',
                    displayField: 'text',
                    valueField: 'value',
                    allowBlank: false,
                    editable: false,
                    listeners: {
                        afterrender: function (combo) {
                            var store = combo.getStore();
                            if (store.getCount() > 0) {
                                combo.select(store.getAt(0));
                            }
                        }
                    }
                },{
                    columnWidth: .02,
                    xtype : 'displayfield',
                    value: '<label style="color:#ff0b23;!important;">*</label>',
                    margin:'0 10 10 10'
                },{
                    columnWidth:.48,
                    xtype : 'textfield',
                    fieldLabel:'展厅序号',
                    name:'sequence',
                    margin:'0 0 10 0',
                    allowBlank:false
                },{
                    columnWidth: .02,
                    xtype : 'displayfield',
                    value: '<label style="color:#ff0b23;!important;">*</label>',
                    margin:'0 10 10 10'
                },{
                    columnWidth:.48,
                    xtype : 'textfield',
                    fieldLabel:'每日参观人数',
                    labelWidth: 105,
                    name:'audiences',
                    margin:'0 1 10 0',
                    allowBlank:false
                },{
                    columnWidth: .02,
                    xtype : 'displayfield',
                    value: '<label style="color:#ff0b23;!important;">*</label>',
                    margin:'0 10 10 10'
                },{
                    columnWidth: 0.07,
                    xtype: 'label',
                    text: '展厅附件:',
                    margin: '10 0 0 0',
                    border: true
                },
                {   columnWidth: 0.15,
                    xtype: 'label',
                    text: '(可双击删除电子文件)',
                    margin: '10 0 0 0',
                    style:{
                        color: 'red',
                    },
                    border: true
                },
                {
                    columnWidth: 0.60, xtype: 'container', itemId: 'container',scrollable:true, margin: '10 0 0 0',
                    eleids: [],
                    fileName: [],
                    defaults: {
                        margin: '0 20 0 0',
                        listeners: {
                            render: function (view) {//渲染后添加双击事件
                                view.addListener("dblclick", function () {
                                    XD.confirm('确定要删除这个' + view.text + '文件吗', function () {
                                        var container=view.findParentByType('form').down('[itemId=container]');
                                        Ext.Ajax.request({
                                            url: '/inform/electronics/'+view.eleid+ '/',
                                            method: 'DELETE',
                                            sync: true,
                                            success: function (resp) {
                                                var respText = Ext.decode(resp.responseText);
                                                if (respText.success == true) {
                                                    XD.msg(respText.msg);
                                                    for(var i=0;i<container.eleids.length;i++){
                                                        if(view.eleid==container.eleids[i]){
                                                            container.eleids.splice(i,1);
                                                            container.fileName.splice(i,1);
                                                        }
                                                    }
                                                    container.remove(view);
                                                }else{
                                                    XD.msg(respText.msg);
                                                }
                                            },
                                            failure: function() {
                                                XD.msg('操作失败');
                                            }
                                        });
                                    }, this);
                                }, null, {element: 'el'});
                            },
                            scope: this
                        }
                    }
                },
                {
                    columnWidth: 0.09, xtype: 'button', itemId: 'upload', text: '上传附件', margin: '10 0 0 10',
                    handler: function (view) {
                        var container =view.findParentByType('form').down('[itemId=container]');
                        var win = Ext.create('Comps.view.UploadView', {parentByType:container});
                        win.on('close', function () {
                            container.removeAll();
                            var lable = [];
                            for(var i=0;i<container.eleids.length;i++){
                                lable.push({xtype: 'label',text: container.fileName[i],eleid:container.eleids[i]});
                            }
                            container.add(lable);
                        }, view);
                        win.show();
                    }
                }
            ]
        },
        {
            width:600,
            height:350,
            html:'<span style="font-size: 1.2em;"> 展厅介绍 :</span><iframe id="editFrame" src="htmledit"  width="100%"' +
            ' height="350px" style="margin:0px;border:0px;"></iframe>'
        }
    ],

    buttons:[{
        text:'保存',//增加反馈快捷键暂时未生效（有bug）
        itemId:'save'
    },'-',{
        text:'返回',
        itemId:'back'
    }]
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
                server: '/inform/electronicsInform',
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
                    url: '/inform/electronics/'+ (typeof(win.informid) == 'undefined' ? '' : '/' + win.informid) + '/' + file.name + '/',
                    success: function (response, opts) {
                        var data = Ext.decode(response.responseText).data;
                        if (!win.destroyed) {
                            var grid = win.down('grid');
                            var record = grid.getStore().getById(file.id);
                            grid.getView().getRow(record).style.backgroundColor = '#87CEFA';
                            record.set('eleid', data.eleid);
                            win.parentByType.eleids.push(data.eleid);
                            win.parentByType.fileName.push(data.filename);
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
            WebUploader.Uploader.unRegister('inform');
        }
    }
});