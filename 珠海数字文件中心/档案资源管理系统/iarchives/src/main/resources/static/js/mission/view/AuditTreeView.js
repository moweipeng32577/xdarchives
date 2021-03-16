/**
 * Created by Administrator on 2019/10/28.
 */


Ext.define('Mission.view.AuditTreeView', {
    extend: 'Ext.tree.Panel',
    xtype: 'auditTreeView',
    store: 'DzJyTreeStore',
    width: 240,
    margin:'0 0 0 0',
    region: 'west',
    autoScroll: true,
    containerScroll: true,
    itemId:'auditTreeViewID',
    collapsible: true,
    split: true,
    header:false,
    floatable: false,
    title: '审批状态'
});