/**
 * Created by Administrator on 2019/6/26.
 */


Ext.define('ManageDirectory.store.BatchModifyTemplateEnumfieldStore',{
    extend:'Ext.data.Store',
    model:'ManageDirectory.model.BatchModifyTemplateEnumfieldModel',
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
