/**
 * Created by yl on 2017/12/5.
 */
Ext.define('ReportSearch.store.ReportFundsStore',{
    extend:'Ext.data.Store',
    xtype:'reportFundsStore',
    fields: ['fundsid', 'fundsname'],
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url: '/funds/getAllFunds',
        extraParams: {

        },
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});