/**
 * Created by xd on 2017/10/21.
 */
Ext.define('User.store.UserGridStore',{
    extend:'Ext.data.Store',
    model:'User.model.UserGridModel',
    autoLoad: true,
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/user/getUnitUser',
        extraParams: {},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
