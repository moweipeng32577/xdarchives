/**
 * Created by yl on 2017/10/26.
 */
Ext.define('ClassifySearch.store.ClassifyJypurposeStore',{
    extend:'Ext.data.Store',
    fields: ['configid', 'value'],
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url: '/electron/getJypurpose',
        extraParams: {
        },
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});