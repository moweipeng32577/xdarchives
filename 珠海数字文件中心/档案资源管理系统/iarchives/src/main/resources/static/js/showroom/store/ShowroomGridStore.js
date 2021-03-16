/**
 * Created by zdw on 2020/03/20
 */
Ext.define('Showroom.store.ShowroomGridStore',{
    extend:'Ext.data.Store',
    model:'Showroom.model.ShowroomGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/showroom/getShowroom',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
