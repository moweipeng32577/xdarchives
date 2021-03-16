/**
 * Created by Administrator on 2019/6/24.
 */
Ext.define('Accept.view.FinishstoreGridView',{
    extend:'Comps.view.BasicGridView',
    xtype:'FinishstoreGridView',
    searchstore: [
        {item: "submitter", name: "提交人"},
        {item: "accepter", name: "接收人"}
    ],
    /*tbar:{
     overflowHandler:'scroller',
     items:[
     ]
     },*/
    columns:[
        {text: '接收人', dataIndex: 'accepter', flex: 2, menuDisabled: true},
        {text: '接收单位', dataIndex: 'organ', flex: 2, menuDisabled: true},
        {text: '接收日期', dataIndex: 'accepdate', flex: 2, menuDisabled: true},
        {text: '提交人', dataIndex: 'submitter', flex: 2, menuDisabled: true},
        {text: '提交单位', dataIndex: 'submitter', flex: 2, menuDisabled: true},
        {text: '提交日期', dataIndex: 'submitdate', flex: 2, menuDisabled: true},
        {text: '已入库', dataIndex: 'finishstore', flex: 2, menuDisabled: true},
        {text: '备注', dataIndex: 'docremark', flex: 2, menuDisabled: true}
    ],
    store: 'AcceptdocFinishGridStore',
    hasSelectAllBox:true
});
