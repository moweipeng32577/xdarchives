/**
 * Created by Administrator on 2017/10/23 0023.
 */
var passStore = Ext.create("Ext.data.Store", {
    fields: ["Name", "Value"],
    data: [
        { text: "销毁", Value: "1" },
        { text: "维持", Value: "6" },
        { text: "变更", Value: "0" }
    ]
});
Ext.define('BillApproval.view.SetQxView', {
    extend: 'Ext.window.Window',
    xtype: 'setQxView',
    itemId:'setQxViewId',
    title: '审核设置',
    frame: true,
    resizable: true,
    width: 380,
    minWidth: 250,
    minHeight: 140,
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
                fieldLabel: "状态",
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
            },{  xtype: "combobox",
                fieldLabel: "销毁鉴定依据",
                itemId:'destructionAppraiseType',
                margin:'15',
                store: Ext.create('Ext.data.Store',{
                    proxy: {
                        type:'ajax',
                        url:'/systemconfig/getByConfigcode',
                        extraParams:{configcode:'销毁鉴定依据'},
                        reader: {
                            type:'json'
                        }
                    },
                    autoLoad:true
                }),
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
        { text: '完成',itemId:'setQxAddSubmit'},
        { text: '关闭',itemId:'setQxAddClose'}
    ]
});