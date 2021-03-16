
Ext.define('Template.store.TableFieldStore',{
    extend:'Ext.data.Store',
    xtype:'tableFieldStore',
    model:'Template.model.TemplateGridModel',
    // autoLoad:true,
    pageSize:XD.pageSize,
    proxy:({
        type: 'ajax',
        // url: '/template/getTableField',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }),
    // idProperty: 'fieldname',
    fields: ['fieldcode','fieldname']

})