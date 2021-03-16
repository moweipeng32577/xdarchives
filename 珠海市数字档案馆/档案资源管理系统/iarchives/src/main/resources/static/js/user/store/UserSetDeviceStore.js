/**
 * Created by Administrator on 2017/10/25 0025.
 */
Ext.define('User.store.UserSetDeviceStore', {
    extend: 'Ext.data.TreeStore',
    xtype:'userSetDeviceStore',
    proxy: {
        type: 'ajax',
        url: '/user/deviceList',
        extraParams:{fnid:1}
    },
    sorters: [],
    listeners:{
        beforeload:function(node){
            if(this.proxy.extraParams.type==""){
                this.proxy.extraParams.userId = window.wuserGridView.userids;
            }

        },

        nodebeforeexpand:function(node, deep, animal) {
            if((node.raw)){
                this.proxy.extraParams.type = node.raw.type;
                this.proxy.extraParams.userId = window.wuserGridView.userids;
            }
        }
    }
});