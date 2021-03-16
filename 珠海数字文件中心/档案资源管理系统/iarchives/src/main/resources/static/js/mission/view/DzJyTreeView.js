/**
 * Created by Administrator on 2018/10/23.
 */

Ext.define('Mission.view.DzJyTreeView', {
    extend: 'Ext.tree.Panel',
    xtype: 'dzJyTreeView',
    store: 'DzJyTreeStore',
    width: 240,
    margin:'0 0 0 0',
    region: 'west',
    autoScroll: true,
    containerScroll: true,
    itemId:'dzJyTreeViewID',
    collapsible: true,
    split: true,
    header:false,
    floatable: false,
    title: '审批状态'
});
