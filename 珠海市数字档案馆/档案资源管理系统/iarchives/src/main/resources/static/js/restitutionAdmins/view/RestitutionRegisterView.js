/**
 * Created by Administrator on 2017/10/24 0024.
 */
Ext.define('Restitution.view.RestitutionRegisterView',{
    extend:'Comps.view.BasicGridView',
    xtype:'restitutionRegisterView',
    itemId:'restitutionRegisterID',
    searchstore:[
        {item: "borrowman", name: "查档人"},
        {item: "borrowdate", name: "查档时间"},
        {item: "desci", name: "查档描述"}
    ],
    tbar: [{
        itemId:'lendRegister',
        xtype: 'button',
        text: '查档登记',
        iconCls:'fa fa-calendar-plus-o'
    },'-',{
        itemId:'delete',
        xtype: 'button',
        text: '删除',
        iconCls:'fa fa-trash-o'
    },'-',{
        itemId:'print',
        xtype: 'button',
        text: '打印清册',
        iconCls:'fa fa-print'
    },'-',{
        itemId:'updataEntries',
        xtype: 'button',
        text: '修改',
        iconCls:'fa fa-print'
    },'-',{
        itemId:'checkEntries',
        xtype: 'button',
        text: '查看',
        iconCls:'fa fa-eye'
    },'-',{
        itemId:'moveEntries',
        xtype: 'button',
        text: '转出',
        iconCls:'	fa fa-exchange'
    }
    ],
    store: 'RestitutionRegisterStore',
    columns: [
        {text: '查档人', dataIndex: 'borrowman', flex: 2, menuDisabled: true},
        {text: '查档时间', dataIndex: 'borrowdate', flex: 2, menuDisabled: true},
        {text: '查档目的', dataIndex: 'borrowmd', flex: 2, menuDisabled: true},
        {text: '查档（接收）单位', dataIndex: 'borroworgan', flex: 2, menuDisabled: true},
        {text: '查档描述', dataIndex: 'desci', flex: 2, menuDisabled: true},
        {text: '查档天数', dataIndex: 'borrowts', flex: 2, menuDisabled: true},
        {text: '查档人号码', dataIndex: 'borrowmantel', flex: 2, menuDisabled: true},
        {text: '归还状态', dataIndex: 'returnstate', flex: 2, menuDisabled: true}
    ]
});
