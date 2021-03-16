/**
 * Created by tanly on 2017/11/13 0013.
 */
Ext.define('Template.store.TemplateSelectStore',{
    extend:'Ext.data.Store',
    xtype:'templateSelectStore',
    idProperty: 'fieldcode',
    fields: ['fieldcode','fieldname'],
    proxy: {
        type: 'ajax',
        url: '/template/getAllField',
        params:{
            nodeid:''
        },
        reader: {
            type: 'json'
        }
    },
    autoLoad: true
});