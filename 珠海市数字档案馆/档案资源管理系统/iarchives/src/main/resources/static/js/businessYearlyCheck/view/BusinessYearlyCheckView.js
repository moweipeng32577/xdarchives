/**
 * Created by Administrator on 2020/10/13.
 */


Ext.define('BusinessYearlyCheck.view.BusinessYearlyCheckView', {
    extend: 'Ext.tab.Panel',
    xtype: 'businessYearlyCheckView',
    requires: [
        'Ext.layout.container.Border'
    ],
    //标签页靠左配置--start
    tabPosition: 'top',
    tabRotation: 0,
    //标签页靠左配置--end

    activeTab: 0,

    items: [{
        title: '档案年检报表',
        layout: 'fit',
        items:[{
            xtype:'businessYearlyCheckReportView'
        }]
    }, {
        title: '新建年检报表',
        layout: 'fit',
        items:[{
            xtype:'businessNewYearlyCheckReportView'
        }]
    }]
});
