/**
 * Created by Administrator on 2017/10/23 0023.
 */
var passStore = Ext.create("Ext.data.Store", {
    fields: ["Name", "Value"],
    data: [
        { text: "通过", Value: "通过" },
        { text: "退回", Value: "退回"}
    ]
});
Ext.define('Curator.view.ApproveAddView', {
    extend: 'Ext.window.Window',
    xtype: 'approveAddView',
    itemId:'approveAddViewId',
    title: '馆长审阅',
    frame: true,
    resizable: true,
    width: '80%',
    height: '70%',
    modal:true,
    closeToolText:'关闭',
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
    items: [{
        xtype: 'form',
        modelValidation: true,
        itemId:'selectApprove',
        items: [{
            fieldLabel: "批示",
            columnWidth: 1,
            itemId: 'approveId',
            xtype: 'textarea',
            labelWidth: 85,
            height:150,
            name: 'approve',
            readOnly: true
        }, {xtype: "combobox",
                fieldLabel: "审核结果",
                itemId:'selectApproveId',
                margin:'15',
                store: passStore,
                editable: false,
                allowBlank: false,
                displayField: "text",
                valueField: "Value",
                emptyText: "--请选择--",
                queryMode: "local"},
            {
                columnWidth: 1,
                itemId:'addproveId',
                xtype: 'textarea',
                fieldLabel: '添加批示',
                name:'addprove',
                margin:'15',
                flex: 1,
                margin: '5 0 0 0'
            }
        ]
    }],

    buttons: [
        { text: '完成',itemId:'approveAddSubmit'},
        { text: '关闭',itemId:'approveAddClose'}
    ]
});