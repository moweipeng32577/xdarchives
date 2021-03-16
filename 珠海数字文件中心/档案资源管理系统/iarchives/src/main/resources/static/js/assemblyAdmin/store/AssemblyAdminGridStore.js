Ext.define('AssemblyAdmin.store.AssemblyAdminGridStore',{
    extend:'Ext.data.Store',
    model:'AssemblyAdmin.model.AssemblyAdminGridModel',
    autoLoad: true,
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/assemblyAdmin/getAssemblyBySearch',
        extraParams: {flag:''},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
