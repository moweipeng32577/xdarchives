/**
 * Created by Administrator on 2020/6/24.
 */



Ext.define('PlaceManage.store.PlaceDefendGridStore',{
    extend:'Ext.data.Store',
    model:'PlaceManage.model.PlaceDefendGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/placeManage/getPlaceDefendByCarId',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});

