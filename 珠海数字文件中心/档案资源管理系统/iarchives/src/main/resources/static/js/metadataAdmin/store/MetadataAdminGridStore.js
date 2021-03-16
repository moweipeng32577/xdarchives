Ext.define('MetadataAdmin.store.MetadataAdminGridStore',{
    extend:'Ext.data.Store',
    model:'MetadataAdmin.model.MetadataAdminGridModel',
    autoLoad: true,
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/metadataAdmin/getMetadataBySearch',
        extraParams: {flag:''},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
