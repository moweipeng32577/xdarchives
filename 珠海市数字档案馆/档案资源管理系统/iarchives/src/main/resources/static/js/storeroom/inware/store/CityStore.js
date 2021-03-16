/**
 * Created by zengdw on 2018/05/09 0001.
 */
Ext.define('Inware.store.CityStore',{
    extend:'Ext.data.Store',
    model:'Inware.model.CityModel',
    //pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/shelves/findZones',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    },
    autoload:true,
    remoteSort:true
});