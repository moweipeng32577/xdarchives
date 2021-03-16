/**
 * Created by tanly on 2017/12/4 0004.
 */
Ext.define('Dataopen.store.DataopenNodeStore',{
    extend:'Ext.data.Store',
    fields: ['id', 'text'],
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url: '/dataopen/getNode',
        extraParams: {
            workname:'开放审批'
        },
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});