/**
 * Created by Administrator on 2017/10/25 0025.
 */
Ext.define('User.store.UserSetDeviceJoinStore', {
    extend: 'Ext.data.TreeStore',
    xtype:'userSetDeviceJoinStore',
    proxy: {
        type: 'ajax',
        url: '/user/deviceJoinList',
        extraParams:{
            fnid:1,
            userids:[]
        }
    },
    sorters: [],
    listeners:{
        nodebeforeexpand:function(node, deep, animal) {
            if((node.raw)){
                this.proxy.extraParams.type = node.raw.type;
            }
        }
    }
});