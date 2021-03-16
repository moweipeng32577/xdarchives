/**
 * Created by Administrator on 2019/7/3.
 */


Ext.define('AssemblyAdmin.model.AssemblyUserSetOrganTreeModel', {
    extend: 'Ext.data.Model',
    fields: [{name: "id", type: "string"},
        {name: "text", type: "string", mapping: "OrganName"},
        {name: "leaf", type: "boolean"}]
});
