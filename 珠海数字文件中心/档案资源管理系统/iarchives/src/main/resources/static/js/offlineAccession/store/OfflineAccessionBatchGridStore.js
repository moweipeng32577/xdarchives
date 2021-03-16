/**
 * Created by yl on 2019/1/10.
 */
Ext.define('OfflineAccession.store.OfflineAccessionBatchGridStore',{
    extend:'Ext.data.Store',
    model:'OfflineAccession.model.OfflineAccessionBatchGridModel',
    autoLoad: true,
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/offlineAccession/getBatch',
        extraParams: {},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
