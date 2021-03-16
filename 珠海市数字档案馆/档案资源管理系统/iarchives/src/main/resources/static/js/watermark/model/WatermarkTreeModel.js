/**
 * Created by tanly on 2017/10/24 0024.
 */
Ext.define('Watermark.model.WatermarkTreeModel', {
    extend: 'Ext.data.Model',
    fields: [{name: "id", type: "string"},
        {name: "text", type: "string", mapping: "OrganName"},
        {name: "leaf", type: "boolean"}]
});
