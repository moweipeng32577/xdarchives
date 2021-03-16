/**
 * Created by yl on 2017/10/26.
 */
Ext.define('ClassifySearch.store.ClassifyLookAddFormGridStore', {
    extend: 'Ext.data.Store',
    model: 'ClassifySearch.model.ClassifyLookAddFormGridModel',
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