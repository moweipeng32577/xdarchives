Ext.define('DigitalInspection.store.DigitalInspectionEntryGridStore',{
    extend:'Ext.data.Store',
    model:'DigitalInspection.model.DigitalInspectionEntryGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/inspectionAccept/getBatchEntryBySearch',
        extraParams:{batchcode:'',isCheck:'否',flag:''},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
