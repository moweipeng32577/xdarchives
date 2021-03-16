/**
 * Created by yl on 2017/10/26.
 */
Ext.define('ExchangeTransfer.store.ExchangeTransferGridStore',{
    extend:'Ext.data.Store',
    model:'ExchangeTransfer.model.ExchangeTransferGridModel',
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
    }
});