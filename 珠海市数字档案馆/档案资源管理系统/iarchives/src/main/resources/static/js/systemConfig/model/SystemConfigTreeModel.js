/**
 * Created by tanly on 2017/11/1 0024.
 */
Ext.define('SystemConfig.model.SystemConfigTreeModel', {
    extend: 'Ext.data.Model',
    fields: [{name: "id", type: "string"},
        {name: "text", type: "string"},
        {name: "leaf", type: "boolean"}]
});
