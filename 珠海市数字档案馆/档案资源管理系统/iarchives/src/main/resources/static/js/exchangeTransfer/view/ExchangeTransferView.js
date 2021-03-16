/**
 * Created by yl on 2017/11/2.
 */
Ext.define('ExchangeTransfer.view.ExchangeTransferView', {
    extend: 'Ext.Panel',
    xtype: 'exchangeTransferView',
    requires: [
        'Ext.layout.container.Border'
    ],
    layout: 'border',
    items: [{xtype:'exchangeTransferGridView'}]
});