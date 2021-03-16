Ext.define('DataEvent.store.DataEventStore',{
    extend:'Ext.data.Store',
    model:'DataEvent.model.DataEventModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/dataEvent/lookDataEvent',
        extraParams: {},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});