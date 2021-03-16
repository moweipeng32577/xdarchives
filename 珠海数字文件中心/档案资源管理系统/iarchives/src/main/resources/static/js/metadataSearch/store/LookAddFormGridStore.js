/**
 * Created by yl on 2017/10/26.
 */
Ext.define('MetadataSearch.store.LookAddFormGridStore', {
    extend: 'Ext.data.Store',
    model: 'MetadataSearch.model.LookAddFormGridModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/electron/getEntryIndex',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});