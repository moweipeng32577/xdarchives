/**
 * Created by tanly on 2017/11/3 0003.
 */
Ext.define('MetadataTemplate.store.CodesettingSelectStore',{
    extend:'Ext.data.Store',
    model:'MetadataTemplate.model.CodesettingJsonModel',
    autoLoad:true,
    proxy:({
        type: 'ajax',
        url: '/codesetting/getTemplateField',
        extraParams:{
            datanodeid:''
        },
        reader: {
            type: 'json'
        }
    }),
    idProperty: 'fieldname',
    fields: ['fieldcode','fieldname']

});