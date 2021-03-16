/**
 * Created by Administrator on 2019/5/23.
 */

Ext.define('Mission.view.DzPrintTreeView', {
    extend: 'Ext.tree.Panel',
    xtype: 'dzPrintTreeView',
    store: 'DzJyTreeStore',
    width: 240,
    margin:'0 0 0 0',
    region: 'west',
    autoScroll: true,
    containerScroll: true,
    itemId:'dzPrintTreeViewID',
    collapsible: true,
    split: true,
    header:false,
    floatable: false,
    title: '审批状态'
});
