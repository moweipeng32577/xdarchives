/**
 * Created by Administrator on 2017/10/24 0024.
 */
Ext.define('Restitution.view.RestitutionYghView',{
    extend:'Comps.view.BasicGridView',
    xtype:'restitutionYghView',
    itemId:'restitutionYghViewID',
    searchstore:[
        // {item: "filenumber", name: "文件编号"},
        {item: "archivecode", name: "档号"},
        {item: "funds", name: "全宗号"},
        {item: "catalog", name: "目录号"},
        {item: "borrowman", name: "查档人"},
        {item: "filecode", name: "案卷号"},
        {item: "recordcode", name: "件号"},
        {item: "title", name: "题名"},
        {item: "responsible", name: "责任者"},
        {item: "approver", name: "审批人"},
        {item: "borrowdate", name: "查档时间"},
        {item: "jybackdate", name: "审批通过时间"}
    ],
    tbar: [
        {
        itemId:'print',
        xtype: 'button',
        text: '打印清册',
        iconCls:'fa fa-print'
    },'-',{
        itemId:'refresh',
        xtype: 'button',
        text: '刷新',
        iconCls:'fa fa-refresh'
    },{
            itemId: 'lookEntryId',
            xtype: 'button',
            text: '查看'
    }],
    store: 'RestitutionYghStore',
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
        {text: '查档人', dataIndex: 'borrowman', flex: 2, menuDisabled: true},
        {text: '查档人电话号码', dataIndex: 'borrowmantel', flex: 2, menuDisabled: true},
        {text: '审批人', dataIndex: 'approver', flex: 2, menuDisabled: true},
        {text: '查档时间', dataIndex: 'borrowdate', flex: 2, menuDisabled: true},
        {text: '审批通过时间', dataIndex: 'jybackdate', flex: 2, menuDisabled: true},
        {text: '归还时间', dataIndex: 'returntime', flex: 2, menuDisabled: true},
        {text: '归还人账号', dataIndex: 'returnloginname', flex: 2, menuDisabled: true},
        {text: '归还人', dataIndex: 'returnware', flex: 2, menuDisabled: true},
        {text: '备注', dataIndex: 'description', flex: 3, menuDisabled: true}
    ]
});