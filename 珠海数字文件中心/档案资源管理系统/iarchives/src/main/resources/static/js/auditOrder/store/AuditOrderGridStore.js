/**
 * Created by Administrator on 2020/6/13.
 */

Ext.define('AuditOrder.store.AuditOrderGridStore',{
    extend:'Ext.data.Store',
    model:'AuditOrder.model.AuditOrderGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/carOrder/getAuditOrderByTask',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
