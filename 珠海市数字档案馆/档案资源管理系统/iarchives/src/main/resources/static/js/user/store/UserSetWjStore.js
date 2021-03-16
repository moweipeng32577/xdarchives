/**
 * Created by Administrator on 2017/10/25 0025.
 */
Ext.define('User.store.UserSetWjStore', {
    extend: 'Ext.data.TreeStore',
    xtype:'userSetWjStore',
    proxy: {
        type: 'ajax',
        url: '/user/getWjList',
    },
    sorters: [],
    listeners: {
        beforeload: function (node) {
            this.proxy.extraParams.userid = window.wuserGridView.userids;
        }
    }
});