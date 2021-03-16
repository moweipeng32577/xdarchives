/**
 * Created by zengdw on 2018/05/09 0001.
 */
Ext.define('Outware.store.OutwareStore',{
    extend:'Ext.data.Store',
    model:'Outware.model.OutwareModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/outware/outwares',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});