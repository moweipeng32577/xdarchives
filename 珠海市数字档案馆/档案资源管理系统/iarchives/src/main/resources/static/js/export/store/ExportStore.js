/**
 * Created by SunK on 2018/7/31 0031.
 */
Ext.define('Export.store.ExportStore',{
    extend:'Ext.data.TreeStore',
    model:'Export.model.ExportModel',
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
        text: '数据导出',
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