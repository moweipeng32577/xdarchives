/**
 * Created by tanly on 2017/12/1 0001.
 */
Ext.define('Dataopen.model.DataopenTreeModel', {
    extend: 'Ext.data.Model',
    fields: [{name: "id", type: "string"},
        {name: "text", type: "string"},
        {name: "leaf", type: "boolean"}]
});