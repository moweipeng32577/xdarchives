/**
 * Created by Administrator on 2020/7/21.
 */
Ext.define('ProjectAdd.store.ProjectLogLookGridStore',{
    extend:'Ext.data.Store',
    model:'ProjectAdd.model.ProjectLogLookGridModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/projectRate/getProjectLogs',
        extraParams: {},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
