/**
 * Created by RonJiang on 2018/1/26
 */
Ext.define('BatchModify.store.BatchModifyTemplatefieldStore',{
    extend:'Ext.data.Store',
    model:'BatchModify.model.BatchModifyTemplatefieldModel',
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