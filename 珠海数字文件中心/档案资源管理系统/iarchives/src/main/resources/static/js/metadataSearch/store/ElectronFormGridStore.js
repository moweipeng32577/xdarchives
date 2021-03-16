/**
 * Created by yl on 2017/10/26.
 */
Ext.define('MetadataSearch.store.ElectronFormGridStore', {
    extend: 'Ext.data.Store',
    model: 'MetadataSearch.model.ElectronFormGridModel',
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