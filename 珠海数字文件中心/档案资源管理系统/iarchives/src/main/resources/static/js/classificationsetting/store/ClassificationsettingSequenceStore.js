/**
 * Created by Administrator on 2018/9/12.
 */

Ext.define('Classificationsetting.store.ClassificationsettingSequenceStore',{
    extend:'Ext.data.Store',
    model:'Classificationsetting.model.ClassificationsettingGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/classificationsetting/classids',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
