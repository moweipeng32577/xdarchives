/**
 * Created by zengdw on 2018/05/09 0001.
 */
Ext.define('ReturnWare.store.UnitStore',{
    extend:'Ext.data.Store',
    model:'ReturnWare.model.UnitModel',
    //pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/shelves/units',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    },
    autoload:true,
    remoteSort:true
});