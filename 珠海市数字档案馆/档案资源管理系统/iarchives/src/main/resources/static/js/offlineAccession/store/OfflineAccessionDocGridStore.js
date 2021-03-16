/**
 * Created by RonJiang on 2018/4/18 0018.
 */
Ext.define('OfflineAccession.store.OfflineAccessionDocGridStore',{
    extend:'Ext.data.Store',
    model:'OfflineAccession.model.OfflineAccessionDocGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/offlineAccession/getBatchdoc',
        extraParams: {},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});