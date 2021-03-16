/**
 * Created by Administrator on 2020/7/20.
 */
Ext.define('DepartmentAudit.store.AuditDclStore',{
    extend:'Ext.data.Store',
    model:'DepartmentAudit.model.AuditDclModel',
    pageSize: XD.pageSize,
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url: '/projectRate/getProjectByDepart',
        extraParams: {projectstatus:'提交部门审核'},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});