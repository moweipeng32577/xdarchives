Ext.define('ArchivesCallout.view.ArchivesCalloutGridView',{
    extend:'Comps.view.BasicGridView',
    xtype:'ArchivesCalloutGridView',
    searchstore: [
        {item: "batchcode", name: "批次号"},
        {item: "assembly", name: "流水线"},
        {item: "searchivescode", name: "起始档号"},
        {item: "spillagecode", name: "漏号"}
    ],
    tbar:{
        overflowHandler:'scroller',
        items:[{
            text:'调档',
            iconCls:'fa fa-plus-circle',
            itemId:'add'
        },'-',{
            text:'修改',
            iconCls:'fa fa-pencil-square-o',
            itemId:'edit'
        },'-',{
            text:'删除',
            iconCls:'fa fa-trash-o',
            itemId:'del'
        },'-',{
            text:'流水线分配',
            iconCls:'fa fa-exchange',
            itemId:'allot'
        },'-',{
            text:'进件登记单',
            iconCls:'fa fa-print',
            itemId:'printIn'
        },'-',{
            text:'还件登记单',
            iconCls:'fa fa-print',
            itemId:'printOut'
        }]
    },
    columns:[
        {text: 'id', dataIndex: 'id', width:150, menuDisabled: true,hidden:true},
        {text: '批次号', dataIndex: 'batchcode', width:200, menuDisabled: true},
        {text: '批次名', dataIndex: 'batchname', width:150, menuDisabled: true},
        {text: '流水线', dataIndex: 'assembly', width:150, menuDisabled: true},
        {text: '起始档号', dataIndex: 'searchivescode', width:150, menuDisabled: true},
        {text: '漏号', dataIndex: 'spillagecode', width:150, menuDisabled: true},
        {text: '借出份数', dataIndex: 'lendcopies', width:150, menuDisabled: true},
        {text: '借出页数', dataIndex: 'lendpages', width:150, menuDisabled: true},
        {text: '借出管理员', dataIndex: 'lendadmin', width:150, menuDisabled: true},
        {text: '借出监理', dataIndex: 'lendsuperior', width:150, menuDisabled: true},
        {text: '借出说明', dataIndex: 'lendexplain', width:150, menuDisabled: true},
        {text: '归还份数', dataIndex: 'returncopies', width:150, menuDisabled: true},
        {text: '归还页数', dataIndex: 'returnpages', width:150, menuDisabled: true},
        {text: '归还时间', dataIndex: 'returntime', width:150, menuDisabled: true},
        {text: '归还人员', dataIndex: 'returncrew', width:150, menuDisabled: true},
        {text: '归还监理', dataIndex: 'returnsuperior', width:150, menuDisabled: true},
        {text: '归还说明', dataIndex: 'returnexplain', width:150, menuDisabled: true},
        {text: '归还状态', dataIndex: 'returnstatus', width:150, menuDisabled: true},
        {text: '批次状态', dataIndex: 'batchstatus', width:150, menuDisabled: true},
        {text: '移交状态', dataIndex: 'connectstatus', width:150, menuDisabled: true},
        {text: '流水线号', dataIndex: 'assemblycode', width:150, menuDisabled: true}
    ],
    store: 'ArchivesCalloutGridStore',
    hasSelectAllBox:true
});