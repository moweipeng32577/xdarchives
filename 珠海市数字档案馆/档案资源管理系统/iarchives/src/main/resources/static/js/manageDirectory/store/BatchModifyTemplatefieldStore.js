/**
 * Created by Administrator on 2019/6/26.
 */


Ext.define('ManageDirectory.store.BatchModifyTemplatefieldStore',{
    extend:'Ext.data.Store',
    model:'ManageDirectory.model.BatchModifyTemplatefieldModel',
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
