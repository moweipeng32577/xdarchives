var samplingTypeStore = Ext.create("Ext.data.Store", {
    fields: ["Name", "Value"],
    data: [
        { Name: "份数", Value: '份数'},
        { Name: "页数", Value: '页数' }
    ]
});
Ext.define('DigitalInspection.view.DigitalInspectionSamplingForm', {
    extend: 'Ext.window.Window',
    xtype: 'DigitalInspectionSamplingForm',
    itemId:'DigitalInspectionSamplingFormId',
    title: '抽检',
    frame: true,
    resizable: true,
    closeToolText:'关闭',
    width: 310,
    height: 300,
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
                {fieldLabel: 'batchcodes', name: 'batchcodes', hidden: true},
                {
                    fieldLabel: '抽检率(%)', name: 'checkcount', allowBlank: false,
                    afterLabelTextTpl: ['<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'],
                    regex: /^([1-9][0-9]{0,1}|100)$/,
                    regexText: '请输入0-100整数'
                },
                {
                    xtype: "combobox",
                    name: "samplingtype",
                    fieldLabel: "抽检类型",
                    store: samplingTypeStore,
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
                },
                {
                    xtype: "combobox",
                    name: "checkgroupid",
                    fieldLabel: "质检小组",
                    store: 'CheckGroupStore',
                    editable: false,
                    displayField: "groupname",
                    valueField: "checkgroupid",
                    queryMode: "all",
                    afterLabelTextTpl: ['<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'],
                    listeners: {
                        afterrender: function (combo) {
                            var store = combo.getStore();
                            setTimeout(function(){
                            if (store.getCount() > 0) {
                                combo.select(store.getAt(0));
                            }
                            },500);
                        },
                        beforerender: function (combo) {
                            var store = combo.getStore();
                            store.removeAll();
                            store.load();
                        }
                    }
                }
                    ]
        }

    ],

    buttons: [
        { text: '提交',itemId:'samplingSubmit'},
        { text: '关闭',itemId:'samplingClose'}
    ]
});