/**
 * Created by Administrator on 2019/10/26.
 */


Ext.define('TransforAuditDeal.store.DuplicateCheckingSelectStore', {
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
