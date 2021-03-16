/**
 * Created by Administrator on 2019/7/3.
 */




Ext.define('AssemblyAdmin.store.AssemblyAdminUserGridStore',{
    extend:'Ext.data.Store',
    model:'AssemblyAdmin.model.AssemblyAdminUserGridModel',
    autoLoad: true,
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/assemblyAdmin/getAssemblyAdminUser',
        extraParams: {},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
