/**
 * Created by tanly on 2017/12/1 0001.
 */
Ext.define('Dataopen.view.DataopenTabView', {
    extend: 'Ext.tab.Panel',
    xtype: 'dataopenTabView',

    //标签页靠左配置--start
    tabPosition: 'top',
    tabRotation: 0,
    //标签页靠左配置--end

    activeTab: 0,
    items: [{
        title: '未开放',
        xtype: 'panel',
        layout: 'fit',
        items: [{
            itemId:'unopened',
            xtype: 'dataopengrid'
        }]
    }, {
        title: '已开放',
        xtype: 'panel',
        layout: 'fit',
        items: [{
            itemId:'opened',
            xtype: 'dataopenOpenedGridView'
        }]
    }, {
        title: '不开放',
        xtype: 'panel',
        layout: 'fit',
        items: [{
            itemId:'dontOpenItem',
            xtype: 'dataopenDontOpenGridView'
        }]
    }]
});