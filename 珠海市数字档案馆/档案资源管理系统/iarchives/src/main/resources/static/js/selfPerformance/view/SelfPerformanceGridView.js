/**
 * Created by Administrator on 2020/4/13.
 */


Ext.define('SelfPerformance.view.SelfPerformanceGridView',{
    extend: 'Comps.view.BasicGridView',
    xtype:'selfPerformanceGridView',
    itemId:'selfPerformanceGridViewId',
    hasSearchBar:false,//无需基础表格组件中的检索栏
    tbar: [
    ],
    store: 'SelfPerformanceGridStore',
    columns: [
        {text: '统计名称', dataIndex: 'title', flex: 3},
        {text: '提交数', dataIndex: 'submitcount', flex: 1, menuDisabled: true},
        {text: '成功数', dataIndex: 'successcount', flex: 1, menuDisabled: true},
        {text: '失败数', dataIndex: 'failcount', flex: 1, menuDisabled: true}
    ]
});
