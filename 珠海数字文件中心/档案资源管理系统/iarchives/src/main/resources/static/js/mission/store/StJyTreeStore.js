/**
 * Created by Administrator on 2018/10/23.
 */

Ext.define('Mission.store.StJyTreeStore',{
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

