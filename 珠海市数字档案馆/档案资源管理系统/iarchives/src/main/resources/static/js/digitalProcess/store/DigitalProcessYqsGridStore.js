Ext.define('DigitalProcess.store.DigitalProcessYqsGridStore',{
    extend:'Ext.data.Store',
    model:'DigitalProcess.model.DigitalProcessYqsGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/digitalProcess/getCalloutEntryBySearch',
        extraParams:{batchcode:null},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
