/**
 * Created by Administrator on 2020/7/27.
 */


Ext.define('User.view.OrganTreeView', {
    itemId:'organTreeViewID',
    extend: 'Ext.tree.Panel',
    xtype: 'organTreeView',
    store: 'OrganTreeStore',
    autoScroll: true,
    containerScroll: true,
    hideHeaders: true
});
