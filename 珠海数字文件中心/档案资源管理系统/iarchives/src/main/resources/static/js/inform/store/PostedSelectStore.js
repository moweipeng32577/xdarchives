/**
 * Created by xd on 2017/10/21.
 */
Ext.define('Inform.store.PostedSelectStore',{
    extend:'Ext.data.Store',
    xtype:'postedSelectStore',
    idProperty: 'fnid',
    fields: ['fnid','text'],
    proxy: {
        type: 'ajax',
        url: '/inform/getPosteds',
        extraParams: {flag:''},
        reader: {
            type: 'json'
        }
    },
    autoLoad: true
});
