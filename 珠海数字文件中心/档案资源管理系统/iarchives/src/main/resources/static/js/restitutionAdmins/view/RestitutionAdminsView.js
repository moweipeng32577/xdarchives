/**
 * Created by yl on 2017/11/3.
 */
Ext.define('Restitution.view.RestitutionAdminsView', {
    extend:'Ext.tab.Panel',
    xtype:'restitutionAdminsView',
    //标签页靠左配置--start
    tabPosition:'top',
    tabRotation:0,
    //标签页靠左配置--end

    activeTab:0,

    items:[
        {
            title:'登记',
            layout: 'fit',
            itemId:'registerId',
            items:[{xtype:'restitutionRegisterView'}]
        },
        {
        title:'未归还',
        layout: 'fit',
        itemId:'dzJyId',
        items:[{xtype:'restitutionWghView'}]
    },{
        title:'已归还',
        layout: 'fit',
        itemId:'stJyId',
        items:[{xtype:'restitutionYghView'}]
    },{
        title:'已转出',
        layout: 'fit',
        itemId:'yzcId',
        items:[{xtype:'restitutionYzcView'}]
    }],
    listeners: {
        beforerender: function (view) {
            if(borrowflag=='1'){
                view.remove(0);
            }
        }
    }
});