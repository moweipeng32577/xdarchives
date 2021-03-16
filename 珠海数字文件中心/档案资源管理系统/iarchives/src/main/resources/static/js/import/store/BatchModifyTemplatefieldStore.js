/**
 * Created by tanly on 2017/11/3 0003.
 */
Ext.define('Import.store.BatchModifyTemplatefieldStore',{
    extend:'Ext.data.Store',
    model:'Import.model.BatchModifyTemplatefieldModel',
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