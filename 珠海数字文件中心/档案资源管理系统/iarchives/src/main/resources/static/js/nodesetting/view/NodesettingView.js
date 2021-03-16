/**
 * Created by tanly on 2017/10/24 0024.
 */
Ext.define('Nodesetting.view.NodesettingView', {
    // extend: 'Ext.panel.Panel',
    // layout: 'border',
    // bodyBorder: false,
    // defaults: {
    //     collapsible: true,
    //     split: true
    // },

    extend: 'Ext.tab.Panel',
    xtype:'nodesettingView',

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
        items: [
            {
                itemId:'nodesettingTreeViewItemID',
                width: 300,
                header:false,
                region: 'west',
                floatable: false,
                layout: 'fit',
                items: [{
                    xtype:'nodesettingTreeView',
                    itemId:'daTree',
                    bodyBorder: false
                }]
            },
            {
                itemId: 'nodesettingPromptViewID',
                collapsible: false,
                region: 'center',
                layout: 'fit',
                items: [{xtype:'nodesettingPromptView'}]
            }
        ]
    }, {
        title: '声像系统',
        layout: 'border',
        itemId: 'sxxtId',
        hidden:!openSxData,
        items: [
            {
                itemId:'nodesettingSxTreeViewItemID',
                width: 300,
                header:false,
                region: 'west',
                floatable: false,
                layout: 'fit',
                items: [{
                    xtype:'nodesettingSxTreeView',
                    itemId:'sxTree',
                    bodyBorder: false
                }]
            },
            {
                itemId: 'nodesettingSxPromptViewID',
                collapsible: false,
                region: 'center',
                layout: 'fit',
                items: [{xtype:'nodesettingSxPromptView'}]
            }
        ]
    }]
});