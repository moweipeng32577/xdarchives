/**
 * Created by Rong on 2019-01-17.
 */
Ext.define('Lot.store.DeviceLinkStore',{
    extend:'Ext.data.Store',
    autoLoad:true,
    proxy: {
        type: 'ajax',
        url: '/devicelink',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});