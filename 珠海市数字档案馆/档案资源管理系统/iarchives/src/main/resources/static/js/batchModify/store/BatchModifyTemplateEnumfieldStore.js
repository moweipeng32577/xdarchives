/**
 * Created by RonJiang on 2018/1/26
 */
Ext.define('BatchModify.store.BatchModifyTemplateEnumfieldStore',{
    extend:'Ext.data.Store',
    model:'BatchModify.model.BatchModifyTemplateEnumfieldModel',
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