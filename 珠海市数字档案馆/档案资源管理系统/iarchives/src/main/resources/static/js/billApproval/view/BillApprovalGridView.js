/**
 * Created by xd on 2017/10/21.
 */
Ext.define('BillApproval.view.BillApprovalGridView',{
    extend: 'Comps.view.BasicGridView',
    xtype:'billApprovalGridView',
    region: 'north',
    height:'60%',
    itemId:'billApprovalGridViewID',
    hasSearchBar:false,
    tbar: [{
        itemId:'look',
        xtype: 'button',
        text: '查看单据'
    },'-',{
        itemId:'setlyqx',
        xtype: 'button',
        text: '设置权限'
    }],
    store: 'BillApprovalGridStore',
    columns: [
        {text: '单据标题', dataIndex: 'title', flex: 2, menuDisabled: true},
        {text: '送审人', dataIndex: 'submitter',flex: 1, menuDisabled: true},
        {text: '单据时间', dataIndex: 'approvaldate', flex: 1, menuDisabled: true},
        {text: '条目总数',  dataIndex: 'total',flex: 1, menuDisabled: true},
        {text: '销毁原因',dataIndex: 'reason', flex: 2, menuDisabled: true},
        {text: '审核状态', dataIndex: 'stateValue',flex: 1, menuDisabled: true}
    ]
});
