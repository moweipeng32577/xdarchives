/**
 * Created by xd on 2017/10/21.
 */
Ext.define('UserGroup.store.UserGroupStore',{
    extend:'Ext.data.Store',
    model:'UserGroup.model.UserGroupModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/userGroup/getUserGroup',
        extraParams: {},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
