/**
 * Created by Administrator on 2017/10/26 0026.
 */
Ext.define('User.model.UserSetSjModel', {
    extend: 'Ext.data.Model',
    xtype:'userSetSjModel',
    fields: [{name: "id", type: "string",mapping: "fnid"},
        {name: "text", type: "string"}]
});

