/**
 * Created by tanly on 2017/12/1.
 */
Ext.define('Dataopen.store.DataopenTreeStore',{
    extend:'Ext.data.TreeStore',
    model:'Dataopen.model.DataopenTreeModel',
    proxy: {
        type: 'ajax',
        url: '/nodesetting/getNodeByParentId',
        extraParams:{pcid:'', type:'数据开放'},
        reader: {
            type: 'json',
            expanded: true
        }
    },
    root: {
        text: '数据开放',
        expanded: true
    },
    listeners:{
        nodebeforeexpand:function(node) {
            if((node.raw)){
                this.proxy.extraParams.pcid = node.raw.fnid;
            }
        }
    }
});