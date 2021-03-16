Ext.define('Acquisition.view.AcquisitionSelectView', {
    extend: 'Ext.tree.Panel',
    xtype: 'acquisitionSelectView',
    store: 'AcquisitionSelectStore',
    itemId:'acquisitionSelectViewId',
    scrollable:true,
    split:1
});