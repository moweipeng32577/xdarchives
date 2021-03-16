/**
 * Created by Administrator on 2019/12/14.
 */


Ext.define('UserGroup.store.LookUserGroupGridStore',{
    extend:'Ext.data.Store',
    model:'UserGroup.model.LookUserGroupGridModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/userGroup/getUsersOnUserGroup',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
