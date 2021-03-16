/**
 * Created by Rong on 2018/4/28.
 */
Ext.define('Moveware.store.ShelvesGridStore',{
    extend:'Ext.data.Store',
    model:'Moveware.model.ShelvesGridModel',
    pageSize: XD.pageSize,
    autoLoad:true,
    proxy: {
        type: 'ajax',
        url: '/shelves/zones',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});