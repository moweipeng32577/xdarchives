/**
 * Created by Administrator on 2020/9/28.
 */


Ext.define('SupervisionGuidance.store.SupervisionGuidanceWorkPlanGridStore',{
    extend:'Ext.data.Store',
    model:'SupervisionGuidance.model.SupervisionGuidanceWorkPlanGridModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/supervisionGuidance/getGuidanceWorkPlans',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
