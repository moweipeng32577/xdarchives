/**
 * Created by Administrator on 2017/10/25 0025.
 */
Ext.define('User.store.UserSetAreaStore', {
    extend: 'Ext.data.TreeStore',xtype:'userSetAreaStore',
    proxy: {
        type: 'ajax',
        url: '/user/areaList',
    },
    sorters: [],
    listeners:{
        beforeload:function(node){
            this.proxy.extraParams.userId = window.wuserGridView.userids;
        },
        // load:function(node){
        //     //this.proxy.extraParams.isp = 'k1';
        // },

       /* nodebeforeexpand:function(node, deep, animal) {
            if((node.raw)){
                this.proxy.extraParams.name = node.raw.name;
                this.proxy.extraParams.userId = window.wuserGridView.userids;
            }
        }*/
    }
});