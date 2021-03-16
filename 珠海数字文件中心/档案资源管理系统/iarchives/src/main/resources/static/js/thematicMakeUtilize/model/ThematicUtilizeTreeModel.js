/**
 * Created by Administrator on 2019/5/22.
 */
Ext.define('ThematicUtilize.model.ThematicUtilizeTreeModel', {
    extend: 'Ext.data.Model',
    fields: [{name: "id", type: "string",mapping: "fnid"},
        {name: "text", type: "string", mapping: "text"},
        {name: "leaf", type: "boolean"}]
});