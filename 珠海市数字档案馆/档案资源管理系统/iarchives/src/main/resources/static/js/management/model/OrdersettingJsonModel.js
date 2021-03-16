/**
 * Created by tanly on 2017/11/7 0007.
 */
Ext.define('Management.model.OrdersettingJsonModel', {
    extend: 'Ext.data.Model',
    xtype: 'ordersettingJsonModel',
    fields: [{name: "fieldname", type: "string"},
        {name: "fieldcode", type: "string"}]
});
