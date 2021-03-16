/**
 * Created by yl on 2017/10/26.
 */
Ext.define('ExchangeReception.store.ExchangeReceptionGridStore',{
    extend:'Ext.data.Store',
    model:'ExchangeReception.model.ExchangeReceptionGridModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/exchangeReception/getExchange',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});