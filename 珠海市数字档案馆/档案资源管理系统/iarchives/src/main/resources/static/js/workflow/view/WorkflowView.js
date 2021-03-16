/**
 * Created by xd on 2017/10/21.
 */
Ext.define('Workflow.view.WorkflowView', {
    //extend: 'Ext.panel.Panel',
    xtype: 'workflowView',
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
        bodyBorder: false,
        defaults: {
            collapsible: true,
            split: true
        },
        items: [
            {
                itemId: 'treeViewId',
                width: 240,
                title: '(按下Ctrl+F可查找)',
                header: false,
                region: 'west',
                floatable: false,
                margin: '0 0 0 0',
                layout: 'fit',
                items: [{xtype: 'workflowTreeView'}]
            },
            {
                itemId: 'workflowPromptViewId',
                collapsible: false,
                region: 'center',
                margin: '0 0 0 0',
                layout: 'fit',
                items: [{xtype: 'workflowPromptView'}]
            }
        ]

    }, {
        title: '声像系统',
        itemId: 'sxxtId',
        layout: 'border',
        hidden:!openSxData,
        layout: 'border',
        bodyBorder: false,
        defaults: {
            collapsible: true,
            split: true
        },
        items: [
            {
                itemId: 'sxTreeViewId',
                width: 240,
                title: '(按下Ctrl+F可查找)',
                header: false,
                region: 'west',
                floatable: false,
                margin: '0 0 0 0',
                layout: 'fit',
                items: [{xtype: 'workflowSxTreeView'}]
            },
            {
                itemId: 'workflowSxPromptViewId',
                collapsible: false,
                region: 'center',
                margin: '0 0 0 0',
                layout: 'fit',
                items: [{xtype: 'workflowSxPromptView'}]
            }
        ]

    }]
});
