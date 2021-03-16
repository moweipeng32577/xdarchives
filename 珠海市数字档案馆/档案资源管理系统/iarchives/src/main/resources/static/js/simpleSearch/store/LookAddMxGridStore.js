/**
 * Created by yl on 2017/10/26.
 */
Ext.define('SimpleSearch.store.LookAddMxGridStore', {
    extend: 'Ext.data.Store',
    model: 'SimpleSearch.model.LookAddMxGridModel',
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