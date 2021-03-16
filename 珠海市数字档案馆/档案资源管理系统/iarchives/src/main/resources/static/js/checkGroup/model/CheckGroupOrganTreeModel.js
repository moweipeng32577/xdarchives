/**
 * Created by Administrator on 2018/12/3.
 */

Ext.define('CheckGroup.model.CheckGroupOrganTreeModel', {
    extend: 'Ext.data.Model',
    fields: [{name: "id", type: "string"},
        {name: "text", type: "string", mapping: "OrganName"},
        {name: "leaf", type: "boolean"}]
});
