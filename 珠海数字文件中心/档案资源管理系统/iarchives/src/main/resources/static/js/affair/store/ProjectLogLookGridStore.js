/**
 * Created by Administrator on 2020/7/21.
 */
Ext.define('Affair.store.ProjectLogLookGridStore',{
    extend:'Ext.data.Store',
    model:'Affair.model.ProjectLogLookGridModel',
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
