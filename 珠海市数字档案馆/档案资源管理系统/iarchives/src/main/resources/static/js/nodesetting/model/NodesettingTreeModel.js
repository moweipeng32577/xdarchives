/**
 * Created by tanly on 2017/10/24 0024.
 */
Ext.define('Nodesetting.model.NodesettingTreeModel', {
    extend: 'Ext.data.Model',
    fields: [{name: "id", type: "string"},
        {name: "text", type: "string"},
        {name: "leaf", type: "boolean"}]
});
