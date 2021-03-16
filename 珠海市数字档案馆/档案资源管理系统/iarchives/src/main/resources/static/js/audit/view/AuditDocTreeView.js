/**
 * Created by Administrator on 2019/10/28.
 */


Ext.define('Audit.view.AuditDocTreeView', {
    extend: 'Ext.tree.Panel',
    xtype: 'auditDocTreeView',
    store: 'AuditDocTreeStore',
    width: 240,
    region: 'west',
    itemId:'auditDocTreeViewID',
    collapsible: true,
    split: true,
    header:false
});
