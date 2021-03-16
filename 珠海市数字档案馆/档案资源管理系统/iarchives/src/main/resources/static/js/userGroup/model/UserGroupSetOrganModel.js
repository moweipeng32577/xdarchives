/**
 * Created by tanly on 2018/4/23 0023.
 */
Ext.define('UserGroup.model.UserGroupSetOrganModel', {
    extend: 'Ext.data.Model',
    xtype:'userGroupSetOrganModel',
    fields: [{name: "id", type: "string",mapping: "fnid"},
        {name: "text", type: "string"}]
});

