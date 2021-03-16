/**
 * Created by Administrator on 2020/6/4.
 */


Ext.define('Outware.view.StBorrowdocDetailGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'stBorrowdocDetailGridView',
    itemId: 'stBorrowdocDetailGridViewId',
    hasSearchBar: false,
    store: 'StBorrowdocDetailGridStore',
    tbar: [{
        itemId:'lookEntry',
        xtype: 'button',
        text: '查看'
    }],
    columns: [
        {text: '题名', dataIndex: 'title', flex: 2, menuDisabled: true},
        {text: '文件编号', dataIndex: 'filenumber', flex: 2, menuDisabled: true},
        {text: '档号', dataIndex: 'archivecode', flex: 2, menuDisabled: true},
        {text: '全宗号', dataIndex: 'funds', flex: 2, menuDisabled: true},
        {text: '目录号', dataIndex: 'catalog', flex: 2, menuDisabled: true},
        {text: '案卷号', dataIndex: 'filecode', flex: 2, menuDisabled: true},
        {text: '件号', dataIndex: 'recordcode', flex: 2, menuDisabled: true},
        {text: '保管期限', dataIndex: 'entryretention', flex: 2, menuDisabled: true},
        {text: '归档年度', dataIndex: 'filingyear', flex: 2, menuDisabled: true},
        {text: '页数', dataIndex: 'pages', flex: 2, menuDisabled: true},
        {text: '门类', dataIndex: 'nodefullname', flex: 4, menuDisabled: true},
        {text: '归还状态', dataIndex: 'state', flex: 2, menuDisabled: true},
        {text: '查档类型', dataIndex: 'type', flex: 2, menuDisabled: true},
        {text: '库存位置', dataIndex: 'entrystorage', flex: 3, menuDisabled: true}
    ]
});
