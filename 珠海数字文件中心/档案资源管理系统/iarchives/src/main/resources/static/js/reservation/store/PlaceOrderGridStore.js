/**
 * Created by Administrator on 2020/4/28.
 */


Ext.define('Reservation.store.PlaceOrderGridStore',{
    extend:'Ext.data.Store',
    model:'Reservation.model.PlaceOrderGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/placeOrder/getUserPlaceOrder',
        extraParams: {
            type:'user'
        },
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});

