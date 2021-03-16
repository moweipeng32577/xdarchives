/**
 * Created by RonJiang on 2018/5/9 0009.
 */
Ext.define('AppraisalStandard.model.AppraisalStandardTreeModel', {
    extend: 'Ext.data.Model',
    fields: [{name: "id", type: "string"},
        {name: "text", type: "string"},
        {name: "leaf", type: "boolean"}]
});