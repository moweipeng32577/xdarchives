/**
 * Created by RonJiang on 2017/11/30 0030.
 */
Ext.define('CompilationAcquisition.store.ManagementFilingStore',{
    extend:'Ext.data.Store',
    model:'CompilationAcquisition.model.ManagementFilingModel',
    proxy: {
        type: 'ajax',
        scope:this,
        url:'/management/entryIndexes/',
        extraParams: {
            entryids:this.entryids
        },
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});