/**
 * Created by Leo on 2019/5/7 0007.
 */
Ext.define('AppraisalStandard.store.AutoRetentionGridStore',{
    extend:'Ext.data.Store',
    model:'AppraisalStandard.model.AutoRetentionGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/appraisalStandard/getAutoRetentionWordsBySearch',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});