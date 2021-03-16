/**
 * Created by yl on 2017/10/26.
 */
Ext.define('PavilionSearch.store.LookAddMxGridStore', {
    extend: 'Ext.data.Store',
    model: 'PavilionSearch.model.LookAddMxGridModel',
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