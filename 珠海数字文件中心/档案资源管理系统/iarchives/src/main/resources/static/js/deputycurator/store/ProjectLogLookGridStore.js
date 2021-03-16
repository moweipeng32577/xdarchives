/**
 * Created by Administrator on 2020/7/21.
 */
Ext.define('Deputycurator.store.ProjectLogLookGridStore',{
    extend:'Ext.data.Store',
    model:'Deputycurator.model.ProjectLogLookGridModel',
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
