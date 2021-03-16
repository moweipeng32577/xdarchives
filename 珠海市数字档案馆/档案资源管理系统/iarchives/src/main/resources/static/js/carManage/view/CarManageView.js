/**
 * Created by Administrator on 2020/6/24.
 */



Ext.define('CarManage.view.CarManageView', {
    extend: 'Ext.panel.Panel',
    xtype: 'carManageView',
    layout: 'card',
    activeItem: 0,
    items: [{
        xtype: 'carManageGridView'
    }, {
        xtype: 'carDefendGridView'
    }]
});
