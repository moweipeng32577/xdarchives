/**
 * Created by yl on 2017/11/2.
 */
Ext.define('SimpleSearch.store.MediaDtStore',{
    extend:'Ext.data.Store',
    // autoLoad: true,
    sortOnLoad: true,
    model:'SimpleSearch.model.MediaDataModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/simpleSearch/findMediaBySearchPlatform',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});