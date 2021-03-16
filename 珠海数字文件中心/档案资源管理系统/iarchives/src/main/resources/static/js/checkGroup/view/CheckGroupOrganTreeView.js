/**
 * Created by Administrator on 2018/12/3.
 */

Ext.define('CheckGroup.view.CheckGroupOrganTreeView', {
    itemId:'checkGroupOrganTreeViewId',
    extend: 'Ext.tree.Panel',
    xtype: 'checkGroupOrganTreeView',
    store: 'CheckGroupOrganTreeStore',
    autoScroll: true,
    containerScroll: true,
    hideHeaders: true
});
