/**
 * Created by xd on 2017/10/21.
 */
Ext.define('Log.store.LogGridStore',{
    extend:'Ext.data.Store',
    model:'Log.model.LogGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/log/findLogDetailBySearch',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
