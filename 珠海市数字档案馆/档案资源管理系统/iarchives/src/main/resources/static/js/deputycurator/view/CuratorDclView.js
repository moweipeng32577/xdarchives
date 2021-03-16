/**
 * Created by Administrator on 2020/7/20.
 */
Ext.define('Deputycurator.view.CuratorDclView',{
    extend:'Comps.view.BasicGridView',
    xtype:'curatorDclView',
    searchstore:[
        {item: "title", name: "标题"},
        {item: "leaderrespon", name: "责任领导"}
    ],
    tbar: [{
        itemId:'review',
        xtype: 'button',
        text: '审阅'
    },'-',{
        itemId:'look',
        xtype: 'button',
        iconCls:'fa fa-eye',
        text: '查看'
    }],
    store: 'CuratorDclStore',
    columns: [
        {text: '标题', dataIndex: 'title', flex: 2, menuDisabled: true},
        {text: '工作项目', dataIndex: 'workproject', flex: 2, menuDisabled: true},
        {text: '工作内容', dataIndex: 'workcontent', flex: 2, menuDisabled: true},
        {text: '责任领导', dataIndex: 'leaderrespon', flex: 2, menuDisabled: true},
        {text: '承办科室', dataIndex: 'undertakedepart', flex: 2, menuDisabled: true},
        {text: '承办人', dataIndex: 'undertaker', flex: 2, menuDisabled: true},
        {text: '配合科室', dataIndex: 'cooperatedepart', flex: 2, menuDisabled: true},
        {text: '完成时间', dataIndex: 'finishtime', flex: 2, menuDisabled: true},
        {text: '督导意见', dataIndex: 'opinion', flex: 2, menuDisabled: true},
        {text: '提交时间', dataIndex: 'fgzsytime', flex: 2, menuDisabled: true},
        {text: '状态', dataIndex: 'projectstatus', flex: 2, menuDisabled: true}
    ]
});
