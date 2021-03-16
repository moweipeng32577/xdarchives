/**
 * Created by xd on 2017/10/21.
 */
Ext.define('ArchiveManagement.store.ArchiveTreeStore',{
    extend:'Ext.data.TreeStore',
    model:'ArchiveManagement.model.ArchiveTreeModel',
    proxy: {
        type: 'ajax',
        url: '/nodesetting/getOrganByParentId',//直接引用nodesetting的方法
        extraParams:{pcid:'0'},
        reader: {
            type: 'json',
            expanded: true
        }
    },
    root: {
        text: '用户管理',
        expanded: true,
        fnid:'0'
    },
    listeners:{
        nodebeforeexpand:function(node, deep, animal) {
            if((node.raw)){
                this.proxy.extraParams.pcid = node.raw.fnid;
            }
        }
    }
});
