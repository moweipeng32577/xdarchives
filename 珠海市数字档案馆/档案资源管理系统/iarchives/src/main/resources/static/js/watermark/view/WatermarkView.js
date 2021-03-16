/**
 * Created by tanly on 2017/11/1 0024.
 */
Ext.define('Watermark.view.WatermarkView', {
    extend: 'Ext.panel.Panel',
    xtype: 'WatermarkView',
    requires: [
        'Ext.layout.container.Border'
    ],
    layout: 'border',
    bodyBorder: false,
    defaults: {
        collapsible: true,
        split: true
    },

    items: [{
        itemId: 'WatermarkTreeViewItemID',
        width: 240,
        header: false,
        region: 'west',
        floatable: false,
        layout: 'fit',
        items: [{xtype: 'WatermarkTreeView'}]
    }, {
        itemId: 'WatermarkPromptViewID',
        collapsible: false,
        region: 'center',
        layout: 'fit',
        items: [{xtype: 'WatermarkPromptView'}]
    }]
});