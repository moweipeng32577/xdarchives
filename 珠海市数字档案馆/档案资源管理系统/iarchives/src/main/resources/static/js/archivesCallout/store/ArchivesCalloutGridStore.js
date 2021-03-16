Ext.define('ArchivesCallout.store.ArchivesCalloutGridStore',{
    extend:'Ext.data.Store',
    model:'ArchivesCallout.model.ArchivesCalloutGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    autoLoad:true,
    proxy: {
        type: 'ajax',
        url: '/archivesCallout/getArchivesCalloutBySearch',
        extraParams:{},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
