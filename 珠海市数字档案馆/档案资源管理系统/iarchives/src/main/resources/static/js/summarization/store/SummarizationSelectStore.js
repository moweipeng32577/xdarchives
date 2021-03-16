/**
 * Created by tanly on 2018/2/2 0002.
 */
Ext.define('Summarization.store.SummarizationSelectStore', {
    extend: 'Ext.data.Store',
    idProperty: 'fieldname',
    fields: ['fieldname'],
    proxy: {
        type: 'ajax',
        url: '/summarization/getSelectedByNodeId',
        reader: {
            type: 'json'
        }
    },
    autoLoad: true
});