Ext.define('Acquisition.store.OAImportGridStore',{
    extend:'Ext.data.Store',
    model:'Acquisition.model.OAImportGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/OARecord/findOARecordBySearch',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
