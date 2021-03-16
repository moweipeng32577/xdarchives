/**
 * Created by Administrator on 2020/6/11.
 */


Ext.define('PlaceOrder.view.PlaceOrderAdminView', {
    extend: 'Ext.panel.Panel',
    xtype: 'placeOrderAdminView',
    layout: 'card',
    activeItem: 0,
    items: [{
        xtype: 'placeOrderView'
    }, {
        xtype: 'placeOrderAuditFormView'
    }]
});
