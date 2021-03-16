Ext.define('Lot.store.DeviceAlarmStore',{
    extend:'Ext.data.Store',
    model:'Lot.model.DeviceAlarmModel',
    autoLoad:true,
    proxy: {
        type: 'ajax',
        url: '/deviceAlarm/grid',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});