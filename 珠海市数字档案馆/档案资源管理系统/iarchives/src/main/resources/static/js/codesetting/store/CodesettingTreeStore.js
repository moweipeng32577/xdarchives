/**
 * Created by tanly on 2017/10/24 0024.
 */
Ext.define('Codesetting.store.CodesettingTreeStore',{
    extend:'Ext.data.TreeStore',
    model:'Codesetting.model.CodesettingTreeModel',
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
        text: '档号设置',
        expanded: true
    },
    listeners:{
        nodebeforeexpand:function(node) {
            this.proxy.extraParams.pcid = node.get('fnid');
            this.proxy.url = '/nodesetting/getNodeByParentId';
        }
    }
});