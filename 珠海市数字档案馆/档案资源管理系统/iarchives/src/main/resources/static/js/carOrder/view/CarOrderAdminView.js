/**
 * Created by Administrator on 2020/6/11.
 */



Ext.define('CarOrder.view.CarOrderAdminView', {
    extend: 'Ext.panel.Panel',
    xtype: 'carOrderAdminView',
    layout: 'card',
    activeItem: 0,
    items: [{
        xtype: 'carOrderView'
    }, {
        xtype: 'carOrderAuditFormView'
    }]
});
