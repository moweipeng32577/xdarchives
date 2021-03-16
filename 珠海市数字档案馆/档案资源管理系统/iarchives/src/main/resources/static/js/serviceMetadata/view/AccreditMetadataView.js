/**
 * Created by tanly on 2017/11/1 0024.
 */
Ext.define('ServiceMetadata.view.AccreditMetadataView', {
    extend: 'Ext.panel.Panel',
    xtype: 'accreditMetadataView',
    layout: 'border',

    items: [{
        region: 'west',
        width: XD.treeWidth,
        xtype: 'treepanel',
        itemId: 'treepanelId',
        store: 'AccreditMetadataTreeStore',
        collapsible: true,
        split: 1,
        header: false,
        bodyBorder: false
    }, {
        xtype: 'accreditMetadataPromptView',
        itemId: 'accreditMetadataPromptViewID'
    }]
});