/**
 * Created by Administrator on 2020/6/16.
 */


Ext.define('AuditOrder.store.InformOrderGridStore',{
    extend:'Ext.data.Store',
    model:'AuditOrder.model.InformOrderGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/carOrder/getInforms',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
