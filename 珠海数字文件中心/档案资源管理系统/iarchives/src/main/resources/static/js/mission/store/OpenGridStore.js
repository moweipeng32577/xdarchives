/**
 * Created by tanly on 2017/12/7 0007.
 */
Ext.define('Mission.store.OpenGridStore',{
    extend:'Ext.data.Store',
    model:'Mission.model.OpenGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/mission/getTask',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});