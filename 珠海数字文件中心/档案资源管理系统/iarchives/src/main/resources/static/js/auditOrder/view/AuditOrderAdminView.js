/**
 * Created by Administrator on 2020/6/16.
 */


Ext.define('AuditOrder.view.AuditOrderAdminView', {
    extend: 'Ext.panel.Panel',
    xtype: 'auditOrderAdminView',
    layout: 'card',
    activeItem: 0,
    items: [{
        xtype: 'auditOrderGridView'
    }, {
        xtype: 'informOrderGridView'
    }, {
        xtype: 'carOrderAuditFormView'
    }, {
        xtype: 'placeOrderAuditFormView'
    }]
});
