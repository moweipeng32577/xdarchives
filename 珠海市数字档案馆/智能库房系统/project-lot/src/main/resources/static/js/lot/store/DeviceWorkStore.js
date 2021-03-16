/**
 * Created by wujy on 2019-09-18.
 */
Ext.define('Lot.store.DeviceWorkStore',{
    extend:'Ext.data.Store',
    model:'Lot.model.DeviceWorkModel',
    autoLoad:true,
    proxy: {
        type: 'ajax',
        url: '/deviceWork/getWorks',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});