/**
 * Created by xd on 2017/10/21.
 */
Ext.define('Workflow.view.WorkflowRightView',{
    extend:'Ext.Panel',
    xtype:'workflowRightView',
    itemId:'workflowRightView',
    layout: 'border',
    bodyBorder: false,
    items: [{xtype: 'workflowGridView'}]
});
