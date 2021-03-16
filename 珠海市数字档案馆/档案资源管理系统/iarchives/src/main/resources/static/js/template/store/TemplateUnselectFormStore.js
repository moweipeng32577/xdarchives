Ext.define('Template.store.TemplateUnselectFormStore',{
    extend:'Ext.data.Store',
    autoLoad:true,
    proxy:({
        type: 'ajax',
        url: '/template/getUnselectForm',
        // extraParams:{
        //     nodeid:''
        // },
        reader: {
            type: 'json'
        }
    }),
    idProperty: 'fieldname',
    fields: ['fieldcode','fieldname']

})