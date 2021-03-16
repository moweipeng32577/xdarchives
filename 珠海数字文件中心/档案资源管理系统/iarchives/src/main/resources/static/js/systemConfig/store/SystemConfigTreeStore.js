/**
 * Created by tanly on 2017/11/1 0024.
 */
Ext.define('SystemConfig.store.SystemConfigTreeStore',{
    extend:'Ext.data.TreeStore',
    model:'SystemConfig.model.SystemConfigTreeModel',
    proxy: {
        type: 'ajax',
        url: '/systemconfig/getByParentconfigid',//直接引用nodesetting的方法
        extraParams:{parentconfigid:''},
        reader: {
            type: 'json',
            expanded: true
        }
    },
    root: {
        text: '参数设置',
        expanded: true,
        fnid:''
    },
    listeners:{
        nodebeforeexpand:function(node) {
            if(node.get('fnid')){
                this.proxy.extraParams.parentconfigid = node.get('fnid');
                this.proxy.url = '/systemconfig/getByParentconfigid';
            }
        }
    }
});