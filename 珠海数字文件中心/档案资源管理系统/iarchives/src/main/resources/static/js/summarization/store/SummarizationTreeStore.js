/**
 * Created by RonJiang on 2018/01/24
 */
Ext.define('Summarization.store.SummarizationTreeStore',{
    extend:'Ext.data.TreeStore',
    model:'Summarization.model.SummarizationTreeModel',
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