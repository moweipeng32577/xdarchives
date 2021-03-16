/**
 * Created by Administrator on 2020/7/20.
 */
Ext.define('ProjectAdd.view.ProjectAddView', {
    extend: 'Ext.tab.Panel',
    xtype: 'projectAddView',
    //标签页靠左配置--start
    tabPosition:'top',
    tabRotation:0,
    //标签页靠左配置--end

    activeTab:0,

    items:[
        {
            title:'待处理',
            layout: 'fit',
            itemId:'addDclViewID',
            items:[{xtype:'addDclView'}]
        },
        {
            title:'已处理',
            layout: 'fit',
            itemId:'addYclViewID',
            items:[{xtype:'addYclView'}]
        }],
    listeners: {
        beforerender: function (view) {

        }
    }
});
