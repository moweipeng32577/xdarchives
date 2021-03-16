/**
 * Created by Administrator on 2020/9/28.
 */


Ext.define('SupervisionGuidance.store.SupervisionGuidanceOrganGridStore',{
    extend:'Ext.data.Store',
    model:'SupervisionGuidance.model.SupervisionGuidanceOrganGridModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/supervisionGuidance/getGuidanceOrgans',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
