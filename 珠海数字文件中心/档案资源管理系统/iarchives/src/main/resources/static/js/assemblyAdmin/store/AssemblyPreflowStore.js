/**
 * Created by Administrator on 2019/7/3.
 */



Ext.define('AssemblyAdmin.store.AssemblyPreflowStore',{
    extend:'Ext.data.Store',
    xtype:'assemblyPreflowStore',
    fields: ['id', 'nodename'],
    proxy: {
        type: 'ajax',
        url: '/assemblyAdmin/getAssemblyPreflow',
        extraParams: {
            id:'',
            flowid:''
        },
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});