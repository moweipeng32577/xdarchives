/**
 * Created by Administrator on 2019/6/26.
 */


Ext.define('SimpleSearchDirectory.store.BookmarksGridStore',{
    extend:'Ext.data.Store',
    model:'SimpleSearchDirectory.model.SimpleSearchDirectoryGridModel',
    pageSize: XD.pageSize,//此处设置每页显示记录条数
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url:'/bookmarks/findBySearchDirectory',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
