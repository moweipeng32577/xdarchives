/**
 * Created by RonJiang on 2017/11/2 0002.
 */
Ext.define('OriginalSearch.store.OriginalSearchGridStore',{
    extend:'Ext.data.Store',
    model:'OriginalSearch.model.OriginalSearchGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url:'/originalSearch/findBySearch',
        extraParams: {},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
