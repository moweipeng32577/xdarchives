/**
 * Created by Rong on 2019-01-17.
 */
Ext.define('Shelves.store.RoomStore',{
    extend:'Ext.data.Store',
    autoLoad:false,
    // model:'Shelves.model.RoomModel',
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