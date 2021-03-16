/**
 * Created by tanly on 2017/12/1 0001.
 */
Ext.define('Inventory.view.InventoryTabView', {
    extend: 'Ext.tab.Panel',
    xtype: 'inventoryTabView',

    //标签页靠左配置--start
    tabPosition: 'top',
    tabRotation: 0,
    //标签页靠左配置--end

    activeTab: 0,
    items: [{
        title: '新建盘点',
        xtype: 'panel',
        layout: 'fit',
        items: [{
            itemId:'waregrid',
            xtype: 'inventoryAddView'
        }]
    }, {
        title: '结果分析',
        xtype: 'panel',
        layout: 'fit',
        items: [{
            itemId:'opened',
            xtype: 'inventoryShowView'
        }]
    }]
});