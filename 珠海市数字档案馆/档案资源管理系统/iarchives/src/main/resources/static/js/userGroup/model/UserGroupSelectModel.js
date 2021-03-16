/**
 * Created by Administrator on 2019/12/14.
 */

Ext.define('UserGroup.model.UserGroupSelectModel', {
    extend: 'Ext.data.Model',
    fields: [{name: 'id', type: 'string',mapping:'userid'},
        {name: 'realname', type: 'string'}]
});

