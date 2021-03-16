/**
 * Created by Administrator on 2020/7/29.
 */


Ext.define('ManageCenter.store.ManagementStore',{
    extend:'Ext.data.TreeStore',
    model:'ManageCenter.model.ManagementModel',
    proxy: {
        type: 'ajax',
        url: '/nodesetting/getNodeByParentId',
        extraParams:{pcid:''},
        reader: {
            type: 'json',
            expanded: true
        }
    },
    root: {
        text: '数据管理',
        expanded: false
    },
    listeners:{
        nodebeforeexpand:function(node, deep, animal) {
            if((node.raw)){
                this.proxy.extraParams.pcid = node.raw.fnid;
            }
        }
    }
});
