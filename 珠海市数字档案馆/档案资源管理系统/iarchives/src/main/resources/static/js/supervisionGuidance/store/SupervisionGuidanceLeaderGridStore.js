/**
 * Created by Administrator on 2020/7/9.
 */


Ext.define('SupervisionGuidance.store.SupervisionGuidanceLeaderGridStore',{
    extend:'Ext.data.Store',
    model:'SupervisionGuidance.model.SupervisionGuidanceLeaderGridModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/supervisionGuidance/getGuidanceLeaders',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
