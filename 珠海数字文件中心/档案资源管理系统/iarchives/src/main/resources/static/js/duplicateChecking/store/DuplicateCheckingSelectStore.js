/**
 * Created by tanly on 2018/2/5 0005.
 */
Ext.define('DuplicateChecking.store.DuplicateCheckingSelectStore', {
    extend: 'Ext.data.Store',
    idProperty: 'fieldcode',
    fields: ['fieldcode', 'fieldname'],
    proxy: {
        type: 'ajax',
        url: '/summarization/getSelectedByNodeId',
        reader: {
            type: 'json'
        }
    },
    autoLoad: true
});