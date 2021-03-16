/**
 * Created by Administrator on 2020/4/28.
 */


Ext.define('PlaceOrder.store.PlaceOrderGridStore',{
    extend:'Ext.data.Store',
    model:'PlaceOrder.model.PlaceOrderGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    autoLoad:false,
    proxy: {
        type: 'ajax',
        url: '/placeOrder/getUserPlaceOrder',
        extraParams: {
            type:'all',
            id:'@'//默认
        },
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});

