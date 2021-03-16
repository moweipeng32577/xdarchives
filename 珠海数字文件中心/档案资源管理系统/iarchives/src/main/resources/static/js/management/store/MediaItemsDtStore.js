/**
 * Created by yl on 2017/11/2.
 */
Ext.define('Management.store.MediaItemsDtStore',{
    extend:'Ext.data.Store',
    // autoLoad: true,
    sortOnLoad: true,
    model:'Management.model.MediaDataModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/management/managementMediaEntries',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});