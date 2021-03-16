/**
 * Created by yl on 2017/12/6.
 */
Ext.define('Mission.store.DestroyGridStore',{
    extend:'Ext.data.Store',
    model:'Mission.model.DestroyGridModel',
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