Ext.define('DigitalInspection.store.DigitalInspectionWcGridStore',{
    extend:'Ext.data.Store',
    model:'DigitalInspection.model.DigitalInspectionWcGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    autoLoad:true,
    proxy: {
        type: 'ajax',
        url: '/inspectionAccept/getBatchBillBySearch',
        extraParams:{status:'完成抽检'},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
