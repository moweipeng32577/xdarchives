/**
 * Created by Administrator on 2019/6/25.
 */


Ext.define('AcceptDirectory.store.AcceptDirectoryExportSetStore',{
    xtype:'acceptDirectoryExportSetStore',
    extend:'Ext.data.Store',
    model:'AcceptDirectory.model.AcceptDirectoryExportSetModel',
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
