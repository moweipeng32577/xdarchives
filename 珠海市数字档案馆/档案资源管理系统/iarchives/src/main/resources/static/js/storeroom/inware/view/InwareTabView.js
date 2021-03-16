/**
 * Created by tanly on 2017/12/1 0001.
 */
Ext.define('Inware.view.InwareTabView', {
    extend: 'Ext.tab.Panel',
    xtype: 'inwareTabView',

    //标签页靠左配置--start
    tabPosition: 'top',
    tabRotation: 0,
    //标签页靠左配置--end

    activeTab: 0,
    items: [{
        title: '新增入库',
        xtype: 'panel',
        layout: 'fit',
        items: [{
            itemId:'waregrid',
            //xtype: 'newInwareView'
            xtype: 'wareFormView'
        }]
    }, {
        title: '归还入库',
        xtype: 'panel',
        layout: 'fit',
        items: [{
            itemId:'opened',
            xtype: 'returnWareView'
        }]
    }, {
        title: '入库历史记录查询',
        xtype: 'panel',
        layout: 'fit',
        items: [{
            itemId:'history',
            xtype: 'historyView'
        }]
    }]
});