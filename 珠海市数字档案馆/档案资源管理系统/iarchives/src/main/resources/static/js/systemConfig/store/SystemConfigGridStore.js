/**
 * Created by tanly on 2017/11/1 0024.
 */
Ext.define('SystemConfig.store.SystemConfigGridStore',{
    extend:'Ext.data.Store',
    model:'SystemConfig.model.SystemConfigGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/systemconfig/systemconfigs',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});