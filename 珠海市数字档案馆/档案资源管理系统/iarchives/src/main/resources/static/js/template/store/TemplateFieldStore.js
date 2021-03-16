Ext.define('Template.store.TemplateFieldStore',{
    extend:'Ext.data.Store',
    xtype:'templateFieldStore',
    idProperty: 'fieldcode',
    fields: ['fieldcode','fieldname'],
    proxy: {
        type: 'ajax',
        url: '/template/getSelectedList',
        params:{
            nodeid:''
        },
        reader: {
            type: 'json'
        }
    },
    autoLoad: true
});