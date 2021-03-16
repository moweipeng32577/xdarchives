/**
 * Created by Administrator on 2018/11/28.
 */

Ext.define('Borrowfinish.view.BorrowFinishGridView',{
    extend:'Comps.view.BasicGridView',
    xtype:'borrowFinishGridView',
    region: 'center',
    itemId:'borrowFinishGridViewid',
    hasSearchBar:false,
    hasCheckColumn:false,
    store: 'BorrowFinishGridStore',
    columns: [
        {text: '查档类型', dataIndex: 'type', flex: 2, menuDisabled: true},
        {text: '描述', dataIndex: 'desci', flex: 4, menuDisabled: true},
        {text: '查档时间', dataIndex: 'borrowdate', flex: 2, menuDisabled: true},
        {text: '申请查档天数', dataIndex: 'borrowts', flex: 2, menuDisabled: true},
        {text: '同意查档天数', dataIndex: 'borrowtyts', flex: 2, menuDisabled: true},
        {text: '审批状态', dataIndex: 'state', flex: 2, menuDisabled: true},
        {text: '办理完结时间', dataIndex: 'finishtime', flex: 2, menuDisabled: true}
    ]
});

