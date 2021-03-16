/**
 * Created by Administrator on 2020/4/21.
 */

Ext.define('CarOrder.view.CarOrderView', {
    extend:'Ext.tab.Panel',
    xtype:'carOrderView',
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
            items:[{xtype:'carOrderManageView'}]
        },{
            title:'预约审核',
            layout: 'fit',
            hidden:orderAuditState=='true'?false:true,
            items:[{xtype:'carOrderAuditGridView'}]
        }]
});
