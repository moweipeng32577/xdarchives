/**
 * 获取所有用户信息
 */
Ext.define('Workflow.store.WorkflowAllGridStore',{
    extend:'Ext.data.Store',
    model:'Workflow.model.WorkflowSelectModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/workflow/getDlUser',
        extraParams: {},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});