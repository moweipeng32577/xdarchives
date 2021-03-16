/**
 * Created by tanly on 2017/12/1 0001.
 */
Ext.define('Outware.view.OutwareTabView', {
    extend: 'Ext.tab.Panel',
    xtype: 'outwareTabView',

    //标签页靠左配置--start
    tabPosition: 'top',
    tabRotation: 0,
    //标签页靠左配置--end

    activeTab: 0,
    items: [{
        title: '出库',
        xtype: 'panel',
        layout: 'fit',
        items: [{
            itemId:'waregrid',
            xtype: 'transferWareView'
        }]
    }, /*{
        title: '查档出库',
        xtype: 'panel',
        layout: 'fit',
        items: [{
            itemId:'opened',
            xtype: 'borrowWareView'
        }]
    },*/ {
        title: '出库历史记录查询',
        xtype: 'panel',
        layout: 'fit',
        items: [{
            itemId:'history',
            xtype: 'historyView'
        }]

    }]
});