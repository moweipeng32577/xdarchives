/**
 * Created by Administrator on 2019/7/3.
 */


Ext.define('AssemblyAdmin.view.AssemblyAdminLinkTabView', {
    extend: 'Ext.tab.Panel',
    xtype: 'assemblyAdminLinkTabView',

    //标签页靠左配置--start
    tabPosition: 'top',
    tabRotation: 0,
    //标签页靠左配置--end

    activeTab: 0,
    items: [{
        title: '环节配置',
        xtype: 'panel',
        layout: 'fit',
        items: [{
            xtype: 'assemblyAdminLinkSetView'

        }]
    }, {
        title: '前置环节',
        xtype: 'panel',
        layout: 'fit',
        items: [{
            xtype: 'assemblyAdminPreLinkView'
        }]
    }]
});
