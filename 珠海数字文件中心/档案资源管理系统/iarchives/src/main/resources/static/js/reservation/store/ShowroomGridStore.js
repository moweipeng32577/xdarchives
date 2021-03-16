/**
 * Created by zdw on 2020/03/20
 */
Ext.define('Reservation.store.ShowroomGridStore',{
    extend:'Ext.data.Store',
    model:'Reservation.model.ShowroomGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/showroom/getDateShowroom',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
