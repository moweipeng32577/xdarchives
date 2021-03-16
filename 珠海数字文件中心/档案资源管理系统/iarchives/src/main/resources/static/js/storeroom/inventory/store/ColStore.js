/**
 * Created by zengdw on 2018/05/09 0001.
 */
Ext.define('Inventory.store.ColStore',{
    extend:'Ext.data.Store',
    model:'Inventory.model.ColModel',
    //pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/shelves/cols',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    },
    autoload:true,
    remoteSort:true
});