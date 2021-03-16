/**
 * Created by Rong on 2018/4/28.
 */
Ext.define('ReservoirArea.store.RoomZoneStore',{
    extend:'Ext.data.Store',
    model:'ReservoirArea.model.RoomZoneGridModel',
    pageSize: XD.pageSize,
    autoLoad:false,
    proxy: {
        type: 'ajax',
        url: '/roomDetail/findZones',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});