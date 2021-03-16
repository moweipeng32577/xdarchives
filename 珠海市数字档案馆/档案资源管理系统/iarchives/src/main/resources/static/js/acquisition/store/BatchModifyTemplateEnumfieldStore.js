/**
 * Created by RonJiang on 2018/1/26
 */
Ext.define('Acquisition.store.BatchModifyTemplateEnumfieldStore',{
    extend:'Ext.data.Store',
    model:'Acquisition.model.BatchModifyTemplateEnumfieldModel',
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