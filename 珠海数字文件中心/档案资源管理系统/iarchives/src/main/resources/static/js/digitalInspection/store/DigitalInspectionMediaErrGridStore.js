Ext.define('DigitalInspection.store.DigitalInspectionMediaErrGridStore',{
    extend:'Ext.data.Store',
    model:'DigitalInspection.model.DigitalInspectionEntryMediaGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/digitalInspection/getMediaErrors',
        extraParams:{batchcode:null,mediaid:null},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
