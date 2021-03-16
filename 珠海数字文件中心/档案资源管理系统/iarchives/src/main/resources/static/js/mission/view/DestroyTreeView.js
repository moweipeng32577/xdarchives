/**
 * Created by yl on 2017/12/6.
 */
Ext.define('Mission.view.DestroyTreeView', {
    extend: 'Ext.tree.Panel',
    xtype: 'destroyTreeView',
    store: 'DestroyTreeStore',
    width: 240,
    margin:'0 0 0 0',
    region: 'west',
    autoScroll: true,
    containerScroll: true,
    itemId:'destroyTreeViewID',
    collapsible: true,
    split: true,
    header:false,
    floatable: false,
    title: '审批状态'
});