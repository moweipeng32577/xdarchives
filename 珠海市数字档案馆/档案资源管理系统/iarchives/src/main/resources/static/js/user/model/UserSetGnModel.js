/**
 * Created by Administrator on 2017/10/26 0026.
 */
Ext.define('User.model.UserSetGnModel', {
    extend: 'Ext.data.Model',
    xtype:'userSetGnModel',
    fields: [{name: "id", type: "string",mapping: "fnid"},
        {name: "text", type: "string"}]
});

