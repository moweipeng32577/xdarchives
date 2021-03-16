/**
 * Created by Rong on 2019-01-17.
 */
Ext.define('Shelves.store.FloorStore',{
    extend:'Ext.data.Store',
    autoLoad:true,
    proxy: {
        type: 'ajax',
        url: '/floor/floors',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});