/**
 * Created by Administrator on 2019/7/3.
 */


Ext.define('AssemblyAdmin.store.AssemblyUserSetStore',{
    extend:'Ext.data.Store',
    xtype:'assemblyUserSetStore',
    model:'AssemblyAdmin.model.AssemblyUserSetModel',
    idProperty: 'userid',
    fields: ['userid','realname'],
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url: '/workflow/getWorkUser',
        reader: {
            type: 'json'
        }
    }
});
