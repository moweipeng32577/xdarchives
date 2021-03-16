/**
 * Created by Administrator on 2020/4/24.
 */


Ext.define('CarOrder.store.CarOrderAuditGridStore',{
    extend:'Ext.data.Store',
    model:'CarOrder.model.CarOrderAuditGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/carOrder/getUserAuditOrder',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
