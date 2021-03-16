Ext.define('CompilationAcquisition.store.ManagementSequenceStore',{
    extend:'Ext.data.Store',
    model:'CompilationAcquisition.model.ManagementSequenceModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/management/sqEntryIndexes',
        extraParams: {},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    },
    hasCloseButton:false
});