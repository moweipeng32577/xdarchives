/**
 * Created by Administrator on 2019/10/26.
 */


Ext.define('TransforAuditDeal.store.BatchModifyTemplateEnumfieldStore',{
    extend:'Ext.data.Store',
    model:'TransforAuditDeal.model.BatchModifyTemplateEnumfieldModel',
    proxy:({
        type: 'ajax',
        url: '/systemconfig/getConfigValue',
        reader: {
            type: 'json'
        }
    }),
    idProperty: 'code',
    fields: ['code','value']
});
