/**
 * Created by tanly on 2017/10/24 0024.
 */
Ext.define('Codesetting.view.CodesettingView', {
    //extend: 'Ext.panel.Panel',
    xtype: 'codesettingView',
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
        requires: ['Ext.layout.container.Border'],
        layout: 'border',
        bodyBorder: false,

        defaults: {
            collapsible: true,
            split: true
        },

        items: [{
            itemId: 'codesettingTreeViewItemID',
            width: XD.treeWidth,
            title: '',
            header: false,
            region: 'west',
            floatable: false,
            layout: 'fit',
            items: [{xtype: 'codesettingTreeView'}]
        }, {
            itemId: 'codesettingPromptViewID',
            collapsible: false,
            region: 'center',
            layout: 'fit',
            items: [{xtype: 'codesettingPromptView'}]
        }]
    },{
        title: '声像系统',
        itemId: 'sxxtId',
        requires: ['Ext.layout.container.Border'],
        layout: 'border',
        bodyBorder: false,
        hidden:!openSxData,
        defaults: {
            collapsible: true,
            split: true
        },

        items: [{
            itemId: 'codesettingSxTreeViewItemID',
            width: XD.treeWidth,
            title: '',
            header: false,
            region: 'west',
            floatable: false,
            layout: 'fit',
            items: [{xtype: 'codesettingSxTreeView'}]
        }, {
            itemId: 'codesettingSxPromptViewID',
            collapsible: false,
            region: 'center',
            layout: 'fit',
            items: [{xtype: 'codesettingSxPromptView'}]
        }]
    }]

});