Ext.define('CompilationAcquisition.store.ManagementClassificationStore',{
    extend:'Ext.data.Store',
    model:'CompilationAcquisition.model.ManagementClassificationModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/management/entries',
        extraParams: {},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});