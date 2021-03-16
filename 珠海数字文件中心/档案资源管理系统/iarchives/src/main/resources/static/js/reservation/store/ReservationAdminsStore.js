/**
 * Created by Administrator on 2020/3/3.
 */
Ext.define('Reservation.store.ReservationAdminsStore',{
    extend:'Ext.data.Store',
    model:'Reservation.model.ReservationAdminsModel',
    autoLoad: true,
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/jyAdmins/getReservationdocs',
        extraParams: {},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});