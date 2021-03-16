/**
 * Created by Administrator on 2020/4/24.
 */


Ext.define('PlaceOrder.store.PlaceOrderAuditGridStore',{
    extend:'Ext.data.Store',
    model:'PlaceOrder.model.PlaceOrderAuditGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/placeOrder/getPlaceAuditOrder',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
