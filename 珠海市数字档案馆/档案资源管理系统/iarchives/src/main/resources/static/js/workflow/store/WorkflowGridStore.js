/**
 * Created by xd on 2017/10/21.
 */
Ext.define('Workflow.store.WorkflowGridStore',{
    extend:'Ext.data.Store',
    model:'Workflow.model.WorkflowGridModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/workflow/getWorkNode',
        extraParams: {work_id:''},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
