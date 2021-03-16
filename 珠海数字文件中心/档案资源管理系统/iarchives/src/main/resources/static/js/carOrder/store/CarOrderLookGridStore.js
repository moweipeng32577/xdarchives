/**
 * Created by Administrator on 2020/4/23.
 */


Ext.define('CarOrder.store.CarOrderLookGridStore',{
    extend:'Ext.data.Store',
    model:'CarOrder.model.CarOrderLookGridModel',
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
