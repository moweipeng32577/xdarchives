/**
 * Created by yl on 2017/11/3.
 */
Ext.define('JyAdmins.view.JyAdminsView', {
    extend:'Ext.tab.Panel',
    xtype:'jyAdminsView',
    requires: [
        'Ext.layout.container.Border'
    ],
    //标签页靠左配置--start
    tabPosition:'top',
    tabRotation:0,
    //标签页靠左配置--end

    activeTab:0,

    items:[{
        title:'查档管理',
        layout: 'border',
        itemId:'dzJyId',
        items:[{
            xtype:'dzJyTreeView',
            bodyBorder: false
        },{
            xtype:'dzJyGridView'
        }]
    },
    //     {
    //     title:'实体查档管理',
    //     layout: 'border',
    //     itemId:'stJyId',
    //     items:[{
    //         xtype:'stJyTreeView',
    //         bodyBorder: false
    //     },{
    //         xtype:'stJyGridView'
    //     }]
    // },
        {
        title:'电子打印管理',
        layout: 'border',
        itemId:'dzPirntId',
        items:[{
            xtype:'dzPrintTreeView',
            bodyBorder: false
        },{
            xtype:'dzPrintGridView'
        }]
    }]
});