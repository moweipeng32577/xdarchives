/**
 * Created by Administrator on 2019/7/3.
 */



Ext.define('AssemblyAdmin.view.AssemblyUserSetOrganTreeView', {
    itemId:'assemblyUserSetOrganTreeViewId',
    extend: 'Ext.tree.Panel',
    xtype: 'assemblyUserSetOrganTreeView',
    store: 'AssemblyUserSetOrganTreeStore',
    autoScroll: true,
    containerScroll: true,
    hideHeaders: true
});
