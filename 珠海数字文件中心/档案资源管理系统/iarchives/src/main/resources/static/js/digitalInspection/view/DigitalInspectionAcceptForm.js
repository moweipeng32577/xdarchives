var archiveTypeStore = Ext.create("Ext.data.Store", {
    fields: ["Name", "Value"],
    data: [
        { Name: "案卷", Value: '案卷' },
        { Name: "按件", Value: '按件'}
    ]
});
Ext.define('DigitalInspection.view.DigitalInspectionAcceptForm', {
    extend: 'Ext.window.Window',
    xtype: 'DigitalInspectionAcceptForm',
    itemId:'DigitalInspectionAcceptFormId',
    title: '设置页数',
    frame: true,
    resizable: true,
    closeToolText:'关闭',
    width: 500,
    minHeight: 100,
    modal:true,
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
            margin: '15',
            items: [
                        { fieldLabel: 'id',name:'id',hidden:true},
                        { fieldLabel: '页数',name:'pages', allowBlank: false,
                            afterLabelTextTpl: ['<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'],
                            regex: /^[1-9]\d*$/,
                            regexText: '请输入0-100整数'
                        },
                    ]
        },

    ],

    buttons: [
        { text: '提交',itemId:'submit'},
        { text: '关闭',itemId:'close'}
    ]
});