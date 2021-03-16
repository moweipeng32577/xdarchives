/**
 * Created by tanly on 2017/11/1 0024.
 */
Ext.define('Watermark.view.WatermarkTreeView', {
    itemId:'WatermarkTreeViewID',
    extend: 'Ext.tree.Panel',
    xtype: 'WatermarkTreeView',
    store: 'WatermarkTreeStore',
    autoScroll: true,
    containerScroll: true,
    hideHeaders: true
});