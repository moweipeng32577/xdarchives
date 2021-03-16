/**
 * Created by Administrator on 2020/4/27.
 */


Ext.define('Reservation.store.MyOrderGridStore',{
    extend:'Ext.data.Store',
    model:'Reservation.model.MyOrderGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/placeOrder/getUserPlaceOrder',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
