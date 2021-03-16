/**
 * Created by Rong on 2017/10/24.
 */
Ext.define('Acquisition.store.AcquisitionStore',{
    extend:'Ext.data.TreeStore',
    model:'Acquisition.model.AcquisitionModel',
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
        text: '数据采集',
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