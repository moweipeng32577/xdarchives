/**
 * Created by Administrator on 2019/7/3.
 */


Ext.define('AssemblyAdmin.model.AssemblyAdminUserGridModel', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string',mapping:'userid'},
        {name: 'loginname', type: 'string'},
        {name: 'realname', type: 'string'},
        {name: 'sex', type: 'string'}
    ]
});
