/**
 * Created by xd on 2017/10/21.
 */
Ext.define('ArchiveManagement.view.ArchiveTreeView', {
    extend: 'Ext.tree.Panel',
    xtype: 'archiveTreeView',
    store: 'ArchiveTreeStore',
    autoScroll: true,
    containerScroll: true,
    itmeId:'archiveTreeViewID',
    hideHeaders: true
});