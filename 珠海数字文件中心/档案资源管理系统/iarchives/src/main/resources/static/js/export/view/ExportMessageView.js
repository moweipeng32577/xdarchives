/**
 * Created by SunK on 2018/10/18 0018.
 */
Ext.define('Export.view.ExportMessageView', {
    extend: 'Ext.window.Window',
    xtype: 'ExportMessage',
    itemId: 'ExportMessage',
    title: '输入导出后的文件名',
    width: 450,
    height: 200,
    modal: true,
    closeToolText: '关闭',
    items: [{
        xtype: 'form',
        autoScroll: true,
        itemId:'form',
        layout: {
            type: 'vbox',
            align: 'stretch'
        },
        fieldDefaults: {
            labelWidth: 80
        },
        bodyPadding: 20,
        items: [{
            layout: 'column',
            items: [{
                columnWidth: 1,
                items: [{
                    name: 'userFileName',
                    xtype: 'textfield',
                    allowBlank: 'false',
                    fieldLabel: '文件名',
                    itemId:'userFileName',
                    emptyText: '请输入...',
                    //value:'请输入...',
                    style: 'width: 90%',
                    listeners:{
                        focus: function(b){
                            //获取焦点
                            b.setValue("")
                        }
                    }
                }]
            }]
        },{
            layout: 'column',
            items: [{
                columnWidth: .21,
                items: [{
                    xtype: 'checkbox',
                    boxLabel: '加密',
                    //columnWidth:'0.26',
                    itemId: 'addZipKey',
                    style: 'width: 90%',
                    handler:function (obj) {
                        var exportMessageView = obj.up('ExportMessage')
                        var isAddZipKey = exportMessageView.down('[itemId=addZipKey]').checked;
                        if(isAddZipKey==true){
                            exportMessageView.down('[itemId=zipPassword]').setDisabled(false)
                            // exportMessageView.down('[itemId=zipPassword]').setHidden(false)
                            // exportMessageView.down('[itemId=zipPassword]').setHideLabel(false)
                        }else {
                            exportMessageView.down('[itemId=zipPassword]').setDisabled(true)
                            // exportMessageView.down('[itemId=zipPassword]').setHidden(true)
                            // exportMessageView.down('[itemId=zipPassword]').setHideLabel(true)
                        }
                    }
                }]
            },{
                columnWidth: .77,
                items: [{
                    name: 'zipPassword',
                    xtype: 'textfield',
                    allowBlank: 'false',
                    //fieldLabel: '压缩密码',
                    disabled:true,
                    // hidden:true,
                    // hideLabel:true,
                    itemId:'zipPassword',
                    style: 'width: 90%'
                }]
            }]
        }]
    }],

    buttons: [{
        text: '导出',
        itemId: 'SaveExport'
    }, {
        text: '取消',
        itemId: 'cancelExport'
    }
    ]
});