/**
 * Created by Administrator on 2020/1/14.
 */



Ext.define('Acquisition.store.ApproveNodeStore',{
    extend:'Ext.data.Store',
    fields: ['id', 'text'],
    proxy: {
        type: 'ajax',
        url: '/electron/getWorkTextNode',
        extraParams: {
            workText:''
        },
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
