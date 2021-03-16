/**
 * Created by tanly on 2017/11/3 0003.
 */
Ext.define('Management.store.OrdersettingSelectStore',{
    extend:'Ext.data.Store',
    model:'Management.model.OrdersettingJsonModel',
    autoLoad:true,
    proxy:({
        type: 'ajax',
        url: '/ordersetting/getTemplateField',
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