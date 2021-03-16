/**
 * Created by Administrator on 2019/10/25.
 */


Ext.define('TransforAuditDeal.store.AuditDocGridStore',{
    extend:'Ext.data.Store',
    model:'TransforAuditDeal.model.AuditDocModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/audit/getDocByState',
        extraParams: {state:'待审核'},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
