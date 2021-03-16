/**
 * Created by Administrator on 2019/12/14.
 */


Ext.define('UserGroup.view.OrganTreeView', {
    itemId:'organTreeViewID',
    extend: 'Ext.tree.Panel',
    xtype: 'organTreeView',
    store: 'OrganTreeStore',
    autoScroll: true,
    containerScroll: true,
    hideHeaders: true
});