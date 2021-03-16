/**
 * Created by SunK on 2018/10/11 0011.
 */
Ext.define('MetadataManagement.store.MetadataManagementGroupSetStore',{
    xtype:'exportGroupSetStore',
    extend:'Ext.data.Store',
    model:'MetadataManagement.model.MetadataManagementGroupSetModel',
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