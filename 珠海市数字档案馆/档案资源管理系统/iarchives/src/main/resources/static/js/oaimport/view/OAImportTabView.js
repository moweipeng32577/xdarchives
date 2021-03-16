/**
 * Created by tanly on 2018/1/22 0022.
 */
Ext.define('OAImport.view.OAImportTabView', {
    extend: 'Ext.tab.Panel',
    xtype: 'oaimporttab',
    itemId: 'OAImportTabViewId',
    tabPosition: 'top',
    tabRotation: 0,
    activeTab: 0,
    items: [{
        title: '便捷操作',
        itemId: 'oaimportTab',
        xtype: 'oaimportView'
    }]
});