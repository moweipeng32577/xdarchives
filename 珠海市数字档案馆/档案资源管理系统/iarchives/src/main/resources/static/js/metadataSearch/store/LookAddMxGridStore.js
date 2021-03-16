/**
 * Created by yl on 2017/10/26.
 */
Ext.define('MetadataSearch.store.LookAddMxGridStore', {
    extend: 'Ext.data.Store',
    model: 'MetadataSearch.model.LookAddMxGridModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/electron/getBoxEntryIndex',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});