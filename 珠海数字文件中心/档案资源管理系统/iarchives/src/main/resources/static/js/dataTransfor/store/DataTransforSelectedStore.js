Ext.define('DataTransfor.store.DataTransforSelectedStore',{
    extend:'Ext.data.TreeStore',
    model:'DataTransfor.model.DataTransforSelectModel',
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
    	text: '节点数据',
        expanded: true,
        fnid:'functionid'
    },
    listeners:{
        nodebeforeexpand:function(node, deep, animal) {
            if((node.raw)){
                this.proxy.extraParams.pcid = node.raw.fnid;
            }
        }
    }
});