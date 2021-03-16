/**
 * Created by Administrator on 2020/10/15.
 */



Ext.define('YearlyCheckAudit.view.YearlyCheckAuditFormGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'yearlyCheckAuditFormGridView',
    itemId: 'yearlyCheckAuditFormGridViewId',
    region: 'north',
    height: '60%',
    store: 'YearlyCheckAuditFormGridStore',
    hasSearchBar: false,
    columns: [
        {text: '年度', dataIndex: 'selectyear', flex: 2, menuDisabled: true},
        {text: '题名', dataIndex: 'title', flex: 4, menuDisabled: true}
    ]
});
