/**
 * Created by Administrator on 2019/12/14.
 */


Ext.define('UserGroup.model.OrganTreeModel', {
    extend: 'Ext.data.Model',
    fields: [{name: "id", type: "string"},
        {name: "text", type: "string", mapping: "OrganName"},
        {name: "leaf", type: "boolean"}]
});

