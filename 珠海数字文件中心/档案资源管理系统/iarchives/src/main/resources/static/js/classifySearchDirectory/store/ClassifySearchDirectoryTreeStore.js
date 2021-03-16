/**
 * Created by Administrator on 2019/6/27.
 */


Ext.define('ClassifySearchDirectory.store.ClassifySearchDirectoryTreeStore',{
    extend:'Ext.data.TreeStore',
    model:'ClassifySearchDirectory.model.ClassifySearchDirectoryTreeModel',
    proxy: {
        type: 'ajax',
        // url: '/nodesetting/getClassificationByParentClassId',
        url: '/nodesetting/getNodeByParentId',
        extraParams:{pcid:'',type:'classifySearchDirectory'},
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
