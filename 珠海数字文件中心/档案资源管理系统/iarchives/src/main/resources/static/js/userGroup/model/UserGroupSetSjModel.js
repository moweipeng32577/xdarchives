/**
 * Created by Administrator on 2017/10/26 0026.
 */
Ext.define('UserGroup.model.UserGroupSetSjModel', {
    extend: 'Ext.data.Model',
    xtype:'userGroupSetSjModel',
    fields: [{name: "id", type: "string",mapping: "fnid"},
        {name: "text", type: "string"}]
});

