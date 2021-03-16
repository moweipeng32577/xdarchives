/**
 * Created by Administrator on 2020/7/27.
 */


Ext.define('User.model.OrganTreeModel', {
    extend: 'Ext.data.Model',
    fields: [{name: "id", type: "string"},
        {name: "text", type: "string", mapping: "OrganName"},
        {name: "leaf", type: "boolean"}]
});

