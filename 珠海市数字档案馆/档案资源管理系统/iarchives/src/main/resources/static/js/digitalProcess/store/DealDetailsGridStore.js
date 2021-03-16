Ext.define('DigitalProcess.store.DealDetailsGridStore',{
    extend:'Ext.data.Store',
    model:'DigitalProcess.model.DealDetailsGridModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/digitalProcess/getDealDetails',
        extraParams: {},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
