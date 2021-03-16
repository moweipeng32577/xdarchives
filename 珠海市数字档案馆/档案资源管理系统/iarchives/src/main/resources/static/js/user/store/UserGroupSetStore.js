/**
 * Created by tanly on 2018/1/12 0012.
 */
Ext.define('User.store.UserGroupSetStore',{
    extend:'Ext.data.Store',
    model:'User.model.UserGroupSetModel',
    idProperty: 'roleid',
    fields: ['roleid','rolename'],
    proxy: {
        type: 'ajax',
        url: '/userGroup/getUserGroupString',
        reader: {
            type: 'json'
        }
    },
    listeners: {
        beforeload: function (){
            this.proxy.extraParams.xtType = window.userGridViewTab;
        }
    }
});