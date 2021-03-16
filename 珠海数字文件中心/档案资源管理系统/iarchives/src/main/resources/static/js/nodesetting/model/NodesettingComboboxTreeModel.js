/**
 * Created by tanly on 2017/10/27 0027.
 */
Ext.define('Nodesetting.model.NodesettingComboboxTreeModel', {
    extend: 'Ext.data.Model',
    fields: [{name: "id", type: "string"},
        {name: "text", type: "string", mapping: "nodesettingName"},
        {name: "leaf", type: "boolean"}]
});
