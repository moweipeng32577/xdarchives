/**
 * Created by Administrator on 2020/10/15.
 */


Ext.define('YearlyCheckAudit.view.YearlyCheckAuditGridView',{
    extend:'Comps.view.BasicGridView',
    xtype:'yearlyCheckAuditGridView',
    itemId:'yearlyCheckAuditGridViewId',
    hasSearchBar:false,
    tbar:{
        overflowHandler:'scroller',
        items:[{
            text:'审核',
            iconCls:'fa fa-plus-circle',
            itemId:'auditId'
        }]
    },
    store: 'YearlyCheckAuditGridStore',
    columns: [
        {text: '送审信息', dataIndex: 'text', flex: 1, menuDisabled: true},
        {text: '审批类型', dataIndex: 'type', flex: 1, menuDisabled: true}
    ]
});
