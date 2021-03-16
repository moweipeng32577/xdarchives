/**
 * Created by Administrator on 2020/7/17.
 */


Ext.define('ElectronApprove.store.ClassifySearchTreeStore',{
    extend:'Ext.data.TreeStore',
    model:'ElectronApprove.model.ClassifySearchTreeModel',
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
