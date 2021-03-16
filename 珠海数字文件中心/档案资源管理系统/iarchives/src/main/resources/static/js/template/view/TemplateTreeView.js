/**
 * Created by tanly on 2017/11/8 0024.
 */
Ext.define('Template.view.TemplateTreeView', {
    itemId:'templateTreeViewID',
    extend: 'Ext.tree.Panel',
    xtype: 'templateTreeView',
    store: 'TemplateTreeStore',
    autoScroll: true,
    containerScroll: true,
    hideHeaders: true
});