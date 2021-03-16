/**
 * Created by tanly on 2017/12/5.
 */
Ext.define('OpenApprove.view.OpenApproveGridView',{
    extend: 'Comps.view.BasicGridView',
    xtype:'openApproveGridView',
    region: 'north',
    height:'45%',
    itemId:'openApproveGridViewID',
    searchstore: [
        {item: "archivecode", name: "档号"},
        {item: "title", name: "题名"},
        {item: "responsible", name: "责任者"},
        {item: "filenumber", name: "文件编号"},
        {item: "funds", name: "全宗号"},
        {item: "catalog", name: "目录号"},
        {item: "filecode", name: "案卷号"},
        {item: "result", name: "开放状态"},
        {item: "appraisedata", name: "鉴定依据"},
        {item: "firstappraiser", name: "初审人"},
        {item: "lastappraiser", name: "复审人"}
    ],
    hasSelectAllBox:true,
    tbar: [{
        itemId:'look',
        xtype: 'button',
        text: '查看'
    },'-',{
        itemId:'setkfqx',
        xtype: 'button',
        text: '设置开放权限'
    },'-',{
        itemId:'printOpen',
        xtype: 'button',
        iconCls:'fa fa-print',
        text: '打印开放审查登记表'
    },'-',{
        itemId:'printNotOpen',
        xtype: 'button',
        iconCls:'fa fa-print',
        text: '打印不开放审查登记表'
    },'-',{
        itemId:'printOpenAppraisal',
        xtype: 'button',
        iconCls:'fa fa-print',
        text: '打印档案开放鉴定汇报表'
    },'-',{
        itemId:'startApprove',
        xtype: 'button',
        text: '开始审核'
    }],
    store: 'OpenApproveGridStore',
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
        {text: '题名', dataIndex: 'title', flex: 2, menuDisabled: true},
        {text: '档号', dataIndex: 'archivecode', flex: 2, menuDisabled: true},
        {text: '责任者', dataIndex: 'responsible', flex: 2, menuDisabled: true},
        {text: '文件编号', dataIndex: 'filenumber', flex: 2, menuDisabled: true},
        {text: '全宗号', dataIndex: 'funds', flex: 2, menuDisabled: true},
        {text: '目录号', dataIndex: 'catalog', flex: 2, menuDisabled: true},
        {text: '案卷号', dataIndex: 'filecode', flex: 2, menuDisabled: true},
        {text: '拟开放状态', dataIndex: 'firstresult', flex: 2, menuDisabled: true},
        {text: '复审开放状态', dataIndex: 'lastresult', flex: 2, menuDisabled: true},
        {text: '开放状态', dataIndex: 'result', flex: 2, menuDisabled: true},
        {text: '鉴定依据', dataIndex: 'appraisedata', flex: 2, menuDisabled: true},
        {text: '初审意见', dataIndex: 'appraisetext', flex: 2, menuDisabled: true},
        {text: '初审人', dataIndex: 'firstappraiser', flex: 2, menuDisabled: true},
        {text: '复审意见', dataIndex: 'lastappraisetext', flex: 2, menuDisabled: true},
        {text: '复审人', dataIndex: 'lastappraiser', flex: 2, menuDisabled: true}
    ]
});
