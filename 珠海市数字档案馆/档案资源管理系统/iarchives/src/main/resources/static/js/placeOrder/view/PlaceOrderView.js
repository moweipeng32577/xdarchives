/**
 * Created by Administrator on 2020/4/28.
 */


Ext.define('PlaceOrder.view.PlaceOrderView', {
    extend:'Ext.tab.Panel',
    xtype:'placeOrderView',
    //标签页靠左配置--start
    tabPosition:'top',
    tabRotation:0,
    //标签页靠左配置--end

    activeTab:0,

    items:[
        {
            title:'预约',
            layout: 'fit',
            hidden:iflag=='1'?true:false,
            items:[{xtype:'placeOrderManageView'}]
        },
        {
            title:'预约审核',
            layout: 'fit',
            hidden:orderAuditState=='true'?false:true,
            items:[{xtype:'placeOrderAuditGridView'}]
        }
        ]
});