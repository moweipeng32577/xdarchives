/**
 * Created by zengdw on 2018/05/09 0001.
 */
Ext.define('ReturnWare.store.LayerStore',{
    extend:'Ext.data.Store',
    model:'ReturnWare.model.LayerModel',
    //pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/shelves/layers',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    },
    autoload:true,
    remoteSort:true
});