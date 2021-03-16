/**
 * Created by yl on 2017/10/26.
 */
Ext.define('ClassifySearch.store.ClassifyLookAddMxGridStore', {
    extend: 'Ext.data.Store',
    model: 'ClassifySearch.model.ClassifyLookAddMxGridModel',
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