Ext.define('Lot.store.DeviceInformationStore',{
    extend:'Ext.data.Store',
    model:'Lot.model.DeviceInformationModel',
    autoLoad:true,
    proxy: {
        type: 'ajax',
        url: '/deviceInformation/grid',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});