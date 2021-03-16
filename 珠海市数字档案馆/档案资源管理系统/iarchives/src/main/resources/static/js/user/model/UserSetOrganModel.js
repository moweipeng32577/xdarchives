/**
 * Created by tanly on 2018/04/21 0026.
 */
Ext.define('User.model.UserSetOrganModel', {
    extend: 'Ext.data.Model',
    xtype:'userSetOrganModel',
    fields: [{name: "id", type: "string",mapping: "fnid"},
        {name: "text", type: "string"}]
});

