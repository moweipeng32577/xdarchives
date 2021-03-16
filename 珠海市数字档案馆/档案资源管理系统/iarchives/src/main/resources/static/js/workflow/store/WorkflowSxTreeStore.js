/**
 * Created by xd on 2017/10/21.
 */
Ext.define('Workflow.store.WorkflowSxTreeStore',{
    extend:'Ext.data.TreeStore',
    model:'Workflow.model.WorkflowTreeModel',
    proxy: {
        type: 'ajax',
        url: '/workflow/getWorkflow',
        extraParams:{
            xtType:'声像系统'
        },
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
