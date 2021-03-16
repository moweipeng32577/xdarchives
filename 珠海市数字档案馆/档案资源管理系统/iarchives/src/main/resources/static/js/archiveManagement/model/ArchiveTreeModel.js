/**
 * Created by xd on 2017/10/21.
 */
Ext.define('ArchiveManagement.model.ArchiveTreeModel', {
    extend: 'Ext.data.Model',
    fields: [{name: "id", type: "string",mapping: "OrganID"},
        {name: "text", type: "string", mapping: "text"},
        {name: "leaf", type: "boolean"}]
});