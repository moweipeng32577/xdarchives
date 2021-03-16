/**
 * Created by Administrator on 2020/4/28.
 */


Ext.define('Reservation.store.PlaceOrderManageGridStore',{
    extend:'Ext.data.Store',
    model:'Reservation.model.PlaceOrderManageGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/placeOrder/getPlaceManages',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});