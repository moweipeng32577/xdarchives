/**
 *   @author wujy
 */
Ext.define('Lot.store.MJJHTStore',{
    extend:'Ext.data.Store',
    autoLoad:false,
    model:'Lot.model.MJJHTModel',
    proxy: {
        type: 'ajax',
        url: '/device/histories',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});