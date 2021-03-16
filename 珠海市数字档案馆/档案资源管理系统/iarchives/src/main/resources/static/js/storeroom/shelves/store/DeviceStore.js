/**
 * Created by Rong on 2019-01-17.
 */
Ext.define('Shelves.store.DeviceStore',{
    extend:'Ext.data.Store',
    autoLoad:false,
    proxy: {
        type: 'ajax',
        url: '',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});