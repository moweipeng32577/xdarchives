/**
 * Created by tanly on 2018/9/17 0024.
 */
Ext.define('User.model.UserOrganTreeModel', {
    extend: 'Ext.data.Model',
    fields: [{name: "id", type: "string"},
        {name: "text", type: "string", mapping: "OrganName"},
        {name: "leaf", type: "boolean"}]
});
