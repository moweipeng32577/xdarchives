/**
 * Created by tanly on 2017/11/1 0024.
 */
Ext.define('Classificationsetting.store.ClassificationsettingGridStore',{
    extend:'Ext.data.Store',
    model:'Classificationsetting.model.ClassificationsettingGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/classificationsetting/classifications',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});