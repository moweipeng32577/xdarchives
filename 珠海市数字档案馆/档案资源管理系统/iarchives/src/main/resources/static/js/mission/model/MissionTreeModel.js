/**
 * Created by RonJiang on 2018/1/10 0010.
 */
Ext.define('Mission.model.MissionTreeModel', {
    extend: 'Ext.data.Model',
    xtype:'missionTreeModel',
    fields: [{name: "id", type: "string"},
        {name: "text", type: "string"}]
});