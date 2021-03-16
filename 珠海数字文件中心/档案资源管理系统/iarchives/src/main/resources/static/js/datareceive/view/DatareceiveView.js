/**
 * Created by yl on 2020/3/17.
 */
Ext.define('Datareceive.view.DatareceiveView', {
    extend: 'Ext.tab.Panel',
    xtype: 'datareceiveView',

    //标签页靠左配置--start
    tabPosition: 'top',
    tabRotation: 0,
    //标签页靠左配置--end

    activeTab: 0,
    items: [{
        title: '开放数据',
        xtype: 'panel',
        layout: 'fit',
        items: [{
            itemId:'tobereceived',
            xtype: 'datareceiveOpenGridView'
        }]
    }, {
        title: '专题档案',
        xtype: 'panel',
        layout: 'fit',
        items: [{
            itemId:'received',
            xtype: 'datareceiveThematicGridView'
        }]
    }]
});
