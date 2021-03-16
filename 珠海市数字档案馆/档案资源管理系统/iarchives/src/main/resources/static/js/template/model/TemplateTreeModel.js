/**
 * Created by tanly on 2017/11/8 0024.
 */
Ext.define('Template.model.TemplateTreeModel', {
    extend: 'Ext.data.Model',
    fields: [{name: "id", type: "string"},
        {name: "text", type: "string", mapping: "templateName"},
        {name: "leaf", type: "boolean"}]
});
