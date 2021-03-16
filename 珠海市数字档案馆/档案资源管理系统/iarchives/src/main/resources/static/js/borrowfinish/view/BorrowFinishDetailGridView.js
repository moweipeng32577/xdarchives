/**
 * Created by Administrator on 2018/11/28.
 */

Ext.define('Borrowfinish.view.BorrowFinishDetailGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'borrowFinishDetailGridView',
    itemId: 'borrowFinishDetailGridViewid',
    region: 'center',
    hasSearchBar: false,
    tbar: [{
        itemId: 'lookMedia',
        xtype: 'button',
        text: '查看原文'
    },{
        itemId: 'hide',
        xtype: 'button',
        text: '返回'
    }],
    store: 'BorrowFinishDetailGridStore',
    columns: [
        {text: 'borrowmsgid', dataIndex: 'entrystorage', flex: 2, hidden: true},
        {text: '题名', dataIndex: 'title', flex: 2, menuDisabled: true},
        {text: '文件编号', dataIndex: 'filenumber', flex: 2, menuDisabled: true},
        {text: '档号', dataIndex: 'archivecode', flex: 2, menuDisabled: true},
        {text: '全宗号', dataIndex: 'funds', flex: 2, menuDisabled: true},
        {text: '目录号', dataIndex: 'catalog', flex: 2, menuDisabled: true},
        {text: '审批通过时间', dataIndex: 'serial', flex: 2, menuDisabled: true},
        {text: '同意查档天数', dataIndex: 'entrysecurity', flex: 2, menuDisabled: true},
        {text: '到期时间', dataIndex: 'responsible', flex: 2, menuDisabled: true},
        {text: '审批结果', dataIndex: 'lyqx', flex: 2, menuDisabled: true},
        {text: '归还状态', dataIndex: 'pages', flex: 2, menuDisabled: true,itemId:'reItem'}
    ]
});
