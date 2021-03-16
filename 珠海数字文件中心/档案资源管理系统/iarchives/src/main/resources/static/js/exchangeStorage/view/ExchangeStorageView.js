/**
 * Created by yl on 2017/11/3.
 */
Ext.define('ExchangeStorage.view.ExchangeStorageView', {
    extend: 'Ext.Panel',
    xtype: 'exchangeStorageView',
    requires: [
        'Ext.layout.container.Border'
    ],
    layout: 'border',
    items: [{xtype:'exchangeStorageGridView'},{xtype:'exchangeStorageShow'}]
});