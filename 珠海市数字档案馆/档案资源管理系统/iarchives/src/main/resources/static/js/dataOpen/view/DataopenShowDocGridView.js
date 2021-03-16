/**
 * Created by RonJiang on 2018/4/20 0020.
 */
Ext.define('Dataopen.view.DataopenShowDocGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype:'dataopenShowDocGridView',
    itemId:'dataopenShowDocGridView',
    hasCloseButton:false,
    searchstore:[
        {item: "doctitle", name: "单据题名"},
        {item: "submitdate", name: "送审日期"},
        {item: "batchnum", name: "开放批次号"},
        {item: "entrycount", name: "条目数量"},
        {item: "opentype", name: "开放类型"},
        {item: "remarks", name: "备注信息"},
        {item: "approve", name: "批注"},
        {item: "state", name: "状态"},
        {item: "opendate", name: "开放日期"}
    ],
    tbar: [{
        itemId:'showEntryDetail',
        xtype: 'button',
        iconCls:'fa fa-info',
        text: '详细内容'
    },'-',{
        itemId:'showEntryInfo',
        xtype: 'button',
        iconCls:'fa fa-eye',
        text: '单据详情'
    },'-',{
        itemId:'kfDealDetailsId',
        xtype: 'button',
        iconCls:'fa fa-newspaper-o',
        text: '办理详情'
    // },'-',{
    //     itemId:'print',
    //     xtype: 'button',
    //     iconCls:'fa fa-print',
    //     text: '打印'
    },'-',{
        itemId:'printOpen',
        xtype: 'button',
        iconCls:'fa fa-print',
        text: '打印开放审查登记表'
    },'-',{
        itemId:'printNotOpen',
        xtype: 'button',
        iconCls:'fa fa-print',
        text: '打印不开放审查登记表'
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
    },'-',{
        itemId:'back',
        xtype: 'button',
        iconCls:'fa fa-undo',
        text: '返回'
    }],
    store: 'OpendocGridStore',
    columns: [
        {text: '单据题名', dataIndex: 'doctitle', flex: 2, menuDisabled: true},
        {text: '送审人', dataIndex: 'submitter', flex: 2, menuDisabled: true},
        {text: '送审日期', dataIndex: 'submitdate', flex: 2, menuDisabled: true},
        {text: '开放批次号', dataIndex: 'batchnum', flex: 2, menuDisabled: true},
        {text: '条目数量', dataIndex: 'entrycount', flex: 2, menuDisabled: true},
        {text: '开放类型', dataIndex: 'opentype', flex: 2, menuDisabled: true},
        {text: '备注信息', dataIndex: 'remarks', flex: 2, menuDisabled: true},
        {text: '批注', dataIndex: 'approve', flex: 2, menuDisabled: true},
        {text: '状态', dataIndex: 'state', flex: 2, menuDisabled: true},
        {text: '开放日期', dataIndex: 'opendate', flex: 2, menuDisabled: true}
    ]
});