/**
 * Created by Administrator on 2020/9/28.
 */


Ext.define('SupervisionGuidance.store.SupervisionGuidanceWorkFundsGridStore',{
    extend:'Ext.data.Store',
    model:'SupervisionGuidance.model.SupervisionGuidanceWorkFundsGridModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/supervisionGuidance/getGuidanceWorkFundss',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
