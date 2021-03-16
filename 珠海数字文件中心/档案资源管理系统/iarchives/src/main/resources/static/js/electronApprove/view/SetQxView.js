/**
 * Created by Administrator on 2017/10/23 0023.
 */
var lookStore = Ext.create("Ext.data.Store", {
    fields: ["Name", "Value"],
    data: [
        { text: "查看", Value: "查看" },
        { text: "拒绝", Value: "拒绝"}
    ]
});
Ext.define('ElectronApprove.view.SetQxView', {
    extend: 'Ext.window.Window',
    xtype: 'setQxView',
    itemId:'setQxViewId',
    title: '利用权限设置',
    frame: true,
    resizable: true,
    width: 300,
    minWidth: 300,
    minHeight: 100,
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
                fieldLabel: "利用权限",
                itemId:'setQxId',
                margin:'15',
                store: lookStore,
                editable: false,
                allowBlank: false,
                displayField: "text",
                valueField: "Value",
                emptyText: "--请选择--",
                queryMode: "local"}
        ]
    }],

    buttons: [
        { text: '完成',itemId:'setQxAddSubmit'},
        { text: '关闭',itemId:'setQxAddClose'}
    ]
});