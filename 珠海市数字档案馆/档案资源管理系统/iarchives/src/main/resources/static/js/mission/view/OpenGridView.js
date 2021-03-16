/**
 * Created by tanly on 2017/12/7 0007.
 */
Ext.define('Mission.view.OpenGridView',{
    extend:'Comps.view.BasicGridView',
    xtype:'openGridView',
    title: '',
    region: 'center',
    itemId:'openGridViewID',
    searchstore:[{item: "text", name: "送审人"},{item: "type", name: "类型"},
        {item: "state", name: "审批状态"},{item: "approvetext", name: "审批环节"},{item: "approveman", name: "审批人"}],
    tbar: [{
        itemId:'transactId',
        xtype: 'button',
        text: '办理',
        iconCls:'fa fa-tasks'
    },'-',{
        itemId:'lookBillsId',
        xtype: 'button',
        text: '查看单据',
        hidden: true,
        iconCls:'fa fa-tasks'
    }],
    store: 'OpenGridStore',
    columns: [
        {text: '送审人', dataIndex: 'text', flex: 2, menuDisabled: true},
        {text: '审批状态', dataIndex: 'state', flex: 2, menuDisabled: true},
        {text: '审批环节', dataIndex: 'approvetext', flex: 2, menuDisabled: true},
        {text: '审批人', dataIndex: 'approveman', flex: 2, menuDisabled: true},
        {text: '类型', dataIndex: 'type', flex: 2, menuDisabled: true}
    ]
});