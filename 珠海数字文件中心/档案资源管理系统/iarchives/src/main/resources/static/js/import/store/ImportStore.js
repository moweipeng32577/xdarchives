/**
 * Created by SunK on 2018/8/3 0003.
 */
Ext.define('Import.store.ImportStore',{
    extend:'Ext.data.TreeStore',
    model:'Import.model.ImportModel',
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
        text: '数据导入',
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