// var assemblyStore = Ext.create("Ext.data.Store", {
//     fields: ["Name", "Value"],
//     data: [
//         { Name: "数字化加工流程", Value: '2018112609292881' },
//     ]
// });

var archiveTypeStore = Ext.create("Ext.data.Store", {
    fields: ["Name", "Value"],
    data: [
        { Name: "按卷", Value: '按卷' },
        { Name: "按件", Value: '按件'}
    ]
});

var timeTypeStore = Ext.create("Ext.data.Store", {
    fields: ["Name", "Value"],
    data: [
        { Name: "批量", Value: '批量' },
        { Name: "非急", Value: '非急'},
        { Name: "特急", Value: '特急' },
        { Name: "追加", Value: '追加' },
        { Name: "已还实体", Value: '已还实体'},
    ]
});

var textTpl = ['<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'];
Ext.define('ArchivesCallout.view.ArchivesCalloutAddBatchForm', {
    extend: 'Ext.window.Window',
    xtype: 'ArchivesCalloutAddBatchForm',
    itemId:'ArchivesCalloutAddBatchForm',
    title: '调档',
    frame: true,
    resizable: true,
    closeToolText:'关闭',
    width: 610,
    minWidth: 610,
    height: '93%',
    modal:true,
    items: [
        {
            xtype: 'container',
            layout:'form',
            height:'100%',
            style: 'overflow:auto;',
            items: [
                {
                    xtype: 'form',
                    layout:'form',
                    height:'100%',
                    items: [
                        {xtype: 'textfield', fieldLabel: 'id',name:'id',hidden:true},
                        {xtype: 'textfield', fieldLabel: 'batchcode',name:'batchcode',hidden:true},
                        {xtype: 'textfield', fieldLabel: 'assembly',name:'assembly',hidden:true},
                        {xtype: 'textfield', fieldLabel: 'searchivescode',name:'searchivescode',hidden:true},
                        {xtype: 'textfield', fieldLabel: 'lendcopies',name:'lendcopies',hidden:true},
                        {xtype: 'textfield', fieldLabel: 'lendpages',name:'lendpages',hidden:true},
                        {xtype: 'textfield', fieldLabel: 'returncopies',name:'returncopies',hidden:true},
                        {xtype: 'textfield', fieldLabel: 'returnpages',name:'returnpages',hidden:true},
                        {xtype: 'textfield', fieldLabel: 'returntime',name:'returntime',hidden:true},
                        {xtype: 'textfield', fieldLabel: 'returncrew',name:'returncrew',hidden:true},
                        {xtype: 'textfield', fieldLabel: 'returnsuperior',name:'returnsuperior',hidden:true},
                        {xtype: 'textfield', fieldLabel: 'returnexplain',name:'returnexplain',hidden:true},
                        {xtype: 'textfield', fieldLabel: 'returnstatus',name:'returnstatus',hidden:true},
                        {xtype: 'textfield', fieldLabel: 'batchstatus',name:'batchstatus',hidden:true},
                        {xtype: 'textfield', fieldLabel: 'connectstatus',name:'connectstatus',hidden:true},
                        {xtype: 'textfield', fieldLabel: '批次名',name:'batchname'},
                        {xtype: 'textfield', fieldLabel: '档案份数',name:'ajcopies',allowBlank: false, afterLabelTextTpl: textTpl},
                        {xtype: 'textfield', fieldLabel: '页数',name:'pages',allowBlank: false, afterLabelTextTpl: textTpl},
                        {xtype: 'textfield',fieldLabel: '漏号',name:'spillagecode' },
                        {xtype: "combobox",
                            name: "timetype",
                            fieldLabel: "时间类型",
                            store: timeTypeStore,
                            editable: false,
                            displayField: "Name",
                            valueField: "Value",
                            queryMode: "local",
                            afterLabelTextTpl: textTpl,
                            listeners: {
                                afterrender: function (combo) {
                                    var store = combo.getStore();
                                    if (store.getCount() > 0) {
                                        combo.select(store.getAt(0));
                                    }
                                }
                            }
                        },
                        {xtype: 'textfield', fieldLabel: '借出时间',name:'lendtime',allowBlank: false, afterLabelTextTpl: textTpl},
                        {xtype: 'textfield', fieldLabel: '借档人',name:'lender',allowBlank: false, afterLabelTextTpl: textTpl},
                        {xtype: 'textfield', fieldLabel: '借档管理员',name:'lendadmin'},
                        {xtype: 'textfield', fieldLabel: '借档监理',name:'lendsuperior'},
                        {xtype: 'textfield', fieldLabel: '借出说明',name:'lendexplain'},
                        {xtype: "combobox",
                            name: "assemblycode",
                            fieldLabel: "流水线",
                            store: 'AssemblyStore',
                            editable: false,
                            displayField: "title",
                            valueField: "code",
                            queryMode: "local",
                            afterLabelTextTpl: textTpl,
                            listeners: {
                                afterrender: function (combo) {
                                    var store = combo.getStore();
                                    if (store.getCount() > 0) {
                                        combo.select(store.getAt(0));
                                    }
                                }
                            }
                        },
                        {xtype: "combobox",
                            name: "archivetype",
                            fieldLabel: "档案类型",
                            store: archiveTypeStore,
                            editable: false,
                            displayField: "Name",
                            valueField: "Value",
                            queryMode: "local",
                            afterLabelTextTpl: textTpl,
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
                }
            ]
        },
    ],

    buttons: [
        { text: '提交',itemId:'batchAddSubmit'},
        { text: '关闭',itemId:'batchAddClose'}
    ]
});