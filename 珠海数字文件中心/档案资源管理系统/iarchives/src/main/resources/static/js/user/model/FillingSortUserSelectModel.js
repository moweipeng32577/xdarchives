/**
 * Created by Administrator on 2020/7/27.
 */



Ext.define('User.model.FillingSortUserSelectModel', {
    extend: 'Ext.data.Model',
    xtype:'fillingSortUserSelectModel',
    fields: [{name: 'id', type: 'string',mapping:'userid'},
        {name: 'realname', type: 'string'}]
});
