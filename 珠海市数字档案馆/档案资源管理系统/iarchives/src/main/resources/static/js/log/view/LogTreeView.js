/**
 * Created by xd on 2017/10/21.
 */
Ext.define('Log.view.LogTreeView', {
    extend: 'Ext.tree.Panel',
    xtype: 'logTreeView',
    store: 'LogTreeStore',
    autoScroll: true,
    containerScroll: true,
    hideHeaders: true
});