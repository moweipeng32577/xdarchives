/**
 * Created by yl on 2017/11/3.
 */
Ext.define('JyAdmins.view.LookBorrowdocMxGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'lookBorrowdocMxGridView',
    itemId: 'lookBorrowdocMxGridViewId',
    region: 'center',
    hasSearchBar: false,
    tbar: [{
        itemId: 'lookMedia',
        xtype: 'button',
        text: '查看原文'
    }, {
        itemId: 'renew',
        xtype: 'button',
        text: '续借'
    },  {
        itemId: 'lookEle',
        xtype: 'button',
        hidden:true,
        text: '查看'
    },{
        itemId: 'printEle',
        xtype: 'button',
        hidden:true,
        text: '打印'
    }, {
        itemId: 'lookEntryId',
        xtype: 'button',
        text: '查看'
    },{
        itemId: 'hide',
        xtype: 'button',
        text: '返回'
    }],
    store: 'LookBorrowdocMxGridStore',
    columns: [
        {
            xtype:'actioncolumn',
            resizable:false,//不可拉伸
            hideable:false,
            header: '原文',
            dataIndex: 'eleid',
            sortable:true,
            width:60,
            align:'center',
            items:['@file']
        },
        {text: 'borrowmsgid', dataIndex: 'entrystorage', flex: 2, hidden: true},
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
        {text: '审批通过时间', dataIndex: 'serial', flex: 2, menuDisabled: true,itemId:'seriald'},
        {text: '同意查档天数', dataIndex: 'entrysecurity', flex: 2, menuDisabled: true,itemId:'entrysecurityId'},
        {text: '到期时间', dataIndex: 'responsible', flex: 2, menuDisabled: true,itemId:'responsibleId'},
        {text: '审批结果', dataIndex: 'lyqx', flex: 2, menuDisabled: true,itemId:'lyqxId'},
        {text: '归还状态', dataIndex: 'state', flex: 2, menuDisabled: true,itemId:'reItem'},
        {text: '查档类型', dataIndex: 'type', flex: 2, menuDisabled: true}
    ]
});