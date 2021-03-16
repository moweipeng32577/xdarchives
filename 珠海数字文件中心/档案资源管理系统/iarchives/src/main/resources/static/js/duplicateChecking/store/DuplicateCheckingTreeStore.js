/**
 * Created by RonJiang on 2018/01/24
 */
Ext.define('DuplicateChecking.store.DuplicateCheckingTreeStore',{
    extend:'Ext.data.TreeStore',
    model:'DuplicateChecking.model.DuplicateCheckingTreeModel',
    proxy: {
        type: 'ajax',
        url: '/nodesetting/getNodeByParentId',
        extraParams:{pcid:''},
        reader: {
            type: 'json'
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