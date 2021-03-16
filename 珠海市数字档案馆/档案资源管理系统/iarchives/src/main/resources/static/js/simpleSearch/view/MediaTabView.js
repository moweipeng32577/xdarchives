/**
 * Created by Administrator on 2020/8/3.
 */


Ext.define('SimpleSearch.view.MediaTabView',{
    extend:'Ext.tab.Panel',
    xtype:'mediaTabView',
    itemId:'mediaTabViewId',
    tabPosition: 'top',
    tabRotation: 0,
    activeTab: 0,
    items:[{
        itemId: 'mediaGridViewId',
        title: '列表显示',
        xtype: 'mediaGridView'
    },{
        itemId: 'mediadtView',
        xtype: 'mediadtView',
        title:'缩略图',
        hasCloseButton:false,
        header:false,
        address:'video'
    }]
});
