/**
 * Created by xd on 2017/10/21.
 */
Ext.define('JyAdmins.view.DzJyGridView',{
    extend:'Comps.view.BasicGridView',
    xtype:'dzJyGridView',
    title: '',
    region: 'center',
    itemId:'dzJyGridViewID',
    searchstore:[{item: "desci", name: "描述"},{item: "borrowman", name: "查档人"},{item: "borrowmd", name: "查档目的"},
        {item: "borroworgan", name: "查档（接收）单位"},{item: "borrowdate", name: "查档时间"},{item: "borrowts", name: "申请查档天数"},
        {item: "returnstate", name: "归还状态"}],
    tbar: [{
            itemId:'dzDealDetailsId',
            xtype: 'button',
            iconCls:'fa fa-newspaper-o',
            text: '办理详情'
    }, '-', {
        itemId:'lookBorrowMsgId',
        xtype: 'button',
        iconCls:'fa fa-newspaper-o',
        text: '查看单据信息'
    }, '-',{
        itemId:'showFileId',
        xtype: 'button',
        iconCls:'fa fa-newspaper-o',
        text: '出具查无此档证明'
    }, '-', {
        itemId:'print',
        iconCls:'fa fa-print',
        text: '打印单据清册'
    }, '-', {
        iconCls: '',
        hidden:true,
        menu: [{
            itemId:'printId',
            iconCls:'fa fa-print',
            text: '打印单据清册'
        }, '-', {
            itemId: 'printApproval',
            iconCls: 'fa fa-print',
            text: '打印调档审批单据',
        }, '-', {
            itemId: 'printEleApproval',
            iconCls: 'fa fa-print',
            text: '打印电子数据审批单',
        }],
        text: '打印'
    }, '-', {
        itemId:'appraise',
        xtype: 'button',
        iconCls:'fa fa-check-circle-o',
        text: '使用评分'
    },'-', {
        xtype: 'button',
        itemId: 'urging',
        text: '催办',
        iconCls:'fa fa-print',
        hidden :true
    // },'-',{
    //     itemId:'printTranster',
    //     xtype: 'button',
    //     iconCls:'fa fa-print',
    //     text: '打印调出审批表'
    },{
        xtype: "checkboxfield",
        boxLabel : '发送短信',
        itemId:'message',
        checked:true,
        hidden :true
    }],
    store: 'DzJyGridStore',
    columns: [
        {text: '描述', dataIndex: 'desci', flex: 4, menuDisabled: true},
        {text: '查档人', dataIndex: 'borrowman', flex: 2, menuDisabled: true},
        {text: '查档目的', dataIndex: 'borrowmd', flex: 2, menuDisabled: true},
        {text: '查档（接收）单位', dataIndex: 'borroworgan', flex: 2, menuDisabled: true},
        {text: '查档时间', dataIndex: 'borrowdate', flex: 2, menuDisabled: true},
        {text: '申请查档天数', dataIndex: 'borrowts', flex: 2, menuDisabled: true},
        {text: '归还状态', dataIndex: 'returnstate', flex: 2, menuDisabled: true,itemId:'returnstateId'}
    ]
});
