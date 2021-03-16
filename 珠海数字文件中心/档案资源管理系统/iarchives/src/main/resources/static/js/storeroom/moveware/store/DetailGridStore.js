/**
 * Created by Rong on 2018/4/28.
 */
Ext.define('Moveware.store.DetailGridStore',{
    extend:'Ext.data.Store',
    model:'Moveware.model.DetailGridModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/shelves/zone',
        //url: '/shelves/zoneshel',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});