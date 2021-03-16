/**
 * Created by Administrator on 2017/10/25 0025.
 */
Ext.define('UserGroup.store.UserGroupSetGnStore', {
    extend: 'Ext.data.TreeStore',
    xtype:'userGroupSetGnStore',
    proxy: {
        type: 'ajax',
        url: '/userGroup/getAllGn',
        extraParams:{fnid:1}
    },
    sorters: [],
    listeners:{
        beforeload:function(node){
            if(this.proxy.extraParams.fnid==1){
                this.proxy.extraParams.roleId = window.roleid;
                this.proxy.extraParams.xtType = window.userGroupViewTab;
            }

            // if(this.proxy.extraParams.fnid!=1){
            //     this.proxy.extraParams.fnid = node.raw.fnid;
            //     this.proxy.extraParams.userId = window.wuserGridView.userids;
            // }else{
            //     this.proxy.extraParams.fnid = 0;
            // }

        },
        // load:function(node){
        //     //this.proxy.extraParams.isp = 'k1';
        // },

        nodebeforeexpand:function(node, deep, animal) {
                if((node.raw)){
                    this.proxy.extraParams.fnid = node.raw.fnid;
                    this.proxy.extraParams.roleId = window.roleid;
                }
        }
    }
});