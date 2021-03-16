/**
 * Created by Administrator on 2019/6/13.
 */
Ext.define('Accept.view.AcceptView',{
    extend: 'Ext.tab.Panel',
    xtype:'AcceptView',
/*    requires: [
        'Ext.layout.container.Border'
    ],*/
    tabPosition: 'top',
    tabRotation: 0,
    activeTab: 0,
    items: [{
        title: '接收单据',
        layout: 'fit',
        itemId: 'acceptdoc',
        xtype: 'AcceptdocView'
    }, {
        title: '正在消毒',
        layout: 'fit',
        itemId: 'sterilizing',
        xtype: 'SterilizingView'
    }
        ,{
            title: '已消毒',
            layout: 'fit',
            itemId: 'finishsterilize',
            xtype: 'FinishsterilizeView'
        }
        ,{
            title: '已入库',
            layout: 'fit',
            itemId: 'finishstore',
            xtype: 'FinishstoreView'
        }
    ]
});