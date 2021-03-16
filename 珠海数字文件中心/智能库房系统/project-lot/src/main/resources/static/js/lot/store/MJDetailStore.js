/**
 *   @author wujy
 */
Ext.define('Lot.store.MJDetailStore',{
    extend:'Ext.data.Store',
    model:'Lot.model.MJDetailModel',
    proxy: {
        type: 'ajax',
        url: '',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});