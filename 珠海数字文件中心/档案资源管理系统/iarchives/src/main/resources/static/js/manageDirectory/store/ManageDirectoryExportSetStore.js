/**
 * Created by Administrator on 2019/6/25.
 */


Ext.define('ManageDirectory.store.ManageDirectoryExportSetStore',{
    xtype:'manageDirectoryExportSetStore',
    extend:'Ext.data.Store',
    model:'ManageDirectory.model.ManageDirectoryExportSetModel',
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
