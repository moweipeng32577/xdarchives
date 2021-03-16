Ext.define('Lot.store.DeviceDiagnoseStore',{
    extend:'Ext.data.Store',
    model:'Lot.model.DeviceDiagnoseModel',
    autoLoad:true,
    proxy: {
        type: 'ajax',
        url: '/deviceDiagnose/grid',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});