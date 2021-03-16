/**
 * Created by RonJiang on 2018/03/08
 */
Ext.define('Acquisition.store.ReportGridStore',{
    extend:'Ext.data.Store',
    model:'Acquisition.model.ReportGridModel',
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
