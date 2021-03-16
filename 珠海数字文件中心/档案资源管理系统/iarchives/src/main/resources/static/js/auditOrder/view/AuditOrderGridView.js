/**
 * Created by Administrator on 2020/6/13.
 */

Ext.define('AuditOrder.view.AuditOrderGridView',{
    extend:'Comps.view.BasicGridView',
    xtype:'auditOrderGridView',
    itemId:'auditOrderGridViewID',
    hasSearchBar:false,
    tbar:{
        overflowHandler:'scroller',
        items:[{
            text:'审核',
            iconCls:'fa fa-plus-circle',
            itemId:'orderAudit'
        },'-',{
            text:'返回',
            itemId:'backId'
        }]
    },
    store: 'AuditOrderGridStore',
    columns: [
        {text: '送审信息', dataIndex: 'text', flex: 1, menuDisabled: true},
        {text: '审批类型', dataIndex: 'type', flex: 1, menuDisabled: true}
    ]
});
