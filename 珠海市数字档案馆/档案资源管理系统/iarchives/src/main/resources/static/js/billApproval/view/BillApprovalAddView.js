/**
 * Created by Administrator on 2017/10/23 0023.
 */
var passStore = Ext.create("Ext.data.Store", {
    fields: ["Name", "Value"],
    data: [
        { text: "通过", Value: "通过" },
        { text: "驳回", Value: "驳回"}
    ]
});
Ext.define('BillApproval.view.BillApprovalAddView', {
    extend: 'Ext.window.Window',
    xtype: 'billApprovalAddView',
    itemId:'billApprovalAddViewId',
    title: '添加批示',
    frame: true,
    resizable: true,
    width: 300,
    minWidth: 300,
    minHeight: 230,
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
        items: [
            {  xtype: "combobox",
                fieldLabel: "评语",
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
                itemId:'approveId',
                xtype: 'textarea',
                fieldLabel: '批示',
                name:'approve',
                margin:'15',
                flex: 1,
                margin: '5 0 0 0'
                // disabled:true
            }
        ]
    }],

    buttons: [
        { text: '完成',itemId:'approveAddSubmit'},
        { text: '关闭',itemId:'approveAddClose'}
    ]
});