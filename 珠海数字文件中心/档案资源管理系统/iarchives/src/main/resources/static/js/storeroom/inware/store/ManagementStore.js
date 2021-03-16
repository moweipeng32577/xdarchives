/**
 * Created by Rong on 2017/10/24.
 */
Ext.define('Inware.store.ManagementStore',{
    extend:'Ext.data.TreeStore',
    model:'Inware.model.ManagementModel',
    proxy: {
        type: 'ajax',
        url: '/nodesetting/getKfNodeByParentId',
        extraParams:{pcid:''},
        reader: {
            type: 'json',
            expanded: true
        }
    },
    root: {
        text: '数据管理',
        expanded: true
    },
    listeners:{
        nodebeforeexpand:function(node, deep, animal) {
            if((node.raw)){
                this.proxy.extraParams.pcid = node.raw.fnid;
            }
        }
    }
});