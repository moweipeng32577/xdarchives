
Ext.define('ThematicUtilize.view.ThematicUtilizeViews', {
    extend: 'Ext.panel.Panel',
    xtype: 'thematicUtilizeViews',
    layout: 'border',
    bodyBorder:false,
    defaults:{
        collapsible:true,
        split:true
    },
    items: [
        {
            width: 240,
            minWidth: 240,
            title: '',
            header:false,
            region: 'west',
            floatable: false,
            layout: 'fit',
            items: [{xtype: 'ThematicUtilizeTreeView'}]
        },
        {
            collapsible: false,
            region: 'center',
            layout: 'fit',
            items: [{xtype: 'thematicUtilizeView'}]
        }
    ]
});