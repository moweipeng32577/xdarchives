/**
 * Created by Administrator on 2020/8/3.
 */


Ext.define('SimpleSearch.store.MediaGridStore',{
    extend:'Ext.data.Store',
    model:'SimpleSearch.model.MediaGridModel',
    pageSize: XD.pageSize,//此处设置每页显示记录条数
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url:'/simpleSearch/findMediaBySearchPlatform',
        timeout:XD.timeout,
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});