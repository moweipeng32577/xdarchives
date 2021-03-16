/**
 * Created by xd on 2017/10/21.
 */
Ext.define('Elecapacity.store.ElecapacityListStore',{
    extend:'Ext.data.Store',
    model:'Elecapacity.model.ElecapacityListModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/elecapacity/getlist',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
