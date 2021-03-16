var stickStore = Ext.create("Ext.data.Store", {
    fields: ["Name", "Value"],
    data: [
        { Name: "1", Value: '1' },
        { Name: "2", Value: '2'},
        { Name: "3", Value: '3'}
    ]
});

Ext.define('QuestionnaireManagement.view.StickView', {
    extend: 'Ext.window.Window',
    xtype: 'stickView',
    itemId: 'stickView',
    title: '置顶',
    frame: true,
    resizable: true,
    closeToolText: '关闭',
    width: 320,
    height:160,
    modal: true,
    layout: 'fit',
    items: [{
        xtype: 'form',
        layout: {
            type: 'vbox',
            align: 'stretch'
        },
        bodyPadding: 15,
        itemId: 'formId',
        items: [
            {  xtype: "combobox",
                name: "stick",
                fieldLabel: "置顶等级",
                store: stickStore,
                editable: false,
                displayField: "Name",
                valueField: "Value",
                queryMode: "local",
                listeners:{
                    afterrender:function(combo){
                        var store = combo.getStore();
                        store.load(function(data){
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
        {text: '确定', itemId: 'stickSubmit'},
        {text: '关闭', itemId: 'stickClose'}
    ]
});