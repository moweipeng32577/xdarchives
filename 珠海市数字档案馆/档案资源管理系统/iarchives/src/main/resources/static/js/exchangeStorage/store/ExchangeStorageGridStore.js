/**
 * Created by yl on 2017/10/26.
 */
Ext.define('ExchangeStorage.store.ExchangeStorageGridStore',{
    extend:'Ext.data.Store',
    model:'ExchangeStorage.model.ExchangeStorageGridModel',
    autoLoad: true,
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/exchangeReception/getExchange',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    },

});