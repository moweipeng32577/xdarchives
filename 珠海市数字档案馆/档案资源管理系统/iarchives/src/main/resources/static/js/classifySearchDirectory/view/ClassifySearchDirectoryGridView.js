/**
 * Created by Administrator on 2019/6/27.
 */


Ext.define('ClassifySearchDirectory.view.ClassifySearchDirectoryGridView',{
    extend: 'Comps.view.EntryGridView',
    xtype:'classifySearchDirectoryGridView',
    title: '当前位置：高级检索',
    addNodenameColumn:true,
    hasCloseButton:false,
    bookmarkStatus:false,//当前是否切换到个人收藏界面操作
    isOpenEle:false,
    tbar: [{
        itemId:'classifySearchBackId',
        xtype: 'button',
        iconCls:'fa fa-undo',
        text: '返回'
    },'-',{
        itemId:'classifySearchShowId',
        xtype: 'button',
        iconCls:'fa fa-eye',
        text: '查看'
    }, '-', {
        itemId:'classifySearchExportId',
        xtype: 'button',
        iconCls:'fa fa-share-square-o',
        text: '导出excel'
    }, '-', {
        itemId:'setBookmarks',
        xtype: 'button',
        iconCls:'fa fa-star',
        text: '收藏'
    }, '-', {
        itemId:'viewBookmarks',
        xtype: 'button',
        iconCls:'fa fa-heart',
        text: '查看收藏'
    }, '-',{
        itemId:'print',
        xtype: 'button',
        iconCls:'fa fa-print',
        text: '打印'
    }],
    dataUrl: '/classifySearchDirectory/findByClassifySearch',
    searchstore:{
        proxy: {
            type: 'ajax',
            url:'/template/queryName',
            extraParams:{nodeid:0},
            reader: {
                type: 'json',
                rootProperty: 'content',
                totalProperty: 'totalElements'
            }
        }
    }
});
