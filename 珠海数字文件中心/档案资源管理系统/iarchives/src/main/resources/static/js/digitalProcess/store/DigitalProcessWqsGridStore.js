Ext.define('DigitalProcess.store.DigitalProcessWqsGridStore',{
    extend:'Ext.data.Store',
    model:'DigitalProcess.model.DigitalProcessWqsGridModel',
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
