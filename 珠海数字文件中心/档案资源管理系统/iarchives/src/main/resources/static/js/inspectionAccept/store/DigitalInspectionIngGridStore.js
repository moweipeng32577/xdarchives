Ext.define('DigitalInspection.store.DigitalInspectionIngGridStore',{
    extend:'Ext.data.Store',
    model:'DigitalInspection.model.DigitalInspectionIngGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    autoLoad:true,
    proxy: {
        type: 'ajax',
        url: '/inspectionAccept/getBatchBillBySearch',
        extraParams:{status:'正在抽检'},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
