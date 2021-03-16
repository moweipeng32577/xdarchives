/**
 * Created by Administrator on 2019/12/14.
 */


Ext.define('UserGroup.store.UserGroupSelectStore',{
    extend:'Ext.data.Store',
    model:'UserGroup.model.UserGroupSelectModel',
    idProperty: 'userid',
    fields: ['userid','realname'],
    proxy: {
        type: 'ajax',
        url: '/userGroup/getAllUsers',
        reader: {
            type: 'json'
        }
    }
});