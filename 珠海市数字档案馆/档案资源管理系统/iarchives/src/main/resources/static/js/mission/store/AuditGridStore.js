/**
 * Created by Administrator on 2020/5/25.
 */


Ext.define('Mission.store.AuditGridStore',{
    extend:'Ext.data.Store',
    model:'Mission.model.DzJyGridModel',
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
