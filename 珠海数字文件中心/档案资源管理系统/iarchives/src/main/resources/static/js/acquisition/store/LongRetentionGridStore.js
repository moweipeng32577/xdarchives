/**
 * Created by RonJiang on 2018/4/21 0021.
 */
Ext.define('Acquisition.store.LongRetentionGridStore',{
    extend:'Ext.data.Store',
    model:'Acquisition.model.LongRetentionGridModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/longRetention/captureEntries',
        timeout:XD.timeout,
        extraParams: {},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});