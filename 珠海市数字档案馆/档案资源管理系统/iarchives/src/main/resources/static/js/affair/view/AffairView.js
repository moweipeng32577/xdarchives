/**
 * Created by Administrator on 2020/7/20.
 */
Ext.define('Affair.view.AffairView', {
    extend: 'Ext.tab.Panel',
    xtype: 'affairView',
    //标签页靠左配置--start
    tabPosition:'top',
    tabRotation:0,
    //标签页靠左配置--end

    activeTab:0,

    items:[
        {
            title:'待处理',
            layout: 'fit',
            itemId:'affairDclViewID',
            items:[{xtype:'affairDclView'}]
        },
        {
            title:'已处理',
            layout: 'fit',
            itemId:'affairYclViewID',
            items:[{xtype:'affairYclView'}]
        }],
    listeners: {
        beforerender: function (view) {

        }
    }
});
