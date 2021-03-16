/**
 * Created by Administrator on 2020/4/21.
 */
var selectTimeModel = Ext.create("Ext.data.Store",{
    fields:["text","value"],
    data:[
        {text:"今天",value:"today"},
        {text:"明天",value:"tomorrow"},
        {text:"后天",value:"aftertomorrow"},
        {text:"一周",value:"week"},
        {text:"所有预约",value:"all"}
    ]
});


Ext.define('CarOrder.view.CarOrderManageView', {
    extend:'Ext.panel.Panel',
    xtype:'carOrderManageView',
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
            xtype:'carOrderManageGridView'
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
            store:'OrderManageGridStore',
            columns:[
                {text: '开始时间', dataIndex: 'starttime', flex: 1, menuDisabled: true},
                {text: '结束时间', dataIndex: 'endtime', flex: 1, menuDisabled: true},
                {text: '预约人', dataIndex: 'caruser', flex: 1, menuDisabled: true},
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
                    text:'借出',
                    iconCls:'',
                    itemId:'lend',
                    hidden :true
                },'-',{
                    text:'查看',
                    iconCls:'fa fa-eye',
                    itemId:'look'
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
                },{
                    xtype: "label",
                    text:'所选时间：'
                },{
                    xtype: 'combo',
                    itemId: 'selectTimeId',
                    store: selectTimeModel,
                    queryMode:'all',
                    name: 'selectTime',
                    hideLabel: true,
                    labelWidth: 60,
                    displayField: 'text',
                    valueField: 'value',
                    editable: false,
                    listeners: {
                        afterrender: function (combo) {
                            var store = combo.getStore();
                            var selectRd;
                            for(var i=0;i<store.getCount();i++){
                                var record = store.getAt(i);
                                if(record.get('value')=='week'){
                                    selectRd = record;
                                    break;
                                }
                            }
                            combo.select(record);
                            combo.fireEvent("select", combo, selectRd);
                        },
                        select:function (view,record) {
                            var southgrid = view.up('carOrderManageView').down('[itemId=southgrid]');
                            var carOrderManageGridView = view.up('carOrderManageView').down('carOrderManageGridView');
                            var select = carOrderManageGridView.getSelectionModel().getSelection();
                            var store = southgrid.getStore();
                            if(select.length==0){
                                store.proxy.extraParams.id = '@';
                            }else{
                                store.proxy.extraParams.id = select[select.length-1].get('id');
                            }
                            var selectTime = record.get('value');
                            store.proxy.extraParams.selectTime = selectTime;
                            store.reload();
                        }
                    }
                }]
            }
        }]
    }]
});
