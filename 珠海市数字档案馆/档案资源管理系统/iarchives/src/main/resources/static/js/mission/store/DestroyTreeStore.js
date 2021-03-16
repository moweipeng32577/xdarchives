/**
 * Created by tanly on 2017/12/29 0029.
 */
Ext.define('Mission.store.DestroyTreeStore',{
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