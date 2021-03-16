/**
 * Created by yl on 2017/10/26.
 */
Ext.define('SimpleSearch.store.JypurposeStore',{
    extend:'Ext.data.Store',
    fields: ['configid', 'value'],
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url: '/electron/getJypurpose',
        extraParams: {
            type:'0'
        },
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});