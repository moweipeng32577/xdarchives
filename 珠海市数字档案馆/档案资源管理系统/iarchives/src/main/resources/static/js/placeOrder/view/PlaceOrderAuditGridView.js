/**
 * Created by Administrator on 2020/4/24.
 */


Ext.define('PlaceOrder.view.PlaceOrderAuditGridView',{
    extend:'Comps.view.BasicGridView',
    xtype:'placeOrderAuditGridView',
    itemId:'placeOrderAuditGridViewID',
    searchstore:[
        {item: 'placeuser', name: '预约人'},
        {item: 'phonenumber', name: '联系电话'},
        {item: 'useway', name: '使用用途'}
    ],
    tbar:{
        overflowHandler:'scroller',
        items:[{
            text:'审核',
            iconCls:'fa fa-plus-circle',
            itemId:'placeOrderAudit'
        }]
    },
    store: 'PlaceOrderAuditGridStore',
    columns: [
        {text: '开始时间', dataIndex: 'starttime', flex: 1, menuDisabled: true},
        {text: '结束时间', dataIndex: 'endtime', flex: 1, menuDisabled: true},
        {text: '预约人', dataIndex: 'placeuser', flex: 1, menuDisabled: true},
        {text: '联系电话', dataIndex: 'phonenumber', flex: 1, menuDisabled: true},
        {text: '预约时间', dataIndex: 'ordertime',flex: 1, menuDisabled: true},
        {text: '使用用途', dataIndex: 'useway', flex: 2, menuDisabled: true},
        {text: '预约状态', dataIndex: 'state', flex: 1, menuDisabled: true},
        {text: '取消原因', dataIndex: 'cancelreason', flex: 3, menuDisabled: true}
    ]
});