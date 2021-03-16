/**
 * Created by Administrator on 2019/10/28.
 */


Ext.define('Audit.store.AuditAdminGridStore',{
    extend:'Ext.data.Store',
    model:'Audit.model.AuditAdminGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/audit/getDocByState',
        extraParams: {state:''},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
