/**
 * Created by Administrator on 2017/10/25 0025.
 */
Ext.define('UserGroup.store.UserGroupSetSjStore', {
    extend: 'Ext.data.TreeStore',
    xtype:'userGroupSetSjStore',
    proxy: {
        type: 'ajax',
        url: '/userGroup/getAllSjQx',
        timeout:XD.timeout,
        extraParams:{pcid:''}
    },
    sorters: [],
    root: {
       // text: '用户管理aa',
        //expanded: true

    },
    listeners:{
        beforeload:function(node){
            // if(this.proxy.extraParams.fnid==1){
            //     this.proxy.extraParams.userId = window.wuserGridView.userids;
            // }
            this.proxy.extraParams.roleId = window.roleid;
            this.proxy.extraParams.xtType = window.userGroupViewTab;
        },

        nodebeforeexpand:function(node, deep, animal) {
            if((node.raw)){
                this.proxy.extraParams.pcid = node.raw.fnid;
                this.proxy.extraParams.roleId = window.roleid;
            }
        }
    }
});