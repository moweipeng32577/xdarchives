/**
 * Created by tanly on 2018/4/23 0023.
 */
Ext.define('UserGroup.store.UserGroupSetOrganStore', {
    extend: 'Ext.data.TreeStore',
    xtype:'userGroupSetOrganStore',
    proxy: {
        type: 'ajax',
        url: '/userGroup/getAllOrganAuth',
        extraParams:{pcid:'0'}
    },
    sorters: [],
    root: {},
    listeners:{
        beforeload:function(node){
            this.proxy.extraParams.roleId = window.roleid;
        },

        nodebeforeexpand:function(node, deep, animal) {
            if((node.raw)){
                this.proxy.extraParams.pcid = node.raw.fnid;
                this.proxy.extraParams.roleId = window.roleid;
            }
        }
    }
});