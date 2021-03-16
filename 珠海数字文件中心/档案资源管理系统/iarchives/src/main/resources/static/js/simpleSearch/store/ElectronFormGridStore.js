/**
 * Created by yl on 2017/10/26.
 */
Ext.define('SimpleSearch.store.ElectronFormGridStore', {
    extend: 'Ext.data.Store',
    model: 'SimpleSearch.model.ElectronFormGridModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/electron/getBorrowEntryIndex',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});