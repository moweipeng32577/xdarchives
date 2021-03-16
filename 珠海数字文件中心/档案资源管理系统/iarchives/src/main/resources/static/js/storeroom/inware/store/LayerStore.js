/**
 * Created by zengdw on 2018/05/09 0001.
 */
Ext.define('Inware.store.LayerStore',{
    extend:'Ext.data.Store',
    model:'Inware.model.LayerModel',
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