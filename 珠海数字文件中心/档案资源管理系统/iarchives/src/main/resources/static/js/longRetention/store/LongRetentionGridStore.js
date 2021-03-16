/**
 * Created by RonJiang on 2018/4/21 0021.
 */
Ext.define('LongRetention.store.LongRetentionGridStore',{
    extend:'Ext.data.Store',
    model:'LongRetention.model.LongRetentionGridModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/longRetention/entries',
        extraParams: {},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});