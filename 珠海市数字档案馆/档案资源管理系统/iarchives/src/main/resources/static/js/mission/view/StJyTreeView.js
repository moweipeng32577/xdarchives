/**
 * Created by Administrator on 2018/10/23.
 */


Ext.define('Mission.view.StJyTreeView', {
    extend: 'Ext.tree.Panel',
    xtype: 'stJyTreeView',
    store: 'StJyTreeStore',
    width: 240,
    margin:'0 0 0 0',
    region: 'west',
    autoScroll: true,
    containerScroll: true,
    itemId:'stJyTreeViewID',
    collapsible: true,
    split: true,
    header:false,
    floatable: false,
    title: '审批状态'
});