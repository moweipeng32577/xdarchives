/**
 * Created by SunK on 2018/9/13 0013.
 */
Ext.define('Management.store.ManagementGroupSetStore',{
    xtype:'managementGroupSetStore',
    extend:'Ext.data.Store',
    model:'Management.model.ManagementGroupSetModel',
    //autoLoad:true,
    proxy:({
        type: 'ajax',
        url: '/export/managementGetFields',
        extraParams:{fieldNodeid:0},
        reader: {
            type: 'json'
        }
    }),
    //idProperty: 'fieldname',
    fields: ['fieldcode','fieldname']
});