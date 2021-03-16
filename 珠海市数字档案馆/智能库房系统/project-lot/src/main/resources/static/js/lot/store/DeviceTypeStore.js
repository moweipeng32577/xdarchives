/**
 * Created by Rong on 2019-11-20.
 */
Ext.define('Lot.store.DeviceTypeStore',{
    extend:'Ext.data.Store',
    autoLoad:true,
    proxy: {
        type: 'ajax',
        url:'/deviceType/getDeviceType',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
    // data:[
    //     ['JK','监控'],
    //     ['HT','温湿度传感器'],
    //     ['LED','大屏'],
    //     ['MJ','门禁'],
    //     ['MJJ','密集架'],
    //     ['AF','安防'],
    //     ['KT','空调机组'],
    //     ['HWHS','抽湿机'],
    //     ['XF','消防'],
    //     ['SJ','漏水检测']
    // ]
});