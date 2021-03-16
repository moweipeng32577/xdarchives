/**
 * Created by Administrator on 2020/10/15.
 */


Ext.define('YearlyCheckAudit.store.YearlyCheckAuditGridStore',{
    extend:'Ext.data.Store',
    model:'YearlyCheckAudit.model.YearlyCheckAuditGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/yearlyCheckAudit/getYearlyCheckAuditTasks',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
