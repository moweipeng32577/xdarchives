/**
 * Created by yl on 2017/11/7.
 */
Ext.define('ExchangeStorage.store.ExchangeStorageDetailGridStore',{
    extend:'Ext.data.Store',
    model:'ExchangeStorage.model.ExchangeStorageDetailGridModel',
    autoLoad: true,
    pageSize: 50,
    proxy: {
        type: 'ajax',
        url: '/exchangeStorage/getExchangeStorage',
        extraParams: {
        },
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});