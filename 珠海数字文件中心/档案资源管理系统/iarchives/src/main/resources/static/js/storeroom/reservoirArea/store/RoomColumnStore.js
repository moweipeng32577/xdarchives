/**
 * Created by Rong on 2018/4/28.
 */
Ext.define('ReservoirArea.store.RoomColumnStore',{
    extend:'Ext.data.Store',
    model:'ReservoirArea.model.RoomColumnGridModel',
    pageSize: XD.pageSize,
    autoLoad:false,
    proxy: {
        type: 'ajax',
        url: '/roomDetail/findColumns',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});