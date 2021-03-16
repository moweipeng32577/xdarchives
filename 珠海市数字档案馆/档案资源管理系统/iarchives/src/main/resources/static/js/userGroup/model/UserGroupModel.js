/**
 * Created by xd on 2017/10/21.
 */
Ext.define('UserGroup.model.UserGroupModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string',mapping:'roleid'},
        {name: 'rolename', type: 'string'},
        {name: 'desciption', type: 'string'}
    ]
});