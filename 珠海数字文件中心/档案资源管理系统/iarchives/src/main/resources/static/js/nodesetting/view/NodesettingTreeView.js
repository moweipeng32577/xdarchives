/**
 * Created by tanly on 2017/10/24 0024.
 */
Ext.define('Nodesetting.view.NodesettingTreeView', {
    extend: 'Ext.tree.Panel',
    xtype: 'nodesettingTreeView',
    store: 'NodesettingTreeStore',
    itemId:'nodesettingTreeViewID',
    scrollable:true
});