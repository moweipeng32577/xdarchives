/**
 * Created by yl on 2017/10/31.
 */
var store = Ext.create('Ext.data.TreeStore', {
    root: {
        expanded: true,
        text: "销毁单据管理",
        children: [{
            text: "未送审",
            id:0,
            leaf: true
        }, {
            text: "待审核",
            id:1,
            leaf: true
        }, {
            text: "已审核",
            id:2,
            leaf: true
        },
        //     {
        //     text: "已审核(不通过)",
        //     id:3,
        //     leaf: true
        // },
            {
            text: "已执行",
            id:4,
            leaf: true
        }, {
            text: "已退回",
            id:5,
            leaf: true
        }]
    }
});
Ext.define('DestructionBill.view.DestructionBillTreeView', {
    extend: 'Ext.tree.Panel',
    xtype: 'destructionBillTreeView',
    store: store,
    autoScroll: true,
    containerScroll: true,
    hideHeaders: true
})