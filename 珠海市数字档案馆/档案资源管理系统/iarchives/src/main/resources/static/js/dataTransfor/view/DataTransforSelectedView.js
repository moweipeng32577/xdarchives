Ext.define('DataTransfor.view.DataTransforSelectedView', {
    extend: 'Ext.tree.Panel',
    width: XD.treeWidth,
    xtype: 'dataTransforSelectedView',
    store: 'DataTransforSelectedStore',
    itemId:'dataTransforSelectedViewId',
    scrollable:true,
    split:1
});