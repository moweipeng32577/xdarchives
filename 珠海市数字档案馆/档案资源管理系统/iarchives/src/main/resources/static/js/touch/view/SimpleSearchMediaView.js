/**
 * Created by Lep on 2019/11/19 0024.
 */
Ext.define('Touch.view.SimpleSearchMediaView',{
    extend: 'Ext.panel.Panel',
    xtype:'simpleSearchMediaView',
    title: '当前位置：简单检索',
    itemId:'simpleSearchMediaViewId',
    hasSearchBar:false,//无需基础表格组件中的检索栏
    bookmarkStatus:false,//当前是否切换到个人收藏界面操作
    layout: 'fit',
    items:[{
        itemId: 'mediaTabViewId',
        xtype: 'mediaTabView'
    }]
});