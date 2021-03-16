/**
 * Created by yl on 2017/11/3.
 */
Ext.define('Organ.view.OrganPreviewNodeTabView', {
    extend:'Ext.tab.Panel',
    xtype:'organPreviewNodeTabView',
    requires: [
        'Ext.layout.container.Border'
    ],
    //标签页靠左配置--start
    tabPosition:'top',
    tabRotation:0,
    //标签页靠左配置--end

    activeTab:0,

    items:[{
        title:'档案系统',
        xtype: 'treepanel',
        itemId: 'previewTreepanelDaId',
        store: 'OrganPreviewTreeStore',
        collapsible: true,
        split: 1,
        bodyBorder: false
    },{
        title:'声像系统',
        xtype: 'treepanel',
        itemId: 'previewTreepanelSxId',
        store: 'OrganPreviewTreeStore',
        collapsible: true,
        split: 1,
        bodyBorder: false
    }]
});