/**
 * Created by xd on 2017/10/21.
 */
Ext.define('Log.model.LogTreeModel', {
    extend: 'Ext.data.Model',
    fields: [{name: "id", type: "string"},
        {name: "text", type: "string", mapping: "logName"},
        {name: "leaf", type: "boolean"}]
});
