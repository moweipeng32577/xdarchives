/**
 * Created by SunK on 2018/10/11 0011.
 */
Ext.define('Export.store.ExportGroupSetStore',{
    xtype:'exportGroupSetStore',
    extend:'Ext.data.Store',
    model:'Export.model.ExportGroupSetModel',
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