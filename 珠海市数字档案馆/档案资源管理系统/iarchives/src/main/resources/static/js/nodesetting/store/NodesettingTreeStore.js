/**
 * Created by tanly on 2017/10/24 0024.
 */
Ext.define('Nodesetting.store.NodesettingTreeStore',{
    extend:'Ext.data.TreeStore',
    model:'Nodesetting.model.NodesettingTreeModel',
    proxy: {
        timeout:10000000,
        type: 'ajax',
        url: '/nodesetting/getNodeByParentId',
        extraParams:{pcid:''},
        reader: {
            type: 'json',
            expanded: true
        }
    },
    root: {
        text: '节点设置',
        expanded: true,
        fnid:'functionid'
    },
    listeners:{
        nodebeforeexpand:function(node, deep, animal) {
            if(node.get('fnid')!='functionid'&&node.get('fnid')){
                this.proxy.extraParams.pcid = node.get('fnid');
                this.proxy.extraParams.xtType =window.classViewTab;
                this.proxy.url = '/nodesetting/getNodeByParentId';
            };
        }
    }
});