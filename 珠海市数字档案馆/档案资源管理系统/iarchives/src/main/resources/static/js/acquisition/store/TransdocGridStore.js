/**
 * Created by RonJiang on 2018/4/18 0018.
 */
Ext.define('Acquisition.store.TransdocGridStore',{
    extend:'Ext.data.Store',
    model:'Acquisition.model.TransdocGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/acquisition/getNodeTransdoc',
        extraParams: {},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});