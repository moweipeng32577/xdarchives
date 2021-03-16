/**
 * Created by Administrator on 2019/7/3.
 */


Ext.define('AssemblyAdmin.store.AssemblyFlowStore', {
    extend: 'Ext.data.Store',
    xtype: 'assemblyFlowStore',
    fields: ['id', 'model'],
    proxy: {
        type: 'ajax',
        url: '/assemblyAdmin/getAssemblyflows',
        extraParams: {
            id: '',
            type: null
        },
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
