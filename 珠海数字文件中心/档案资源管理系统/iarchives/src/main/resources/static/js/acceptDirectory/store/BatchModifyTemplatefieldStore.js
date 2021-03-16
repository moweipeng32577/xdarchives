/**
 * Created by Administrator on 2019/6/26.
 */


Ext.define('AcceptDirectory.store.BatchModifyTemplatefieldStore',{
    extend:'Ext.data.Store',
    model:'AcceptDirectory.model.BatchModifyTemplatefieldModel',
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
