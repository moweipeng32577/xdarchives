/**
 * Created by Administrator on 2020/7/20.
 */
Ext.define('Curator.view.CuratorView', {
    extend: 'Ext.tab.Panel',
    xtype: 'curatorView',
    //标签页靠左配置--start
    tabPosition:'top',
    tabRotation:0,
    //标签页靠左配置--end

    activeTab:0,

    items:[
        {
            title:'待处理',
            layout: 'fit',
            itemId:'curatorDclViewID',
            items:[{xtype:'curatorDclView'}]
        },
        {
            title:'已处理',
            layout: 'fit',
            itemId:'curatorYclViewID',
            items:[{xtype:'curatorYclView'}]
        }],
    listeners: {
        beforerender: function (view) {

        }
    }
});
