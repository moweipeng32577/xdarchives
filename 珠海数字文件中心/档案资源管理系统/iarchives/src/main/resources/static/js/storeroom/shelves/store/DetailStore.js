/**
 * Created by Rong on 2018/4/28.
 */
Ext.define('Shelves.store.DetailStore',{
    extend:'Ext.data.Store',
    model:'Shelves.model.DetailGridModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/shelves/zone',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});