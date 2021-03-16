/**
 * Created by xd on 2017/10/21.
 */
Ext.define('Log.view.LogView', {
    extend: 'Ext.tab.Panel',
    xtype:'logView',
    itemId:'logGridViewId',
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
                layout:'fit',
                region: 'center',
                xtype: 'logGridView'
            }
        ]
    }, {
        title: '声像系统',
        layout: 'border',
        itemId: 'sxxtId',
        hidden:!openSxData,
        items: [
            {
                layout:'fit',
                region: 'center',
                xtype: 'logGridView'
            }
        ]
    },{
        title: '新闻系统',
        layout: 'border',
        itemId: 'xwxtId',
        hidden:!openSxData,
        items: [
            {
                layout:'fit',
                region: 'center',
                xtype: 'logGridView'
            }
        ]
    }]
});
