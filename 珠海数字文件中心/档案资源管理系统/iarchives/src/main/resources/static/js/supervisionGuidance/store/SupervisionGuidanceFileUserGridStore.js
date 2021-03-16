/**
 * Created by Administrator on 2020/9/28.
 */



Ext.define('SupervisionGuidance.store.SupervisionGuidanceFileUserGridStore',{
    extend:'Ext.data.Store',
    model:'SupervisionGuidance.model.SupervisionGuidanceFileUserGridModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/supervisionGuidance/getGuidanceFileUsers',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});

