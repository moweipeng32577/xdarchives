/**
 * Created by tanly on 2017/11/1 0024.
 */
Ext.define('Classificationsetting.model.ClassificationsettingTreeModel', {
    extend: 'Ext.data.Model',
    fields: [{name: "id", type: "string"},
        {name: "text", type: "string", mapping: "classificationsettingName"},
        {name: "leaf", type: "boolean"}]
});
