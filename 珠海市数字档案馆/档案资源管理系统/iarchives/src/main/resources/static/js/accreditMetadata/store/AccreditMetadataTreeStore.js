/**
 * Created by tanly on 2017/11/1 0024.
 */
Ext.define('AccreditMetadata.store.AccreditMetadataTreeStore',{
    extend:'Ext.data.TreeStore',
    model:'AccreditMetadata.model.AccreditMetadataTreeModel',
    proxy: {
        type: 'ajax',
        url: '/accreditMetadata/getByParentconfigid',//直接引用nodesetting的方法
        extraParams:{parentconfigid:''},
        reader: {
            type: 'json',
            expanded: true
        }
    },
    root: {
        text: '授权元数据维护',
        expanded: true,
        fnid:''
    },
    listeners:{
        nodebeforeexpand:function(node) {
            if(node.get('fnid')){
                this.proxy.extraParams.parentconfigid = node.get('fnid');
                this.proxy.url = '/accreditMetadata/getByParentconfigid';
            }
        }
    }
});