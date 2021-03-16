/**
 * Created by Administrator on 2020/5/9.
 */


Ext.define('ProjectRate.view.ProjectRateGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'projectRateGridView',
    store: {
        extend: 'Ext.data.Store',
        model:'ProjectRate.model.ProjectLogLookGridModel',
        pageSize: XD.pageSize,
        autoLoad: true,
        proxy: {
            type: 'ajax',
            url: '/projectRate/getProjectByCurator',
            extraParams: {projectstatus:'领导审阅通过发布'},
            reader: {
                type: 'json',
                rootProperty: 'content',
                totalProperty: 'totalElements'
            }
        }
    },
    searchstore:[
        {item: "title", name: "标题"},
        {item: "leaderrespon", name: "责任领导"}
    ],
    tbar: [{
        text:'查看',
        iconCls:'fa fa-eye',
        itemId:'look'
    },'-',{
        text:'打印',
        iconCls:'fa fa-print',
        itemId:'print'
    }],
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
        {text: '提交时间', dataIndex: 'gzsytime', flex: 2, menuDisabled: true},
        {text: '状态', dataIndex: 'projectstatus', flex: 2, menuDisabled: true}
    ]
});
