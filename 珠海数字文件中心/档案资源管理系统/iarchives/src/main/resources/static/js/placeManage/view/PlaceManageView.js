/**
 * Created by Administrator on 2020/6/24.
 */


Ext.define('PlaceManage.view.PlaceManageView', {
    extend: 'Ext.panel.Panel',
    xtype: 'placeManageView',
    layout: 'card',
    activeItem: 0,
    items: [{
        xtype: 'placeManageGridView'
    }, {
        xtype: 'placeDefendGridView'
    }]
});
