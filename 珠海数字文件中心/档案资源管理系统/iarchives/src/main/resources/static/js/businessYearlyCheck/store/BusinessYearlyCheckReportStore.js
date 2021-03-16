/**
 * Created by Administrator on 2020/10/13.
 */



Ext.define('BusinessYearlyCheck.store.BusinessYearlyCheckReportStore',{
    extend:'Ext.data.Store',
    model:'BusinessYearlyCheck.model.BusinessYearlyCheckReportModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/businessYearlyCheck/getYearlyCheckReportsByState',
        extraParams:{
        },
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});