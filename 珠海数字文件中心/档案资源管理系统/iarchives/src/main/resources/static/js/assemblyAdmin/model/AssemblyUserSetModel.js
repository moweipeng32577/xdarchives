/**
 * Created by Administrator on 2019/7/3.
 */



Ext.define('AssemblyAdmin.model.AssemblyUserSetModel', {
    extend: 'Ext.data.Model',
    xtype:'assemblyUserSetModel',
    fields: [{name: 'id', type: 'string',mapping:'userid'},
        {name: 'realname', type: 'string'}]
});