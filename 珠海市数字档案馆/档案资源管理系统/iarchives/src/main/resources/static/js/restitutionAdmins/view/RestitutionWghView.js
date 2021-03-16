/**
 * Created by Administrator on 2017/10/24 0024.
 */
Ext.define('Restitution.view.RestitutionWghView',{
    extend:'Comps.view.BasicGridView',
    xtype:'restitutionWghView',
    itemId:'restitutionWghViewID',
    viewConfig: {
        forceFit: true,
        stripeRows: true,
        getRowClass: function(record,rowIndex, rowParams, store) {
            if(record.get('backdate')==getDateStr(0)){//到期
                return 'x-grid-record-yellow';
            }else if(record.get('backdate')==getDateStr(1)){//即将到期
                return 'x-grid-record-green';
            }else if(record.get('backdate')<getDateStr(0)){//过期
                return 'x-grid-record-red';
            }
        }
    },
    searchstore:[
        // {item: "filenumber", name: "文件编号"},
        {item: "archivecode", name: "档号"},
        {item: "funds", name: "全宗号"},
        {item: "catalog", name: "目录号"},
        {item: "filecode", name: "案卷号"},
        {item: "recordcode", name: "件号"},
        {item: "title", name: "题名"},
        {item: "responsible", name: "责任者"},
        {item: "borrowman", name: "查档人"},
        {item: "borrowdate", name: "查档时间"},
        {item: "jybackdate", name: "审批通过时间"}
        // {item: "backdate", name: "到期时间"}
    ],
    tbar: [{
        itemId:'restitution',
        xtype: 'button',
        text: '归还',
        iconCls:'fa fa-mail-reply'
    },'-',{
        itemId:'renewal',
        xtype: 'button',
        text: '续查',
        iconCls:'fa fa-calendar-plus-o'
    },'-',{
        itemId:'print',
        xtype: 'button',
        text: '打印清册',
        iconCls:'fa fa-print'
    },'-',{
        itemId:'reason',
        xtype: 'button',
        text: '查看续查理由',
        iconCls:'fa fa-eye'
    },'-',{
        itemId:'refresh',
        xtype: 'button',
        text: '刷新',
        iconCls:'fa fa-refresh'
    },{
        itemId: 'lookEntryId',
        xtype: 'button',
        text: '查看'
    }
    // ,'-',{
    //     itemId:'askToReturn',
    //     xtype: 'button',
    //     text: '催还'
    // }
    ],
    store: 'RestitutionWghStore',
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
        {text: '查档天数', dataIndex: 'jyts', flex: 2, menuDisabled: true},
        {text: '到期时间', dataIndex: 'backdate', flex: 2, menuDisabled: true},
        {text: '续查理由', dataIndex: 'renewreason', flex: 2, menuDisabled: true}
    ]
});

function getDateStr(AddDayCount) {
    var dd = new Date();
    dd.setDate(dd.getDate()+AddDayCount);//获取AddDayCount天后的日期
    var y = dd.getFullYear();
    var m = dd.getMonth()+1;//获取当前月份的日期
    var d = dd.getDate();
    if (m >= 1 && m <= 9) {
        m = "0" + m;
    }
    if (d >= 0 && d <= 9) {
        d = "0" + d;
    }
    return y+""+m+""+d;
}