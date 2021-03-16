/**
 * Created by Rong on 2019-01-17.
 */
Ext.define('Lot.store.DeviceByFloorCodeStore',{
    extend:'Ext.data.Store',
    autoLoad:true,
    model:'Lot.model.DeviceModel',
    proxy: {
        type: 'ajax',
        // url: '/areaDevice',
        url:'/userDevicesByFloorCode',//用户权限设备 by  floorcode
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});