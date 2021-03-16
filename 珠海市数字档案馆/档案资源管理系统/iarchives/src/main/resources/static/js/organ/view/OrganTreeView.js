/**
 * Created by tanly on 2017/11/1 0024.
 */
Ext.define('Organ.view.OrganTreeView', {
    itemId:'organTreeViewID',
    extend: 'Ext.tree.Panel',
    xtype: 'organTreeView',
    store: 'OrganTreeStore',
    autoScroll: true,
    containerScroll: true,
    hideHeaders: true
});