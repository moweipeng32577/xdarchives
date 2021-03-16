/**
 * Created by tanly on 2017/11/8 0024.
 */
Ext.define('Template.view.TemplateSxTreeView', {
    itemId:'templateSxTreeViewID',
    extend: 'Ext.tree.Panel',
    xtype: 'templateSxTreeView',
    store: 'TemplateSxTreeStore',
    autoScroll: true,
    containerScroll: true,
    hideHeaders: true
});