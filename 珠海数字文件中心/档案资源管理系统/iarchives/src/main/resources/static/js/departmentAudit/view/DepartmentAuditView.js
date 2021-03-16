/**
 * Created by Administrator on 2020/7/20.
 */
Ext.define('DepartmentAudit.view.DepartmentAuditView', {
    extend: 'Ext.tab.Panel',
    xtype: 'departmentAuditView',
    //标签页靠左配置--start
    tabPosition:'top',
    tabRotation:0,
    //标签页靠左配置--end

    activeTab:0,

    items:[
        {
            title:'待处理',
            layout: 'fit',
            itemId:'auditDclViewID',
            items:[{xtype:'auditDclView'}]
        },
        {
            title:'已处理',
            layout: 'fit',
            itemId:'auditYclViewID',
            items:[{xtype:'auditYclView'}]
        }],
    listeners: {
        beforerender: function (view) {

        }
    }
});
