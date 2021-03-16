Ext.define('DigitalInspection.store.DigitalInspectionWclGridStore',{
    extend:'Ext.data.Store',
    model:'DigitalInspection.model.DigitalInspectionWclGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    autoLoad:true,
    proxy: {
        type: 'ajax',
        url: '/inspectionAccept/getBatchBillBySearch',
        extraParams:{status:'未抽检'},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
