/**
 * Created by tanly on 2017/11/4 0004.
 */
Ext.define('Management.view.OrdersettingItemSelectedFormView', {
    extend: 'Ext.form.Panel',
    itemId: 'selectorID',
    xtype: 'ordersettingItemSelectedFormView',
    layout: 'fit',
    items: [{
        itemId: 'itemselectorID',
        xtype: 'itemselector',
        imagePath: '../ux/images/',
        store: 'OrdersettingSelectStore',
        displayField: 'fieldname',
        valueField: 'fieldname',
        allowBlank: false,
        msgTarget: 'side',
        fromTitle: '可选字段(按Ctrl+F查找)',
        toTitle: '已选字段'
    }]
});