/**
 * Created by yl on 2017/11/13.
 */
Ext.define('LongRetention.store.LongRetentionTreeStore',{
    extend:'Ext.data.TreeStore',
    model:'LongRetention.model.LongRetentionTreeModel',
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
        text: '分类检索',
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