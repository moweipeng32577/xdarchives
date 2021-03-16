/**
 * Created by RonJiang on 2017/11/30 0030.
 */
Ext.define('Management.store.ManagementFilingStore',{
    extend:'Ext.data.Store',
    model:'Management.model.ManagementFilingModel',
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