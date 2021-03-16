Ext.define('DigitalInspection.store.DigitalInspectionSearchGridStore',{
    extend:'Ext.data.Store',
    model:'DigitalInspection.model.DigitalInspectionSearchGridModel',
    pageSize: XD.pageSize,//此处设置每页显示记录条数
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url:'/inspectionAccept/findByCaptureSearch',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});