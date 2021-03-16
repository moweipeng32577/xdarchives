Ext.define('Acquisition.store.AcquisitionSequenceStore',{
    extend:'Ext.data.Store',
    model:'Acquisition.model.AcquisitionSequenceModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/acquisition/entryIndexSqCaptures',
        extraParams: {},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    },
    hasCloseButton:false
});