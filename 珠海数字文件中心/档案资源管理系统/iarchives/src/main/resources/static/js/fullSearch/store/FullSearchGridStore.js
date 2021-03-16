/**
 * Created by tanly on 2017/11/17 0002.
 */
Ext.define('FullSearch.store.FullSearchGridStore',{
    extend:'Ext.data.Store',
    model:'FullSearch.model.FullSearchGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url:'/fullSearch/search',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
