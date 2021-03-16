Ext.define('DataTransfor.view.DataTransforSelectView', {
    extend: 'Ext.tree.Panel',
    width: XD.treeWidth,
    xtype: 'dataTransforSelectView',
    store: 'DataTransforSelectStore',
    itemId:'dataTransforSelectViewId',
    scrollable:true,
    split:1
});