/**
 * Created by RonJiang on 2017/10/24 0024.
 */
Ext.define('ClassifySearch.store.ClassifyBookmarksGridStore',{
    extend:'Ext.data.Store',
    model:'ClassifySearch.model.ClassifySearchGridModel',
    pageSize: XD.pageSize,//此处设置每页显示记录条数
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url:'/bookmarks/findBySearch',
        extraParams: {},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});