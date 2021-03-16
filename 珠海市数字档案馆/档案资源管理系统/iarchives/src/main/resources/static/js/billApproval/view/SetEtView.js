/**
 * Created by Administrator on 2017/10/23 0023.
 */
var passStore = Ext.create("Ext.data.Store", {
    fields: ["Name", "Value"],
    data: [
        { text: "10年", Value: "10年" },
        { text: "30年", Value: "30年" },
        { text: "长期", Value: "长期" },
        { text: "永久", Value: "永久"}
    ]
});
Ext.define('BillApproval.view.SetEtView', {
    extend: 'Ext.window.Window',
    xtype: 'setEtView',
    itemId:'setEtViewId',
    title: '修改保管期限',
    frame: true,
    resizable: true,
    width: 250,
    minWidth: 250,
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
                fieldLabel: "保管期限",
                itemId:'setQxId',
                margin:'15',
                store: passStore,
                editable: false,
                allowBlank: false,
                displayField: "text",
                valueField: "Value",
                queryMode: "local",
                listeners:{
                    afterrender:function(combo){
                        var store = combo.getStore();
                        store.load(function(){
                            if(this.getCount() > 0){
                                combo.select(this.getAt(0));
                            }
                        });
                    }
                }
            }
        ]
    }],

    buttons: [
        { text: '完成',itemId:'setEtAddSubmit'},
        { text: '关闭',itemId:'setEtAddClose'}
    ]
});