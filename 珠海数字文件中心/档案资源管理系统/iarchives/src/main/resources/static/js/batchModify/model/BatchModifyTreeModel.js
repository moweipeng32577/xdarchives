/**
 * Created by RonJiang on 2018/01/24
 */
Ext.define('BatchModify.model.BatchModifyTreeModel', {
    extend: 'Ext.data.Model',
    fields: [{name: "id", type: "string"},
        {name: "text", type: "string"},
        {name: "leaf", type: "boolean"}]
});