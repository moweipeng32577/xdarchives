/**
 * Created by Administrator on 2020/10/15.
 */



Ext.define('YearlyCheckAudit.view.YearlyCheckAuditView', {
    extend: 'Ext.panel.Panel',
    xtype: 'yearlyCheckAuditView',
    layout: 'card',
    activeItem: 0,
    items: [{
        xtype: 'yearlyCheckAuditGridView'
    }, {
        xtype: 'yearlyCheckAuditFormDealView'
    }]
});

