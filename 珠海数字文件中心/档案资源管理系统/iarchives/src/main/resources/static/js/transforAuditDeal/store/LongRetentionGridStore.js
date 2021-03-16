/**
 * Created by RonJiang on 2018/4/21 0021.
 */
Ext.define('TransforAuditDeal.store.LongRetentionGridStore',{
    extend:'Ext.data.Store',
    model:'TransforAuditDeal.model.LongRetentionGridModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/longRetention/captureEntries',
        extraParams: {},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});