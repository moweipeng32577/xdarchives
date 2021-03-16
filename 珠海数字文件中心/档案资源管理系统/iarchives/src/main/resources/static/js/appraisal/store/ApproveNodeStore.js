/**
 * Created by Administrator on 2020/1/14.
 */



Ext.define('Appraisal.store.ApproveNodeStore',{
    extend:'Ext.data.Store',
    fields: ['id', 'text'],
    proxy: {
        type: 'ajax',
        url: '/dataopen/getNode',
        extraParams: {
            workname:'销毁审批'
        },
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
