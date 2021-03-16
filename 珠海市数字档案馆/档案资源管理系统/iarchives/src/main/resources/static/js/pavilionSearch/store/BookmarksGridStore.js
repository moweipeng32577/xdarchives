/**
 * Created by RonJiang on 2017/10/24 0024.
 */
Ext.define('PavilionSearch.store.BookmarksGridStore',{
    extend:'Ext.data.Store',
    model:'PavilionSearch.model.PavilionSearchGridModel',
    pageSize: XD.pageSize,//此处设置每页显示记录条数
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url:'/bookmarks/findBySearch',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});