/**
 * Created by Administrator on 2020/10/15.
 */


Ext.define('YearlyCheckAudit.view.YearlyCheckAuditFormDealView', {
    extend: 'Ext.panel.Panel',
    xtype: 'yearlyCheckAuditFormDealView',
    layout: 'border',
    items: [{xtype:'yearlyCheckAuditFormGridView'},{xtype:'yearlyCheckAuditFormView'}]
});
