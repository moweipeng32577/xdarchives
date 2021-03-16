/**
 * Created by Administrator on 2019/7/3.
 */


Ext.define('AssemblyAdmin.store.AssemblyAdminUserSelectStore',{
    extend:'Ext.data.Store',
    xtype:'assemblyAdminUserSelectStore',
    model:'AssemblyAdmin.model.AssemblyUserSetModel',
    idProperty: 'userid',
    fields: ['userid','realname'],
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url: '/assemblyAdmin/getAssemblyFlowUser',
        reader: {
            type: 'json'
        }
    }
});
