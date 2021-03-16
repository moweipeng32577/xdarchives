/**
 * Created by xd on 2017/10/21.
 */
Ext.define('Inform.model.PostedSelectModel', {
    extend: 'Ext.data.Model',
    xtype:'postedSelectModel',
    fields: [{name: "fnid", type: "string"},
        {name: "text", type: "string"}]
});
