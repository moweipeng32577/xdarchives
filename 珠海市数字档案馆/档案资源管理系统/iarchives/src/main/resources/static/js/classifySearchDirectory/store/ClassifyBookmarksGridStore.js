/**
 * Created by Administrator on 2019/6/27.
 */


Ext.define('ClassifySearchDirectory.store.ClassifyBookmarksGridStore',{
    extend:'Ext.data.Store',
    model:'ClassifySearchDirectory.model.ClassifySearchDirectoryGridModel',
    pageSize: XD.pageSize,//此处设置每页显示记录条数
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url:'/bookmarks/findBySearchDirectory',
        extraParams: {},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
