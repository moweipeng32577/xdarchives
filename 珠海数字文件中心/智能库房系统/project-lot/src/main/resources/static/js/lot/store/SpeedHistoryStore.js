/**
 * Created by Rong on 2019-03-25.
 */
Ext.define('Lot.store.SpeedHistoryStore',{
    extend:'Ext.data.Store',
    model:'Lot.model.SpeedHistoryModel',
    proxy: {
        type: 'ajax',
        url:'/speed/ht/recordsBycode',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});