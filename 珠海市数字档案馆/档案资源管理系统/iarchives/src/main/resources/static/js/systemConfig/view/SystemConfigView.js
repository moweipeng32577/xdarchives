/**
 * Created by tanly on 2017/11/1 0024.
 */
Ext.define('SystemConfig.view.SystemConfigView', {
    //extend: 'Ext.panel.Panel',
    xtype: 'systemConfigView',
    extend: 'Ext.tab.Panel',
    requires: [
        'Ext.layout.container.Border'
    ],
    tabPosition: 'top',
    tabRotation: 0,
    activeTab: 0,

    items: [{
        title: '档案系统',
        itemId: 'daxtId',
        layout: 'border',
        items: [{
            region: 'west',
            width: XD.treeWidth,
            xtype: 'treepanel',
            itemId: 'treepanelId',
            store: 'SystemConfigTreeStore',
            collapsible: true,
            split: 1,
            header: false,
            bodyBorder: false
        }, {
            region: 'center',
            layout: 'fit',
            header:false,
            xtype: 'systemConfigPromptView',
            itemId: 'systemConfigPromptViewID'
        }]

    },{
        title: '声像系统',
        itemId: 'sxxtId',
        layout: 'border',
        hidden:!openSxData,
        items: [{
            region: 'west',
            width: XD.treeWidth,
            xtype: 'treepanel',
            itemId: 'sxTreepanelId',
            store: 'SxSystemConfigTreeStore',
            collapsible: true,
            split: 1,
            header: false,
            bodyBorder: false
        }, {
            region: 'center',
            layout: 'fit',
            header:false,
            xtype: 'sxSystemConfigPromptView',
            itemId: 'sxSystemConfigPromptViewID'
        }]
    }]


});