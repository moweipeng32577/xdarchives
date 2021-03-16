/**
 * Created by SunK on 2018/9/13 0013.
 */
Ext.define('CompilationAcquisition.store.ManagementGroupSetStore',{
    xtype:'managementGroupSetStore',
    extend:'Ext.data.Store',
    model:'CompilationAcquisition.model.ManagementGroupSetModel',
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