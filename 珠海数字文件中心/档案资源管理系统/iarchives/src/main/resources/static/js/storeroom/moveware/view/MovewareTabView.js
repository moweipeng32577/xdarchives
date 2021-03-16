/**
 * Created by tanly on 2017/12/1 0001.
 */
Ext.define('Moveware.view.MovewareTabView', {
    extend: 'Ext.tab.Panel',
    xtype: 'movewareTabView',

    //标签页靠左配置--start
    tabPosition: 'top',
    tabRotation: 0,
    //标签页靠左配置--end

    activeTab: 0,
    items: [{
        title: '档案移动',
        xtype: 'panel',
        layout: 'fit',
        items: [{
            itemId:'waregrid',
            xtype: 'entryMoveShellView'/*'entryMoveView'*/
        }]
    }, {
        title: '单元格移动',
        xtype: 'panel',
        layout: 'fit',
        items: [{
            itemId:'opened',
            xtype: 'shelvesMoveView'
        }]
    }]
});