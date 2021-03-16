/**
 * Created by xd on 2017/10/21.
 */
Ext.define('JyAdmins.view.StJyTreeView', {
    extend: 'Ext.tree.Panel',
    xtype: 'stJyTreeView',
    store: 'StJyTreeStore',
    width: 240,
    region: 'west',
    itemId:'stJyTreeViewID',
    collapsible: true,
    split: true,
    header:false
});