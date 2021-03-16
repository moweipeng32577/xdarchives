/**
 * Created by yl on 2017/10/27.
 */
Ext.define('BranchAudit.view.ThematicProdGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'thematicProdGridView',
    searchstore:[{item: "title", name: "专题名称"},{item: "thematiccontent", name: "专题描述"},{item:"thematictypes",name:"专题类型"},{item: "publishstate", name: "发布状态"}],
    tbar: [{
        xtype: 'button',
        itemId:'branchAudit',
        text: '审核'
    },{
        xtype: 'button',
        itemId:'thematicSeeBtnID',
        iconCls:'fa fa-eye',
        text: '查看'
    }, '-', {
        xtype: 'button',
        itemId:'compilation',
        iconCls:'fa fa-info-circle',
        text: '信息编研'
    }],
    store: 'ThematicProdGridStore',
    columns: [
        {text: '专题名称', dataIndex: 'title', flex: 0.45, menuDisabled: true},
        {text: '专题描述', dataIndex: 'thematiccontent', flex: 0.45, menuDisabled: true},
        {text: '专题类型', dataIndex: 'thematictypes', flex: 0.1, menuDisabled: true},
        {text: '发布状态', dataIndex: 'publishstate', flex: 0.1, menuDisabled: true},
        {text: '提交时间', dataIndex: 'submitedtime', flex: 0.1, menuDisabled: true}
    ]
});