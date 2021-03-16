/**
 * Created by Administrator on 2019/6/14.
 */
Ext.define('Accept.view.AcceptdocGridView',{
    extend:'Comps.view.BasicGridView',
    xtype:'AcceptdocGridView',
    searchstore: [
        {item: "submitter", name: "提交人"},
        {item: "accepter", name: "接收人"}
    ],
    tbar:{
        overflowHandler:'scroller',
        items:[
            {
                text:'新增单据',
                iconCls:'fa fa-plus-circle',
                itemId:'add'
            },'-',{
                text:'修改',
                iconCls:'fa fa-pencil-square-o',
                itemId:'update'
            },'-',{
                text:'打印',
                iconCls:'fa fa-print',
                itemId:'sampling'
            },'-',{
                text:'删除',
                iconCls:'fa fa-trash-o',
                itemId:'del'
            }
        ]
    },
    columns:[
        {text: '接收人', dataIndex: 'acceptdocid', flex: 2, menuDisabled: true,hidden:true},
        {text: '接收人', dataIndex: 'accepter', flex: 2, menuDisabled: true},
        {text: '接收单位', dataIndex: 'organ', flex: 2, menuDisabled: true},
        {text: '接收日期', dataIndex: 'accepdate', flex: 2, menuDisabled: true},
        {text: '提交人', dataIndex: 'submitter', flex: 2, menuDisabled: true},
        {text: '提交单位', dataIndex: 'submitorgan', flex: 2, menuDisabled: true},
        {text: '提交日期', dataIndex: 'submitdate', flex: 2, menuDisabled: true},
        {text: '档案数量', dataIndex: 'archivenum', flex: 2, menuDisabled: true},
        {text: '正在消毒', dataIndex: 'sterilizing', flex: 1.5, menuDisabled: true},
        {text: '已消毒', dataIndex: 'sterilized', flex: 1.5, menuDisabled: true},
        {text: '已入库', dataIndex: 'finishstore', flex: 1.5, menuDisabled: true},
        {text: '备注', dataIndex: 'docremark', flex: 2, menuDisabled: true}
    ],
    store: 'AcceptdocFinishGridStore',
    hasSelectAllBox:true
});
