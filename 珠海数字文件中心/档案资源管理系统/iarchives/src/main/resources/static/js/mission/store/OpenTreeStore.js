/**
 * Created by tanly on 2017/12/7 0007.
 */
Ext.define('Mission.store.OpenTreeStore',{
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