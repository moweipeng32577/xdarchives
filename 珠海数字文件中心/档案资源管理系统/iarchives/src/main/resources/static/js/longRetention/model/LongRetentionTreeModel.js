/**
 * Created by yl on 2017/11/13.
 */
Ext.define('LongRetention.model.LongRetentionTreeModel', {
    extend: 'Ext.data.Model',
    fields: [{name: "id", type: "string"},
        {name: "text", type: "string"},
        {name: "leaf", type: "boolean"}]
});