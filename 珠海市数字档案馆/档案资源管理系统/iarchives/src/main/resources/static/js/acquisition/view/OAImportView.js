Ext.define('Acquisition.view.OAImportView', {
    extend: 'Ext.panel.Panel',
    xtype:'oAImportView',
    layout: 'border',
    bodyBorder: false,
    defaults: {
        collapsible: true,
        split: true
    },
    items: [
        {
            itemId:'oaImportGridViewId',
            collapsible: false,
            region: 'center',
            layout: 'fit',
            items: [{xtype:'oAImportGridView'}]
        }
    ]
});