/**
 * Created by Rong on 2018/4/28.
 */
Ext.define('Inventory.store.InventoryResultStore',{
    extend:'Ext.data.Store',
    model:'Inventory.model.InventoryResultModel',
    pageSize: XD.pageSize,
    //autoLoad:true,
    proxy: {
        type: 'ajax',
        url: '/inventory/resultShow',
        /*extraParams: {
            chickid:'',
            resulttype:''
        },*/
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});