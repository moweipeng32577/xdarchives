/**
 * Created by xd on 2017/10/21.
 */
Ext.define('JyAdmins.view.DzJyTreeView', {
    extend: 'Ext.tree.Panel',
    xtype: 'dzJyTreeView',
    store: 'DzJyTreeStore',
    width: 240,
    region: 'west',
    itemId:'dzJyTreeViewID',
    collapsible: true,
    split: true,
    header:false
});