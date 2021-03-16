/**
 * 楼层数据源
 * Created by Rong on 2019-11-12.
 */
Ext.define('Lot.store.FloorStore',{
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