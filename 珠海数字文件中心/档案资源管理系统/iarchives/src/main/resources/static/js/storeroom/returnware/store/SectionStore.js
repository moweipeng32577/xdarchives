/**
 * Created by zengdw on 2018/05/09 0001.
 */
Ext.define('ReturnWare.store.SectionStore',{
    extend:'Ext.data.Store',
    model:'ReturnWare.model.SectionModel',
    //pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/shelves/sections',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    },
    autoload:true,
    remoteSort:true
});