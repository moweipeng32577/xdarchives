Ext.define('DigitalInspection.store.DigitalInspectionEntryMediaGridStore',{
    extend:'Ext.data.Store',
    model:'DigitalInspection.model.DigitalInspectionEntryMediaGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/inspectionAccept/getEntryMedias',
        extraParams:{batchcode:'',entryid:''},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
