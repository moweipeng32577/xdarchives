/**
 * Created by Administrator on 2020/12/28.
 */
Ext.define('Affair.store.AffairNodeStore',{
    extend:'Ext.data.Store',
    fields: ['id', 'text'],
    proxy: {
        type: 'ajax',
        url: '/dataopen/getNode',
        extraParams: {
            workname:'项目管理审批'
        },
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});