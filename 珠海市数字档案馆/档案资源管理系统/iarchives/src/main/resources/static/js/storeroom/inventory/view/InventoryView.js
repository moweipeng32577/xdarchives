/**
 * Created by zengdw on 2018/05/09 0001.
 */

Ext.define('Inventory.view.InventoryView', {
    extend: 'Ext.panel.Panel',
    xtype: 'inventoryView',
    layout: 'card',
    activeItem: 0,
    items: [{
        layout: 'border',
        bodyBorder: false,
        defaults: {
            split: true
        },
        itemId: 'gridview',
        items: [{
            region: 'center',
            itemId: 'tabviewID',
            xtype: 'inventoryTabView'
        }]
    }]

});