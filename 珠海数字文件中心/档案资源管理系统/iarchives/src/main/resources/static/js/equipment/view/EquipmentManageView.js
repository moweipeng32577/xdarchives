/**
 * Created by Administrator on 2020/6/24.
 */

Ext.define('Equipment.view.EquipmentManageView', {
    extend: 'Ext.panel.Panel',
    xtype: 'equipmentManageView',
    layout: 'card',
    activeItem: 0,
    items: [{
        xtype: 'equipmentGridView'
    }, {
        xtype: 'equipmentDefendGridView'
    }]
});
