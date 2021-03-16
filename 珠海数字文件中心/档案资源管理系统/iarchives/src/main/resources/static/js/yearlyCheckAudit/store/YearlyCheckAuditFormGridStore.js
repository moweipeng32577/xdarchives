/**
 * Created by Administrator on 2020/10/15.
 */



Ext.define('YearlyCheckAudit.store.YearlyCheckAuditFormGridStore',{
    extend:'Ext.data.Store',
    model:'YearlyCheckAudit.model.YearlyCheckAuditFormGridStoreModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/yearlyCheckAudit/getYearlyCheckApproveReportsByTaskid',
        extraParams:{},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});