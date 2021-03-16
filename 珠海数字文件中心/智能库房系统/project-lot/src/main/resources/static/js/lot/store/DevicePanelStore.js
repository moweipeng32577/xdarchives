/**
 * Created by Administrator on 2020/3/6.
 */


Ext.define('Lot.store.DevicePanelStore',{
    extend:'Ext.data.Store',
    autoLoad:true,
    model:'Lot.model.DeviceModel',
    proxy: {
        type: 'ajax',
        // url: '/user/devicePanel', //权限过滤
        url: '/devicePanel',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});