var errTypeStore = Ext.create("Ext.data.Store", {
    fields: ["Name", "Value"],
    data: [
        { Name: "整理/装订", Value: '整理/装订' },
        { Name: "扫描错误", Value: '扫描错误'},
        { Name: "质检错误", Value: '质检错误' },
        { Name: "编目错误", Value: '编目错误' },
    ]
});

var describeStore = Ext.create("Ext.data.Store", {
    fields: ["Name", "Value"],
    data: [
        { Name: "装订不牢固", Value: '装订不牢固' },
        { Name: "漏、跳编页码", Value: '漏、跳编页码'},
        { Name: "整理、装订顺序", Value: '整理、装订顺序' },
        { Name: "装订倒置", Value: '装订倒置' },
        { Name: "漏扫", Value: '漏扫' },
        { Name: "文件顺序", Value: '文件顺序'},
        { Name: "图像不清晰", Value: '图像不清晰' },
        { Name: "图像与实体不符合", Value: '图像与实体不符合' },
        { Name: "扫描图像不全", Value: '扫描图像不全' },

        { Name: "档案实体资料完整", Value: '漏、跳编页码'},
        { Name: "不整齐", Value: '整理、装订顺序' },
        { Name: "复印件标志", Value: '装订倒置' },
        { Name: "图像清晰", Value: '漏、跳编页码'},
        { Name: "图像处理(幅面、方向、整洁)", Value: '整理、装订顺序' },
        { Name: "原件不清标志", Value: '装订倒置' },
        { Name: "字轨案号", Value: '漏、跳编页码'},
        { Name: "页面未切割", Value: '整理、装订顺序' },
        { Name: "目录未与实体文件的顺序一致", Value: '装订倒置' },
        { Name: "漏编目", Value: '漏、跳编页码'},
        { Name: "编目重复", Value: '整理、装订顺序' }
    ]
});

Ext.define('DigitalInspection.view.DigitalInspectionDetailErrForm', {
    extend: 'Ext.window.Window',
    xtype: 'DigitalInspectionDetailErrForm',
    itemId:'DigitalInspectionDetailErrFormId',
    title: '新增错误信息',
    frame: true,
    resizable: true,
    closeToolText:'关闭',
    width: 350,
    height: 220,
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
                        { fieldLabel: 'batchcode',name:'batchcode',hidden:true},
                        { fieldLabel: 'mediaid',name:'mediaid',hidden:true},
                        {  xtype: "combobox",
                            name: "errtype",
                            fieldLabel: "错误类型",
                            store: errTypeStore,
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
                        {  xtype: "combobox",
                            name: "depict",
                            fieldLabel: "描述",
                            store: describeStore,
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
        { text: '提交',itemId:'errSubmit'},
        { text: '关闭',itemId:'errClose'}
    ]
});