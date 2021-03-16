/**
 * Created by tanly on 2018/9/17 0024.
 */
Ext.define('User.store.UserCopySelectStore', {
    extend: 'Ext.data.Store',
    xtype: 'userCopySelectStore',
    idProperty: 'fnid',
    fields: ['fnid', 'text'],
    proxy: {
        type: 'ajax',
        url: '/user/getCopyUser',
        reader: {
            type: 'json'
        }
    },
    autoLoad: true
});
