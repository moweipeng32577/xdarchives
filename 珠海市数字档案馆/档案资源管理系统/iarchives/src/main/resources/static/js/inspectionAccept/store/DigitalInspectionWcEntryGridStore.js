Ext.define('DigitalInspection.store.DigitalInspectionWcEntryGridStore',{
    extend:'Ext.data.Store',
    model:'DigitalInspection.model.DigitalInspectionEntryGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/inspectionAccept/getWcBatchEntryBySearch',
        extraParams:{batchcode:'',isCheck:'否',flag:''},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
