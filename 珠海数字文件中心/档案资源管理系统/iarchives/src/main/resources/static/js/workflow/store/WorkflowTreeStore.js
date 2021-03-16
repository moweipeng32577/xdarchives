/**
 * Created by xd on 2017/10/21.
 */
Ext.define('Workflow.store.WorkflowTreeStore',{
    extend:'Ext.data.TreeStore',
    model:'Workflow.model.WorkflowTreeModel',
    proxy: {
        type: 'ajax',
        url: '/workflow/getWorkflow',
        reader: {
            type: 'json',
            expanded: true
        }
    },
    root: {
        text: '审批流程',
        expanded: true
    }
});
