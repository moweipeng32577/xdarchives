/**
 * Created by yl on 2017/10/26.
 */
Ext.define('DestructionBill.view.DestructionBillGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'destructionBillGridView',
    region: 'center',
    searchstore:[{item: "title", name: "单据标题"}, {item: "submitter", name: "送审人"}, {item: "reason", name: "销毁原因"}],
    tbar: [],
    store: 'DestructionBillGridStore',
    columns: [
    	{text: '审批环节', dataIndex: 'approvetext', flex: 2, menuDisabled: true},
        {text: '审批人', dataIndex: 'approveman', flex: 2, menuDisabled: true},
        {text: '单据标题', dataIndex: 'title', flex: 2, menuDisabled: true},
        {text: '送审人', dataIndex: 'submitter',flex: 1, menuDisabled: true},
        {text: '单据时间', dataIndex: 'approvaldate', flex: 1, menuDisabled: true},
        {text: '条目总数',  dataIndex: 'total',flex: 1, menuDisabled: true},
        {text: '销毁原因',dataIndex: 'reason', flex: 2, menuDisabled: true},
        {text: '销毁鉴定依据',dataIndex: 'destructionappraise', flex: 2, menuDisabled: true}
    ]
});