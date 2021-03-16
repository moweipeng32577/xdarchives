/**
 * Created by xd on 2017/10/21.
 */
Ext.define('User.view.UserTreeView', {
    extend: 'Ext.tree.Panel',
    xtype: 'userTreeView',
    store: 'UserTreeStore',
    autoScroll: true,
    containerScroll: true,
    itmeId:'userTreeViewID',
    hideHeaders: true
});