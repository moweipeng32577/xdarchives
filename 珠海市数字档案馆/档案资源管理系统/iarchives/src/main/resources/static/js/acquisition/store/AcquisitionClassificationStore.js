Ext.define('Acquisition.store.AcquisitionClassificationStore',{
    extend:'Ext.data.Store',
    model:'Acquisition.model.AcquisitionClassificationModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/acquisition/entries',
        extraParams: {},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});