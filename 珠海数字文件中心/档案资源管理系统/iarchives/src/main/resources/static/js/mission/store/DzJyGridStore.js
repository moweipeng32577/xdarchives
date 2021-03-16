/**
 * Created by Administrator on 2018/10/23.
 */

Ext.define('Mission.store.DzJyGridStore',{
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
