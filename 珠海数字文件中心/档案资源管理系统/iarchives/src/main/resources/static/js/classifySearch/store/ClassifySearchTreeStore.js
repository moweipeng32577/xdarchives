/**
 * Created by RonJiang on 2017/10/26 0026.
 */
Ext.define('ClassifySearch.store.ClassifySearchTreeStore',{
    extend:'Ext.data.TreeStore',
    model:'ClassifySearch.model.ClassifySearchTreeModel',
    proxy: {
        type: 'ajax',
        url: '/nodesetting/getClassificationByParentClassId',
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