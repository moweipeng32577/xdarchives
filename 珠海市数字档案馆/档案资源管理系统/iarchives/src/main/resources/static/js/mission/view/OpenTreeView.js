/**
 * Created by tanly on 2017/12/7 0007.
 */
Ext.define('Mission.view.OpenTreeView', {
    extend: 'Ext.tree.Panel',
    xtype: 'openTreeView',
    store: 'OpenTreeStore',
    width: 240,
    margin:'0 0 0 0',
    region: 'west',
    autoScroll: true,
    containerScroll: true,
    itemId:'openTreeViewID',
    collapsible: true,
    split: true,
    header:false,
    floatable: false,
    title: '审批状态'
});