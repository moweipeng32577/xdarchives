/**
 * Created by RonJiang on 2018/4/17 0017.
 */
Ext.define('Feedback.view.FeedbackGridView',{
    extend: 'Comps.view.BasicGridView',
    xtype:'feedbackGridView',
    itemId:'feedbackGridViewID',
    hasCloseButton:false,
    bodyBorder: false,
    store: 'FeedbackGridStore',
    searchstore:[
        {item: "title", name: "标题"},
        {item: "askman", name: "投件人"},
        {item: "asktime", name: "提问时间"},
        {item: "flag", name: "回复状态"}
    ],
    tbar: [{
        itemId:'feedbackAdd',
        xtype: 'button',
        text: '增加',
        iconCls:'fa fa-plus-circle'
    },'-',{
        itemId:'feedbackReply',
        xtype: 'button',
        text: '回复',
        iconCls:'fa fa-envelope-o'
    },'-',{
        itemId:'feedbackDel',
        xtype: 'button',
        text: '删除',
        iconCls:'fa fa-trash-o'
    },'-',{
        itemId:'feedbackLook',
        xtype: 'button',
        text: '查看',
        iconCls:'fa fa-eye'
    }],
    columns: [
        {text: '标题', dataIndex: 'title', flex: 2, menuDisabled: true},
        {text: '投件人', dataIndex: 'askman', flex: 2, menuDisabled: true},
        {text: '提问时间', dataIndex: 'asktime', flex:2, menuDisabled: true},
        {text: '评分', dataIndex: 'appraise', flex: 2, menuDisabled: true},
        {text: '回复状态', dataIndex: 'flag', flex:2, menuDisabled: true}
    ]
});