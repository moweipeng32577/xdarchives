/**
 * Created by RonJiang on 2017/10/26 0026.
 */
Ext.define('ClassifySearch.model.ClassifySearchTreeModel', {
    extend: 'Ext.data.Model',
    fields: [{name: "id", type: "string"},
        {name: "text", type: "string"},
        {name: "leaf", type: "boolean"}]
});