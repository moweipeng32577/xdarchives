/**
 * Created by RonJiang on 2018/5/9 0009.
 */
Ext.define('AppraisalStandard.store.AppraisalStandardGridStore',{
    extend:'Ext.data.Store',
    model:'AppraisalStandard.model.AppraisalStandardGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/appraisalStandard/getAppraisalStandardBySearch',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});