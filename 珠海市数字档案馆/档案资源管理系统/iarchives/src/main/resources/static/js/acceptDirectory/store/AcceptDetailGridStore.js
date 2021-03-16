/**
 * Created by Administrator on 2019/6/25.
 */


Ext.define('AcceptDirectory.store.AcceptDetailGridStore',{
    extend:'Ext.data.Store',
    model:'AcceptDirectory.model.AcceptDetailGridModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/acceptDirectory/getimpRecord',
        extraParams: {
            imptype:""
        },
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
