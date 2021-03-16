/**
 * Created by Administrator on 2019/5/27.
 */


Ext.define('JyAdmins.view.DzPrintTreeView', {
    extend: 'Ext.tree.Panel',
    xtype: 'dzPrintTreeView',
    store: 'DzJyTreeStore',
    width: 240,
    region: 'west',
    itemId:'dzPrintTreeViewID',
    collapsible: true,
    split: true,
    header:false
});
