/**
 * Created by tanly on 2017/11/1 0024.
 */
Ext.define('Classificationsetting.view.ClassificationsettingView', {
    //extend: 'Ext.panel.Panel',
    //layout: 'border',
    extend: 'Ext.tab.Panel',
    xtype: 'classificationsettingView',

    requires: [
        'Ext.layout.container.Border'
    ],
    tabPosition: 'top',
    tabRotation: 0,
    activeTab: 0,

    items: [{
        title: '档案系统',
        layout: 'border',
        itemId: 'daxtId',
        items: [{
            region: 'west',
            width: XD.treeWidth,
            xtype: 'treepanel',
            itemId: 'treepanelDaId',
            store: 'ClassificationsettingTreeStore',
            collapsible: true,
            split: 1,
            header: false,
            bodyBorder: false
        }, {
            itemId: 'gridDaId',
            xtype: 'classificationsettingPromptView'
        }]
    }, {
        title: '声像系统',
        layout: 'border',
        itemId: 'sxxtId',
        hidden:!openSxData,
        items: [{
            region: 'west',
            width: XD.treeWidth,
            xtype: 'treepanel',
            itemId: 'treepanelSxId',
            store: 'ClassificationsettingTreeStore',
            collapsible: true,
            split: 1,
            header: false,
            bodyBorder: false
        }, {
            itemId: 'gridSxId',
            xtype: 'classificationsettingPromptView'
        }]
    }]

});