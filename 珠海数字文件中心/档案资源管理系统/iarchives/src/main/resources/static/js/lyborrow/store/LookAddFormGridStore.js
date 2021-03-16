/**
 * Created by yl on 2017/10/26.
 */
Ext.define('Borrow.store.LookAddFormGridStore', {
    extend: 'Ext.data.Store',
    model: 'Borrow.model.LookAddFormGridModel',
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