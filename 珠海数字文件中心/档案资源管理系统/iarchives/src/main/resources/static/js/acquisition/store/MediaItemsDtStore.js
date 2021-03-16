/**
 * Created by yl on 2017/11/2.
 */
Ext.define('Acquisition.store.MediaItemsDtStore',{
    extend:'Ext.data.Store',
    // autoLoad: true,
    sortOnLoad: true,
    model:'Acquisition.model.MediaDataModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/acquisition/captureMediaEntries',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});