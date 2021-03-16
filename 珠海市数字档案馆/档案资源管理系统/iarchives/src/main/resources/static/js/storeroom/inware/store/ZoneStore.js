/**
 * Created by zengdw on 2018/05/09 0001.
 */
Ext.define('Inware.store.ZoneStore',{
    extend:'Ext.data.Store',
    model:'Inware.model.ZoneModel',
    //pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/shelves/xlZones',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    },
    autoload:true,
    remoteSort:true
});