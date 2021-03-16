/**
 * Created by tanly on 2018/04/21 0025.
 */
Ext.define('User.store.UserSetOrganStore', {
    extend: 'Ext.data.TreeStore',
    xtype:'uerSetOrganStore',
    proxy: {
        type: 'ajax',
        url: '/nodesetting/getCkeckOrganByParentId',
        extraParams:{pcid:'0'}
    },
    sorters: [],
    root: {},
    listeners:{
        beforeload:function(node){
            this.proxy.extraParams.userids = window.wuserGridView.userids;
        },

        nodebeforeexpand:function(node, deep, animal) {
            if((node.raw)){
                this.proxy.extraParams.pcid = node.raw.fnid;
                this.proxy.extraParams.userids = window.wuserGridView.userids;
            }
        }
    }
});