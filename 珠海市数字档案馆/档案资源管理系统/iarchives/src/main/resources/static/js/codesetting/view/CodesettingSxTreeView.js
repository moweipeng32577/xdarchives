/**
 * Created by tanly on 2017/10/24 0024.
 */
Ext.define('Codesetting.view.CodesettingSxTreeView', {
    extend: 'Ext.tree.Panel',
    xtype: 'codesettingSxTreeView',
    store: 'CodesettingSxTreeStore',
    autoScroll: true,
    containerScroll: true,
    itemId: 'codesettingSxTreeViewID',
    hideHeaders: true
});