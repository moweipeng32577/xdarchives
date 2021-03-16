Ext.define('DigitalInspection.store.DigitalInspectionWcAcceptEntryGridStore',{
    extend:'Ext.data.Store',
    model:'DigitalInspection.model.DigitalInspectionEntryGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/digitalInspection/getWcBatchEntryBySearch',
        extraParams:{batchcode:'',isCheck:'否',flag:''},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
