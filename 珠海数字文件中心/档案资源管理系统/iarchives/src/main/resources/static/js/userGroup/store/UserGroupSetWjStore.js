/**
 * Created by Administrator on 2017/10/25 0025.
 */
Ext.define('UserGroup.store.UserGroupSetWjStore', {
    extend: 'Ext.data.TreeStore',
    xtype:'userGroupSetWjStore',
    proxy: {
        type: 'ajax',
        url: '/userGroup/getWjList',
    },
    sorters: [],
    listeners: {
        beforeload: function (node) {
            this.proxy.extraParams.usergroupid = window.userGroupids;
        }
    }
});