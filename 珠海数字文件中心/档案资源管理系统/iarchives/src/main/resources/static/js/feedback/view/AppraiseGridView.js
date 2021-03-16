/**
 * Created by Administrator on 2020/3/18.
 */


Ext.define('Feedback.view.AppraiseGridView',{
    extend: 'Comps.view.BasicGridView',
    xtype:'appraiseGridView',
    itemId:'appraiseGridViewId',
    hasCloseButton:false,
    bodyBorder: false,
    store: 'AppraiseGridStore',
    searchstore:[
        {item: "askman", name: "评价人"},
        {item: "appraise", name: "评分"},
        {item: "appraisetext", name: "评价内容"},
        {item: "flag", name: "回复状态"},
        {item: "replycontent", name: "回复内容"}
    ],
    columns: [
        {text: '评价人', dataIndex: 'askman', flex: 2, menuDisabled: true},
        {text: '评分', dataIndex: 'appraise', flex: 2, menuDisabled: true},
        {text: '评价内容', dataIndex: 'appraisetext', flex:2, menuDisabled: true},
        {text: '评价类型', dataIndex: 'borrowdocid', flex:2, menuDisabled: true},
        {text: '回复状态', dataIndex: 'flag', flex:2, menuDisabled: true},
        {text: '回复内容', dataIndex: 'replycontent', flex:2, menuDisabled: true}
    ]
});
