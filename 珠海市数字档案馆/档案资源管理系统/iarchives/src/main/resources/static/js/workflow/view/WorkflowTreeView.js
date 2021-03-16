/**
 * Created by xd on 2017/10/21.
 */
Ext.define('Workflow.view.WorkflowTreeView', {
    extend: 'Ext.tree.Panel',
    xtype: 'workflowTreeView',
    store: 'WorkflowTreeStore',
    autoScroll: true,
    containerScroll: true,
    itmeId:'workflowTreeViewID',
    hideHeaders: true
});