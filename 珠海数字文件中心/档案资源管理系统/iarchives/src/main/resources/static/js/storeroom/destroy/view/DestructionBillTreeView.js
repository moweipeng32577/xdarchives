/**
 * Created by yl on 2017/10/31.
 */
var store = Ext.create('Ext.data.TreeStore', {
    root: {
        expanded: true,
        text: "销毁单据管理",
        children: [{
            text: "电子档案已销毁",
            id:4,
            leaf: true
        }, {
            text: "实体档案已销毁",
            id:7,
            leaf: true
        }]
    }
});
Ext.define('Destroy.view.DestructionBillTreeView', {
    extend: 'Ext.tree.Panel',
    xtype: 'destructionBillTreeView',
    store: store,
    autoScroll: true,
    containerScroll: true,
    hideHeaders: true
})