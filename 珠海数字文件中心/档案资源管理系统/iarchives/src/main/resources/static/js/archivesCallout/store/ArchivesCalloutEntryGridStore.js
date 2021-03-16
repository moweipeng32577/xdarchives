Ext.define('ArchivesCallout.store.ArchivesCalloutEntryGridStore',{
    extend:'Ext.data.Store',
    model:'ArchivesCallout.model.ArchivesCalloutEntryGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/archivesCallout/getCalloutEntryBySearch',
        extraParams:{batchcode:null},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
