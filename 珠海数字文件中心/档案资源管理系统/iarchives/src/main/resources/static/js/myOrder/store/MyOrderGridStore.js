/**
 * Created by Administrator on 2020/4/27.
 */


Ext.define('MyOrder.store.MyOrderGridStore',{
    extend:'Ext.data.Store',
    model:'MyOrder.model.MyOrderGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/myOrder/getUserOrder',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
