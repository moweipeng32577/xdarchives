/**
 * Created by tanly on 2017/10/24 0024.
 */
Ext.define('Codesetting.model.CodesettingTreeModel', {
    extend: 'Ext.data.Model',
    fields: [{name: "id", type: "string"},
        {name: "text", type: "string", mapping: "codesettingName"},
        {name: "leaf", type: "boolean"}]
});
