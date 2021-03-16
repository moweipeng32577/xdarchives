/**
 * Created by Administrator on 2018/10/23.
 */

Ext.define('Mission.view.StJyGridView',{
    extend:'Comps.view.BasicGridView',
    xtype:'stJyGridView',
    title: '',
    region: 'center',
    itemId:'stJyGridViewID',
    hasSearchBar:false,
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
    }],
    store: 'StJyGridStore',
    columns: [
        {text: '查档人', dataIndex: 'text', flex: 2, menuDisabled: true},
        {text: '审批状态', dataIndex: 'state', flex: 2, menuDisabled: true},
        {text: '审批环节', dataIndex: 'approvetext', flex: 2, menuDisabled: true},
        {text: '审批人', dataIndex: 'approveman', flex: 2, menuDisabled: true},
        {text: '查档类型', dataIndex: 'type', flex: 2, menuDisabled: true}
    ]
});
