/**
 * Created by tanly on 2017/11/8 0024.
 */
Ext.define('MetadataTemplate.view.MetadataTemplateTreeView', {
    itemId:'templateTreeViewID',
    extend: 'Ext.tree.Panel',
    xtype: 'templateTreeView',
    store: 'MetadataTemplateTreeStore',
    autoScroll: true,
    containerScroll: true,
    hideHeaders: true
});