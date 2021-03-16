/**
 * Created by zengdw on 2018/05/09 0001.
 */
Ext.define('Inventory.store.CityStore',{
    extend:'Ext.data.Store',
    model:'Inventory.model.CityModel',
    //pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/shelves/zones',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    },
    autoload:true,
    remoteSort:true
});