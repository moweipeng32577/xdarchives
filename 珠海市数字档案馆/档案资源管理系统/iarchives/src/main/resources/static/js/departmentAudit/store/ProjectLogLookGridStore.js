/**
 * Created by Administrator on 2020/7/21.
 */
Ext.define('DepartmentAudit.store.ProjectLogLookGridStore',{
    extend:'Ext.data.Store',
    model:'DepartmentAudit.model.ProjectLogLookGridModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/projectRate/getProjectLogs',
        extraParams: {},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
