/**
 * Created by Administrator on 2019/6/24.
 */


Ext.define('AcceptDirectory.store.AcceptDirectoryStore',{
    extend:'Ext.data.TreeStore',
    model:'AcceptDirectory.model.AcceptDirectoryModel',
    autoLoad:false,
    proxy: {
        type: 'ajax',
        url: '/nodesetting/getNodeByParentId',
        extraParams:{pcid:''},
        reader: {
            type: 'json'
        }
    },
    root: {
        text: '数据接收',
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
