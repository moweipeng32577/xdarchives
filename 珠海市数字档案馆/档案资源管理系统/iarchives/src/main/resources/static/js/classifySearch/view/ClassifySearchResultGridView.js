/**
 * Created by RonJiang on 2017/11/3 0003.
 */
Ext.define('ClassifySearch.view.ClassifySearchResultGridView',{
    extend: 'Comps.view.EntryGridView',
    xtype:'classifySearchResultGridView',
    title: '当前位置：分类检索',
    addNodenameColumn:true,
    hasCloseButton:false,
    bookmarkStatus:false,//当前是否切换到个人收藏界面操作
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
        }, '-', {
            itemId: 'stAdd',
            xtype: 'button',
            iconCls:'fa fa-plus-circle',
            text: '添加实体查档'
        }, '-', {
            itemId: 'lookAdd',
            xtype: 'button',
            iconCls:'fa fa-eye',
            text: '处理实体查档'
        }, '-', {
            itemId: 'electronAdd',
            xtype: 'button',
            iconCls:'fa fa-plus-circle',
            text: '添加电子查档'
        }, '-', {
            itemId: 'dealElectronAdd',
            xtype: 'button',
            iconCls:'fa fa-indent',
            text: '处理电子查档'
        }],
    dataUrl: '/classifySearch/findByClassifySearch',
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