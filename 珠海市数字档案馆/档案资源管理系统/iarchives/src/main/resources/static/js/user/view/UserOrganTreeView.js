/**
 * Created by tanly on 2018/9/17 0024.
 */
Ext.define('User.view.UserOrganTreeView', {
    itemId: 'userOrganTreeViewID',
    extend: 'Ext.tree.Panel',
    xtype: 'userOrganTreeView',
    store: 'UserOrganTreeStore',
    autoScroll: true,
    containerScroll: true,
    hideHeaders: true
});