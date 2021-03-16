Ext.define('Acquisition.store.AcquisitionSelectStore',{
    extend:'Ext.data.TreeStore',
    model:'Acquisition.model.AcquisitionSelectModel',
    proxy: {
        timeout:10000000,
        type: 'ajax',
        url: '/nodesetting/getNodeInfo',
        extraParams:{nodeinfo:'',type:'0'},
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
                this.proxy.extraParams.nodeinfo = node.raw.fnid;
            }
        }
    }
});