/**
 * Created by Administrator on 2018/10/23.
 */

Ext.define('Mission.view.DzJyGridView',{
    extend:'Comps.view.BasicGridView',
    xtype:'dzJyGridView',
    title: '',
    region: 'center',
    itemId:'dzJyGridViewID',
    searchstore:[{item: "text", name: "查档人"},{item: "type", name: "查档类型"},
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
        iconCls:'fa fa-tasks'
    },'-', {
        xtype: 'button',
        itemId: 'urging',
        text: '催办',
        iconCls:'fa fa-print',
        hidden :true
    },{
        xtype: "checkboxfield",
        boxLabel : '发送短信',
        itemId:'message',
        checked:true,
        hidden :true
    }],
    store: 'DzJyGridStore',
    columns: [
        {text: '查档人', dataIndex: 'text', flex: 2, menuDisabled: true},
        {text: '审批状态', dataIndex: 'state', flex: 2, menuDisabled: true},
        {text: '审批环节', dataIndex: 'approvetext', flex: 2, menuDisabled: true},
        {text: '审批人', dataIndex: 'approveman', flex: 2, menuDisabled: true},
        {text: '查档类型', dataIndex: 'type', flex: 2, menuDisabled: true}
    ]
});

