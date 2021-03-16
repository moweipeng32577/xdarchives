/**
 * Created by Administrator on 2020/4/27.
 */


Ext.define('MyOrder.store.MyOrderLookGridStore',{
    extend:'Ext.data.Store',
    model:'MyOrder.model.MyOrderLookGridModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/carOrder/getAuditDetails',
        extraParams: {},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});

