/**
 * Created by Administrator on 2019/10/26.
 */

Ext.define('TransforAuditDeal.store.BatchModifyTemplatefieldStore',{
    extend:'Ext.data.Store',
    model:'TransforAuditDeal.model.BatchModifyTemplatefieldModel',
    proxy:({
        type: 'ajax',
        url: '/batchModify/getFilteredTemplateField',
        reader: {
            type: 'json'
        }
    }),
    idProperty: 'fieldname',
    fields: ['fieldcode','fieldname']
});
