/**
 * Created by Rong on 2019-01-17.
 */
Ext.define('Lot.store.DeviceStore',{
    extend:'Ext.data.Store',
    autoLoad:true,
    model:'Lot.model.DeviceModel',
    proxy: {
        type: 'ajax',
        // url: '/user/areaDevice',  //权限过滤
        url:'/areaDevice',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});