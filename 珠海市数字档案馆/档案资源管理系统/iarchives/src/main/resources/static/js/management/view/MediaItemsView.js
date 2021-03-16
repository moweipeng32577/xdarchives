/**
* Created by Lep on 2019/11/19 0024.
*/
Ext.define('Management.view.MediaItemsView',{
    extend: 'Ext.panel.Panel',
    xtype:'mediaItemsView',
    title: '当前位置：数据管理',
    itemId:'mediaItemsViewId',
    tbar: {
        overflowHandler: 'scroller',
        itemId:"functionTbar",
        items: functionButton
    },
    hasSearchBar:true,//无需基础表格组件中的检索栏
    bookmarkStatus:false,//当前是否切换到个人收藏界面操作
    items:[{
        itemId: 'mediaItemsDataView',
        xtype: 'mediaItemsDataView',
        title:'缩略图',
        hasCloseButton:false,
        header:false,
        address:'video'//dataType.indexOf('5,8') != -1 ? 'photo': dataType.indexOf('6,9') != -1 ? 'video':'audio',
    }]
});