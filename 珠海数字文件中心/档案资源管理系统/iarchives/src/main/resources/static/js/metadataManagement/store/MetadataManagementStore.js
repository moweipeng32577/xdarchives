/**
 * Created by SunK on 2018/7/31 0031.
 */
Ext.define('MetadataManagement.store.MetadataManagementStore',{
    extend:'Ext.data.TreeStore',
    model:'MetadataManagement.model.MetadataManagementModel',
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
        text: '元数据管理',
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