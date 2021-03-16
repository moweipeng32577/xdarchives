/**
 * Created by Rong on 2018/4/28.
 */
Ext.define('Inventory.store.InventoryStore',{
    extend:'Ext.data.Store',
    model:'Inventory.model.InventoryModel',
    pageSize: XD.pageSize,
    autoLoad:true,
    proxy: {
        type: 'ajax',
        url: '/inventory/show',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});