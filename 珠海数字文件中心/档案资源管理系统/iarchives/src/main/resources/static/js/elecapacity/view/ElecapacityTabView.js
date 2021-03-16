/**
 * Created by yl on 2017/11/3.
 */
Ext.define('Elecapacity.view.ElecapacityTabView', {
    extend:'Ext.tab.Panel',
    xtype:'elecapacityTabView',
    //标签页靠左配置--start
    tabPosition:'top',
    tabRotation:0,
    //标签页靠左配置--end
    activeTab:0,
    items:[
        {
            title:'容量统计',
            layout: 'fit',
            itemId:'total',
            items:[{xtype:'elecapacityView'}]
        },
        {
            title: '库房明细',
            layout: 'fit',
            itemId: 'detail',
            items: [
                {xtype: 'elecapacityListView'}
                ]
        }]
});