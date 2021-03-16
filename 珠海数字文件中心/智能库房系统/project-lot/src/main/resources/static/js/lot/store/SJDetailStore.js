/**
 *   @author wujy
 */
Ext.define('Lot.store.SJDetailStore',{
    extend:'Ext.data.Store',
    model:'Lot.model.SJDetailModel',
    proxy: {
        type: 'ajax',
        url: '/water/deviceWarning',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});