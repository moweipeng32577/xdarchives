/**
 * Created by Administrator on 2020/4/27.
 */


Ext.define('MyPlaceOrder.view.MyOrderGridView',{
    extend:'Comps.view.BasicGridView',
    xtype:'myOrderGridView',
    itemId:'myOrderGridViewID',
    searchstore:[
        {item: 'placeuser', name: '预约人'},
        {item: 'phonenumber', name: '联系电话'},
        {item: 'useway', name: '使用用途'}
    ],
    tbar:{
        overflowHandler:'scroller',
        items:[{
            text:'查看',
            iconCls:'fa fa-eye',
            itemId:'look'
        },'-',{
            text:'取消预约',
            iconCls:'fa fa-times',
            itemId:'cancel'
        },'-',{
            text:'打印',
            iconCls:'fa fa-print',
            itemId:'printId'
        }]
    },
    store: 'MyOrderGridStore',
    columns: [
        {text: '开始时间', dataIndex: 'starttime', flex: 1, menuDisabled: true},
        {text: '结束时间', dataIndex: 'endtime', flex: 1, menuDisabled: true},
        {text: '楼层',dataIndex:'floor',flex:1, menuDisabled: true},
        {text: '场地',dataIndex:'placeName',flex:2,menuDisabled: true},
        {text: '预约人', dataIndex: 'placeuser', flex: 1, menuDisabled: true},
        {text: '联系电话', dataIndex: 'phonenumber', flex: 1, menuDisabled: true},
        {text: '预约时间', dataIndex: 'ordertime',flex: 1, menuDisabled: true},
        {text: '使用用途', dataIndex: 'useway', flex: 2, menuDisabled: true},
        {text: '预约状态', dataIndex: 'state', flex: 1, menuDisabled: true},
        {text: '取消原因', dataIndex: 'cancelreason', flex: 3, menuDisabled: true}
    ]
});
