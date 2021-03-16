/**
 * Created by Administrator on 2018/12/3.
 */

Ext.define('CheckGroup.model.CheckGroupUserSelectModel', {
    extend: 'Ext.data.Model',
    xtype:'checkGroupUserSelectModel',
    fields: [{name: 'id', type: 'string',mapping:'userid'},
        {name: 'realname', type: 'string'}]
});
