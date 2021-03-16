/**
 * Created by tanly on 2017/12/5 0023.
 */
var openStore = Ext.create("Ext.data.Store", {
    fields: ["Name", "Value"],
    data: [
        { text: "条目开放", Value: "条目开放" },
        { text: "拒绝", Value: "拒绝"}
    ]
});
Ext.define('OpenApprove.view.SetQxView', {
    extend: 'Ext.window.Window',
    xtype: 'setQxView',
    itemId:'setQxViewId',
    title: '开放设置',
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
                fieldLabel: "审批",
                itemId:'setQxId',
                margin:'15',
                store: openStore,
                editable: false,
                allowBlank: false,
                displayField: "text",
                valueField: "Value",
                emptyText: "--请选择--",
                queryMode: "local",
                listeners:{
                    afterrender:function(combo){
                        var store = combo.getStore();
                        if(store.getCount() > 0){
                            combo.select(store.getAt(0));
                        }
                    }
                }
            }
        ]
    }],

    buttons: [
        { text: '完成',itemId:'setQxAddSubmit'},
        { text: '关闭',itemId:'setQxAddClose'}
    ]
});