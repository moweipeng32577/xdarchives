/**
 * Created by Administrator on 2017/10/26 0026.
 */
Ext.define('UserGroup.model.UserGroupSetGnModel', {
    extend: 'Ext.data.Model',
    xtype:'userGroupSetGnModel',
    fields: [{name: "id", type: "string",mapping: "fnid"},
        {name: "text", type: "string"}]
});

