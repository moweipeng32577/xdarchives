Ext.define('DigitalInspection.store.DigitalInspectionEntryDetailGridStore',{
    extend:'Ext.data.Store',
    model:'DigitalInspection.model.DigitalInspectionEntryDetailGridModel',
    pageSize:1000,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/digitalInspection/getBatchEntryBySearch',
        extraParams:{batchcode:'',isCheck:'是'},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
