Ext.define('DigitalProcess.store.DigitalProcessWchjGridStore',{
    extend:'Ext.data.Store',
    model:'DigitalProcess.model.DigitalProcessWchjGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/digitalProcess/getFinishCalloutEntryBySearch',
        extraParams:{batchcode:null},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
