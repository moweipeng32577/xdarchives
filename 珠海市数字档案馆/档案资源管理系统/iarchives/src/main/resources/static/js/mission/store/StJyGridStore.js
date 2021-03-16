/**
 * Created by Administrator on 2018/10/23.
 */

Ext.define('Mission.store.StJyGridStore',{
    extend:'Ext.data.Store',
    model:'Mission.model.StJyGridModel',
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
