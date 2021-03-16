/**
 * Created by Administrator on 2020/4/28.
 */


Ext.define('PlaceOrder.store.PlaceOrderManageGridStore',{
    extend:'Ext.data.Store',
    model:'PlaceOrder.model.PlaceOrderManageGridModel',
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