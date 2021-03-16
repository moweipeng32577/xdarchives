/**
 * Created by Administrator on 2020/3/23.
 */


Ext.define('AppraiseManage.view.AppraiseManageGridView',{
    extend: 'Comps.view.BasicGridView',
    xtype:'appraiseManageGridView',
    itemId:'appraiseManageGridViewId',
    hasCloseButton:false,
    bodyBorder: false,
    store: 'AppraiseManageGridStore',
    searchstore:[
        {item: "askman", name: "评价人"},
        {item: "appraise", name: "评分"},
        {item: "appraisetext", name: "评价内容"}
    ],
    tbar: [{
        itemId:'look',
        xtype: 'button',
        text: '查看'
    },'-',{
        itemId:'export',
        xtype: 'button',
        text: '导出'
    },'-',{
        itemId:'census',
        xtype: 'button',
        text: '统计'
    }],
    columns: [
        {text: '评价人', dataIndex: 'askman', flex: 2, menuDisabled: true},
        {text: '评分', dataIndex: 'appraise', flex: 2, menuDisabled: true},
        {text: '评分星数', dataIndex: 'appraisestar', flex:2, menuDisabled: true},
        {text: '评价内容', dataIndex: 'appraisetext', flex:2, menuDisabled: true},
        {text: '评价类型', dataIndex: 'borrowdocid', flex:2, menuDisabled: true}
    ]
});

