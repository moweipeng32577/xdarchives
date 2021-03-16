/**
 * Created by RonJiang on 2018/2/27
 */
Ext.define('Report.store.ReportGridStore',{
    extend:'Ext.data.Store',
    model:'Report.model.ReportGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/report/getNodeReport',
        extraParams: {},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
