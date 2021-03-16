/**
 * Created by xd on 2017/10/21.
 */
Ext.define('Workflow.view.WorkflowSxTreeView', {
    extend: 'Ext.tree.Panel',
    xtype: 'workflowSxTreeView',
    store: 'WorkflowSxTreeStore',
    autoScroll: true,
    containerScroll: true,
    itmeId:'workflowSxTreeViewID',
    hideHeaders: true
});