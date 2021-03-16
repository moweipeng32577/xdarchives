var archiveTypeStore = Ext.create("Ext.data.Store", {
    fields: ["Name", "Value"],
    data: [
        { Name: "按件", Value: '按件'},
        { Name: "案卷", Value: '案卷' }
    ]
});
Ext.define('DigitalInspection.view.DigitalInspectionAddBatchForm', {
    extend: 'Ext.window.Window',
    xtype: 'DigitalInspectionAddBatchForm',
    itemId:'DigitalInspectionAddBatchFormId',
    title: '新增批次',
    frame: true,
    resizable: true,
    closeToolText:'关闭',
    width: 610,
    minWidth: 610,
    minHeight: 250,
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
                        { fieldLabel: '批次号',name:'batchcode',readOnly:true},
                        { fieldLabel: '抽检员',name:'inspector',readOnly:true},
                        { fieldLabel: '批次名',name:'batchname', allowBlank: false,
                            afterLabelTextTpl: ['<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>']
                        },
                        {  xtype: "combobox",
                            name: "archivetype",
                            fieldLabel: "档案类型",
                            store: archiveTypeStore,
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
                        }
                    ]
        },

    ],

    buttons: [
        { text: '提交',itemId:'batchAddSubmit'},
        { text: '关闭',itemId:'batchAddClose'}
    ]
});