/**
 * Created by Rong on 2019-01-17.
 */
Ext.define('Lot.store.DeviceAreaFloorStore',{
    extend:'Ext.data.Store',
    autoLoad:true,
    proxy: {
        type: 'ajax',
        url: '/deviceArea/getAllFloor',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});