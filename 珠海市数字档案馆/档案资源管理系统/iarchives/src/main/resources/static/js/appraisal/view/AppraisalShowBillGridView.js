/**
 * Created by RonJiang on 2018/4/20 0020.
 */
Ext.define('Appraisal.view.AppraisalShowBillGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype:'appraisalShowBillGridView',
    searchstore:[
        {item: "title", name: "单据题名"},
        {item: "approvaldate", name: "单据时间"},
        {item: "total", name: "条目总数"},
        {item: "reason", name: "销毁原因"}
    ],
    tbar: [{
        itemId:'showEntryDetail',
        xtype: 'button',
        text: '详细内容'
    }, '-',
        {
            itemId:'xhDealDetailsId',
            xtype: 'button',
            iconCls:'fa fa-newspaper-o',
            text: '办理详情'
        },
        '-',
        {
        itemId:'print',
        xtype: 'button',
        text: '打印'
    },  '-',{
        itemId:'back',
        xtype: 'button',
        text: '返回'
    }],
    store: 'BillGridStore',
    columns: [
        {text: '单据题名', dataIndex: 'title', flex: 2, menuDisabled: true},
        {text: '单据时间', dataIndex: 'approvaldate', flex: 2, menuDisabled: true},
        {text: '条目总数', dataIndex: 'total', flex: 2, menuDisabled: true},
        {text: '销毁原因', dataIndex: 'reason', flex: 3, menuDisabled: true},
        {text: '提交人', dataIndex: 'submitter', flex: 2, menuDisabled: true},
        {text: '状态', dataIndex: 'state', flex: 2, menuDisabled: true}
    ]
});