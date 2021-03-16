Ext.define('ArchivesCallout.store.AssemblyStore',{
    extend:'Ext.data.Store',
    xtype:'AssemblyStore',
    fields: ['id', 'title'],
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url: '/digitalInspection/getAssemblys',
        extraParams: {},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});