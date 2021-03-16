/**
 * Created by yl on 2017/10/26.
 */
Ext.define('SimpleSearch.store.LookAddFormGridStore', {
    extend: 'Ext.data.Store',
    model: 'SimpleSearch.model.LookAddFormGridModel',
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