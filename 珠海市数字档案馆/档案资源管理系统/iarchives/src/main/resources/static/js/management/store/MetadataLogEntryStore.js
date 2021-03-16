Ext.define('Management.store.MetadataLogEntryStore',{
    extend:'Ext.data.Store',
    model:'Management.model.MetadataLogEntryModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/management/findSearchMetadataLog',
        extraParams: {},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
