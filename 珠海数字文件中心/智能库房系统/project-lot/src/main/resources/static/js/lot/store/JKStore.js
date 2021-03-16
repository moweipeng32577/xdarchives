/**
 * Created by Rong on 2019-01-17.
 */
Ext.define('Lot.store.JKStore',{
    extend:'Ext.data.Store',
    autoLoad:true,
    model:'Lot.model.DeviceModel',
    proxy: {
        type: 'ajax',
        url: '/userJKDevices', //权限视频监控设备
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});