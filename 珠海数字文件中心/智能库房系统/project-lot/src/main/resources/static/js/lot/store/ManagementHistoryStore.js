Ext.define('Lot.store.ManagementHistoryStore',{
    extend:'Ext.data.Store',
    model:'Lot.model.managementHistoryModel',
    autoLoad:true,
    proxy: {
        type: 'ajax',
        url:'/managementHistory/grid',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});