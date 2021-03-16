/**
 * Created by yl on 2017/11/2.
 */
Ext.define('ExchangeReception.view.ExchangeReceptionView', {
    extend: 'Ext.Panel',
    xtype: 'exchangeReceptionView',
    requires: [
        'Ext.layout.container.Border'
    ],
    layout: 'border',
    items: [{itemId:'exchangeReceptionGridViewID',xtype:'exchangeReceptionGridView'}]
});