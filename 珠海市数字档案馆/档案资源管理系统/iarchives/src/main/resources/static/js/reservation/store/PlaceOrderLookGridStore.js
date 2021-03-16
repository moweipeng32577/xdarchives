/**
 * Created by Administrator on 2020/4/23.
 */


Ext.define('Reservation.store.PlaceOrderLookGridStore',{
    extend:'Ext.data.Store',
    model:'Reservation.model.PlaceOrderLookGridModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/placeOrder/getAuditDetails',
        extraParams: {},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
