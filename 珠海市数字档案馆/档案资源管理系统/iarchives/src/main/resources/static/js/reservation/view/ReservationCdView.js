/**
 * Created by Administrator on 2020/3/3.
 */
Ext.define('Reservation.view.ReservationCdView', {
    extend: 'Ext.window.Window',
    xtype: 'reservationCdView',
    height: '100%',
    width: '100%',
    header: false,
    draggable: false,//禁止拖动
    resizable: false,//禁止缩放
    modal: true,
    closeToolText:'关闭',
    closeAction: 'hide',
    layout: 'border',
    split:true,
    items: [
        {
            xtype: 'reservationFormItemView',
            region: 'north',height: '40%'
        }, {
            xtype: 'restitutionReplyFormView',
            title:'预约回复单',
            region:'center'
        }, {
            xtype:'restitutionCancelFormView',
            title:'取消预约单',
            region:'south',height: '30%'
        }
    ]
});