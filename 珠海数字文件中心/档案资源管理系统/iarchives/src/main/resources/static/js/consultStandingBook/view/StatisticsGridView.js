/**
 * Created by Leo on 2020/7/3 0003.
 */
Ext.define('ConsultStandingBook.view.StatisticsGridView',{
    extend:'Comps.view.BasicGridView',
    xtype:'statisticsGridView',
    itemId:'statisticsGridViewId',
    hasSearchBar:false,
    tbar:{
        overflowHandler:'scroller',
        items:[{
            fieldLabel: '开始日期',
            emptyText: '请选择开始日期',
            xtype: 'datefield',
            name: 'startdate',
            itemId: 'startdateid',
            format: 'Y-m-d',
            labelStyle: "text-align:right;padding-top: 6px;width:50px",
            maxValue: new Date(),
            margin: '12 0 0 -35',
            listeners: {
                //展开开始日期窗口，关闭结束日期窗口
                expand: function (field) {
                    var endday = this.findParentByType('statisticsGridView').down('[itemId = enddateid]');
                    endday.collapse();
                },
                select: function (datefield, date) {
                    var endday = this.findParentByType('statisticsGridView').down('[itemId = enddateid]');
                    // endday.setMinValue(date);
                    Ext.defer(function () {
                        endday.expand();
                    }, 10);
                }
            }

        },{
            emptyText: '请选择结束日期',
            fieldLabel: '结束日期',
            xtype: 'datefield',
            name: 'enddate',
            itemId: 'enddateid',
            format: 'Y-m-d',
            labelStyle: "text-align:right;padding-top: 6px;",
            margin: '12 0 0 -8',

        },'-',{
            text:'搜索',
            iconCls:'fa fa-eye',
            itemId:'findId'
        },'-',{
            text:'管理',
            iconCls:'fa fa-eye',
            itemId:'managementId'
        },'-',{
            text:'打印',
            iconCls:'fa fa-print',
            itemId:'printId'
        }]
    },
    store: 'ConsultStandingBookStore',
    columns: [
        {text: '统计时间', dataIndex: 'date', width: 100, menuDisabled: true},
        {text: '文书档案', dataIndex: 'wsda', width: 250, menuDisabled: true},
        {text: '婚姻档案', dataIndex: 'hyda', width: 220, menuDisabled: true},
        {text: '退伍档案', dataIndex: 'twda', width: 220, menuDisabled: true},
        {text: '人员/已故人员档案', dataIndex: 'ryda', width: 250, menuDisabled: true},
        {text: '土地档案', dataIndex: 'tdda',width: 220, menuDisabled: true},
        {text: '林政档案', dataIndex: 'lzda',width: 220, menuDisabled: true},
        {text: '合同档案', dataIndex: 'htda', width: 220, menuDisabled: true},
        {text: '科技/城建/基建档案', dataIndex: 'kjcjda', width: 220, menuDisabled: true},
        {text: '业务/工龄档案', dataIndex: 'ywglda', width: 220, menuDisabled: true},
        {text: '其他档案/资料', dataIndex: 'qtdazl', width: 220, menuDisabled: true},
        {text: '电话/现场咨询', dataIndex: 'dhxc', width: 120, menuDisabled: true}
    ]
});