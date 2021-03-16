/**
 * Created by tanly on 2017/12/4 0004.
 */
Ext.define('Dataopen.store.DataopenNodeuserStore',{
    extend:'Ext.data.Store',
    fields: ['userid', 'realname'],
    // autoLoad: true,
    proxy: {
        type: 'ajax',
        url: '/dataopen/getNodeuser',
        extraParams: {
            nodeId:''
        },
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});