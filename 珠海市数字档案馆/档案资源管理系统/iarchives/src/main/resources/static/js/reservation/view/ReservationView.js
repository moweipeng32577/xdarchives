/**
 * Created by Administrator on 2020/3/17.
 */

Ext.define('Reservation.view.ReservationView', {
    extend: 'Ext.panel.Panel',
    xtype: 'reservationView',
    layout: 'border',
    bodyBorder: false,
    defaults: {
        split: true
    },
    items: [{
        region: 'west',
        width: 240,
        xtype: 'treepanel',
        itemId: 'treepanelId',
        header: false,
        hideHeaders: true,
        store: {
            extend: 'Ext.data.TreeStore',
            autoLoad:true,
            proxy: {
                type: 'ajax',
                url:'/systemconfig/getByConfigcode',
                extraParams:{
                    configcode:'预约类型',
                    type:iflag=='1'? "Ly":"Gl"
                },
                reader: {
                    type: 'json',
                    expanded: true
                }
            },
            root: {
                text: '预约类型',
                expanded: true
            }
        },
        autoScroll: true,
        rootVisible: true
    }, {
        region: 'center',
        layout:'card',
        itemId:'gridcard',
        activeItem:0,
        items: [
            {
                xtype:'reservationAdminsView'
            },{
                xtype:'placeOrderManageView'
            },{
                xtype:'myOrderGridView'
            }
        ]
    }]
});
