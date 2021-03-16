/**
 * Created by Administrator on 2020/5/25.
 */


Ext.define('Mission.store.AuditTreeStore',{
    extend:'Ext.data.TreeStore',
    model:'Mission.model.MissionTreeModel',
    autoLoad:true,
    proxy: {
        type: 'ajax',
        url: '/mission/getSpState',
        reader: {
            type: 'json',
            expanded: true
        }
    },
    root: {
        text: '审批状态',
        expanded: true
    }
});
