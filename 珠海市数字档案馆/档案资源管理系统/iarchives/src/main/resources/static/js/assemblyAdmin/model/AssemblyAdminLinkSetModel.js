/**
 * Created by Administrator on 2019/7/3.
 */


Ext.define('AssemblyAdmin.model.AssemblyAdminLinkSetModel', {
    extend: 'Ext.data.Model',
    xtype:'assemblyAdminLinkSetModel',
    fields: [{name: 'id', type: 'string'},
        {name: 'modelname', type: 'string'}]
});
