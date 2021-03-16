Ext.define('Acquisition.store.MetadataLogEntryStore',{
    extend:'Ext.data.Store',
    model:'Acquisition.model.MetadataLogEntryModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/acquisition/findSearchMetadataLog',
        extraParams: {},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
