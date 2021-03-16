/**
 * Created by Rong on 2019-03-25.
 */
Ext.define('Lot.store.DeviceHistoryStore',{
    extend:'Ext.data.Store',
    model:'Lot.model.DeviceHistoryModel',
    proxy: {
        type: 'ajax',
        url:'',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});