Ext.define('ArchivesCallout.view.ArchivesCalloutAssemblyBatchForm', {
    extend: 'Ext.window.Window',
    xtype: 'ArchivesCalloutAssemblyBatchForm',
    itemId:'ArchivesCalloutAssemblyBatchForm',
    title: '调整流水线',
    frame: true,
    resizable: true,
    closeToolText:'关闭',
    width: 610,
    minWidth: 610,
    minHeight: 150,
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

    items: [{
        xtype: 'form',
        modelValidation: true,
        margin: '15',
        items: [
            {  fieldLabel: 'id',name:'id',hidden:true},
            {  xtype: "combobox",
                name: "assemblycode",
                fieldLabel: "流水线",
                store: 'AssemblyStore',
                editable: false,
                displayField: "title",
                valueField: "id",
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
    }],

    buttons: [
        { text: '提交',itemId:'add'},
        { text: '关闭',itemId:'close'}
    ]
});