/**
 * Created by tanly on 2017/11/1 0024.
 */
Ext.define('ServiceMetadata.store.AccreditMetadataTreeStore',{
    extend:'Ext.data.TreeStore',
    model:'ServiceMetadata.model.AccreditMetadataTreeModel',
    proxy: {
        type: 'ajax',
        url: '/serviceMetadata/getByParentconfigid',//直接引用nodesetting的方法
        extraParams:{parentconfigid:''},
        reader: {
            type: 'json',
            expanded: true
        }
    },
    root: {
        text: '业务元数据维护',
        expanded: true,
        fnid:''
    },
    listeners:{
        nodebeforeexpand:function(node) {
            if(node.get('fnid')){
                this.proxy.extraParams.parentconfigid = node.get('fnid');
                this.proxy.url = '/serviceMetadata/getByParentconfigid';
            }
        }
    }
});