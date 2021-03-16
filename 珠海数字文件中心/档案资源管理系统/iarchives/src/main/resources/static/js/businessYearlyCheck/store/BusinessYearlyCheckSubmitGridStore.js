/**
 * Created by Administrator on 2020/10/15.
 */


Ext.define('BusinessYearlyCheck.store.BusinessYearlyCheckSubmitGridStore',{
    extend:'Ext.data.Store',
    model:'BusinessYearlyCheck.model.BusinessYearlyCheckSubmitGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/businessYearlyCheck/getYearlyCheckApproveReports',
        extraParams:{},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
