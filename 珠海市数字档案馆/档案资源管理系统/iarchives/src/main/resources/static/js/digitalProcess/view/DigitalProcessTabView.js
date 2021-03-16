Ext.define('DigitalProcess.view.DigitalProcessTabView', {
    extend: 'Ext.tab.Panel',
    xtype: 'DigitalProcessTabView',

    //标签页靠左配置--start
    tabPosition: 'top',
    tabRotation: 0,
    //标签页靠左配置--end

    activeTab: 0,
    items: [{
        title: '未签收',
        xtype: 'panel',
        layout: 'fit',
        items: [{
            itemId:'wqsGridId',
            xtype: 'DigitalProcessWqsGridView'
        }]
    }, {
        title: '已签收',
        xtype: 'panel',
        layout: 'fit',
        items: [{
            itemId:'yqsGridId',
            xtype: 'DigitalProcessYqsGridView'
        }]
    }, {
        title: '已处理',
        xtype: 'panel',
        layout: 'fit',
        items: [{
            itemId:'ywcGridId',
            xtype: 'DigitalProcessYwcGridView'
        }]
    }]
});