Ext.define('DigitalProcess.store.DigitalProcessYwcGridStore',{
    extend:'Ext.data.Store',
    model:'DigitalProcess.model.DigitalProcessYwcGridModel',
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
