/**
 * Created by Administrator on 2019/10/25.
 */

Ext.define('Acquisition.store.ApproveManStore',{
    extend:'Ext.data.Store',
    fields: ['userid', 'realname'],
    proxy: {
        type: 'ajax',
        url: '/electronApprove/getNextSpman',
        extraParams: {
            workText:'采集移交审核'
        },
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
