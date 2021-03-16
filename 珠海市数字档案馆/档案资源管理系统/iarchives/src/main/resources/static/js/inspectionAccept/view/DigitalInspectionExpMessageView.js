var exportTypeStore = Ext.create("Ext.data.Store", {
    fields: ["Name", "Value"],
    data: [
        { Name: "jpg", Value: 'jpg' },
        { Name: "tif", Value: 'tif'},
        { Name: "pdf", Value: 'pdf'}
    ]
});

Ext.define( 'DigitalInspection.view.DigitalInspectionExpMessageView', {
    extend: 'Ext.window.Window',
    xtype: 'DigitalInspectionExpMessageView',
    itemId: 'DigitalInspectionExpMessageView',
    title: '输入导出后的文件名',
    width: 450,
    height: 300,
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
                    style: 'width: 100%',
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
                    style: 'width: 100%',
                    handler:function (obj) {
                        var exportMessageView = obj.up('DigitalInspectionExpMessageView')
                        var isAddZipKey = exportMessageView.down('[itemId=addZipKey]').checked;
                        if(isAddZipKey==true){
                            exportMessageView.down('[itemId=zipPassword]').setDisabled(false)
                        }else {
                            exportMessageView.down('[itemId=zipPassword]').setDisabled(true)
                        }
                    }
                }]
            },{
                columnWidth: .79,
                items: [{
                    name: 'zipPassword',
                    xtype: 'textfield',
                    allowBlank: 'false',
                    //fieldLabel: '压缩密码',
                    disabled:true,
                    // hidden:true,
                    // hideLabel:true,
                    itemId:'zipPassword',
                    style: 'width: 100%'
                }]
            }]
        },
            {
                xtype: "combobox",
                name: "exporttype",
                fieldLabel: "导出格式选择",
                store: exportTypeStore,
                editable: false,
                displayField: "Name",
                valueField: "Value",
                queryMode: "local",
                afterLabelTextTpl: ['<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'],
                listeners: {
                    afterrender: function (combo) {
                        var store = combo.getStore();
                        if (store.getCount() > 0) {
                            combo.select(store.getAt(0));
                        }
                    }
                }
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