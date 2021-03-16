/**
 * Created by Rong on 2019-01-17.
 */
Ext.define('Lot.store.DeviceAreaStore',{
    extend:'Ext.data.Store',
    autoLoad:true,
    fields: [
        {name:'name'},
        {name:'type'},
        {name:'floor'},
        {name:'archivestype'}
        ],
    proxy: {
        type: 'ajax',
        url: '/deviceArea/devicearea',
        // url: '/user/devicearea', //根据用户区域权限过滤
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});