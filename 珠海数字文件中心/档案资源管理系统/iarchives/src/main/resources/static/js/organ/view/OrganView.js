/**
 * Created by tanly on 2017/11/1 0024.
 */
Ext.define('Organ.view.OrganView', {
    extend: 'Ext.panel.Panel',
    xtype: 'organView',
    layout: 'border',
    bodyBorder: false,
    defaults: {
        collapsible: true,
        split: true
    },

    items: [{
        itemId: 'organTreeViewItemID',
        width: 240,
        header: false,
        region: 'west',
        floatable: false,
        layout: 'fit',
        items: [{xtype: 'organTreeView'}]
    }, {
        itemId: 'organPromptViewID',
        collapsible: false,
        region: 'center',
        layout: 'fit',
        items: [{xtype: 'organPromptView'}]
    }]
});