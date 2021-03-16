/**
 * Created by Administrator on 2020/4/28.
 */


Ext.define('Reservation.view.PlaceOrderManageView', {
    extend:'Ext.panel.Panel',
    xtype:'placeOrderManageView',
    layout:'fit',
    items:[
        {
            itemId:'pairgrid',
            layout:{
                type:'vbox',
                pack: 'start',
                align: 'stretch'
            },
            items:[{
                flex:3,
                itemId:'northgrid',
                xtype:'placeOrderManageGridView'
            },{
                flex:7,
                title:'预约列表',
                itemId:'southgrid',
                xtype:'basicgrid',
                collapsible:true,
                collapseToolText:'收起',
                expandToolText:'展开',
                collapsed: false,
                split:true,
                allowDrag:true,
                hasSearchBar:false,
                expandOrcollapse:'expand',//默认打开
                store:'PlaceOrderGridStore',
                columns:[
                    {text: '开始时间', dataIndex: 'starttime', flex: 1, menuDisabled: true},
                    {text: '结束时间', dataIndex: 'endtime', flex: 1, menuDisabled: true},
                    {text: '预约人', dataIndex: 'placeuser', flex: 1, menuDisabled: true},
                    {text: '联系电话', dataIndex: 'phonenumber', flex: 1, menuDisabled: true},
                    {text: '预约时间', dataIndex: 'ordertime',flex: 1, menuDisabled: true},
                    {text: '使用用途', dataIndex: 'useway', flex: 2, menuDisabled: true},
                    {text: '预约状态', dataIndex: 'state', flex: 1, menuDisabled: true},
                    {text: '取消原因', dataIndex: 'cancelreason', flex: 3, menuDisabled: true}
                ],
                tbar:{
                    overflowHandler:'scroller',
                    items:[{
                        text:'新增预约',
                        iconCls:'fa fa-plus-circle',
                        itemId:'add',
                        hidden :true
                    },'-',{
                        text:'取消预约',
                        iconCls:'fa fa-times',
                        itemId:'cancel',
                        hidden :true
                    },'-',{
                        text:'删除预约',
                        iconCls:'fa fa-trash-o',
                        itemId:'del',
                        hidden :true
                    },'-',{
                        text:'查看',
                        iconCls:'fa fa-eye',
                        itemId:'look',
                        hidden :true
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
                    }]
                }
            }]
        }]
});
