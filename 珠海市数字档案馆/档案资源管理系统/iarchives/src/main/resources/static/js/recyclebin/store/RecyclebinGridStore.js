/**
 * Created by RonJiang on 2018/04/23
 */
Ext.define('Recyclebin.store.RecyclebinGridStore',{
    extend:'Ext.data.Store',
    model:'Recyclebin.model.RecyclebinGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/recyclebin/getRecyclebin',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});