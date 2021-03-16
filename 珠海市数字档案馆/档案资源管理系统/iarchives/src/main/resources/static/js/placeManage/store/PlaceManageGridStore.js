/**
 * Created by Administrator on 2020/4/20.
 */


Ext.define('PlaceManage.store.PlaceManageGridStore',{
    extend:'Ext.data.Store',
    model:'PlaceManage.model.PlaceManageGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/placeManage/getPlaceManages',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
