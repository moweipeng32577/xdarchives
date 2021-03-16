/**
 * Created by Administrator on 2020/7/21.
 */
Ext.define('ProjectRate.store.ProjectLogLookGridStore',{
    extend:'Ext.data.Store',
    model:'ProjectRate.model.ProjectLogLookGridModel',
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
