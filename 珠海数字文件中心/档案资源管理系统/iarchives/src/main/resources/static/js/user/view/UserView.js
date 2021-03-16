/**
 * Created by xd on 2017/10/21.
 */
Ext.define('User.view.UserView', {
    extend: 'Ext.panel.Panel',
    xtype:'userView',
    layout: 'border',
    bodyBorder: false,
    defaults: {
        collapsible: true,
        split: true
    },
    items: [
        {
            itemId:'treeViewId',
            width: 240,
            title: '(按下Ctrl+F可查找)',
            region: 'west',
            floatable: false,
            header:false,
            margin: '0 0 0 0',
            layout: 'fit',
            items: [{xtype: 'userTreeView'}]
        },
        {
            itemId:'userPromptViewId',
            collapsible: false,
            region: 'center',
            margin: '0 0 0 0',
            layout: 'fit',
            items: [{xtype:'userPromptView'}]
        }
    ]
});
