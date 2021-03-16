/**
 * Created by RonJiang on 2018/2/27
 */
Ext.define('Report.model.ReportTreeModel', {
    extend: 'Ext.data.Model',
    fields: [{name: "id", type: "string"},
        {name: "text", type: "string"},
        {name: "leaf", type: "boolean"}]
});
