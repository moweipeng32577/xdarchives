/**
 * Created by tanly on 2017/10/24 0024.
 */
Ext.define('Codesetting.view.CodesettingTreeView', {
    extend: 'Ext.tree.Panel',
    xtype: 'codesettingTreeView',
    store: 'CodesettingTreeStore',
    autoScroll: true,
    containerScroll: true,
    itemId: 'codesettingTreeViewID',
    hideHeaders: true
});