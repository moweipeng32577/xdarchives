/**
 * Created by zengdw on 2018/05/09 0001.
 */
Ext.define('Inware.store.InwareStore',{
    extend:'Ext.data.Store',
    model:'Inware.model.InwareModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/inware/inwares',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});