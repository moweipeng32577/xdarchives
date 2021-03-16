/**
 * Created by Administrator on 2018/9/12.
 */


Ext.define('User.store.UserSequenceStore',{
    extend:'Ext.data.Store',
    model:'User.model.UserGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/user/userids',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});