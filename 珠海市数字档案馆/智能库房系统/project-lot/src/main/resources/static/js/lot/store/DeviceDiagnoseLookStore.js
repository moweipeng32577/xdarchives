Ext.define('Lot.store.DeviceDiagnoseLookStore',{
    extend:'Ext.data.Store',
    model:'Lot.model.DeviceDiagnoseLookModel',
    proxy: {
        type: 'ajax',
        url: '/deviceDiagnose/diagnose',
        extraParams:{id:0},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});