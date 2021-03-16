/**
 * Created by Administrator on 2019/7/3.
 */


Ext.define('AssemblyAdmin.store.AssemblyAdminLinkSetStore',{
    extend:'Ext.data.Store',
    xtype:'assemblyAdminLinkSetStore',
    model:'AssemblyAdmin.model.AssemblyAdminLinkSetModel',
    idProperty: 'id',
    fields: ['id','modelname'],
    proxy: {
        type: 'ajax',
        url: '/assemblyAdmin/getLinkAll',
        reader: {
            type: 'json'
        }
    }
});
