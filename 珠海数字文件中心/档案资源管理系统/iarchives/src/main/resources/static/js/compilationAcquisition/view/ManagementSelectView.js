Ext.define('CompilationAcquisition.view.ManagementSelectView', {
    extend: 'Ext.tree.Panel',
    xtype: 'managementSelectView',
    store: 'ManagementSelectStore',
    itemId:'managementSelectViewId',
    scrollable:true,
    split:1
});