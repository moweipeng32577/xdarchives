/**
 * Created by tanly on 2017/11/8 0024.
 */
Ext.define('Template.store.TemplateTreeStore',{
    extend:'Ext.data.TreeStore',
    model:'Template.model.TemplateTreeModel',
    proxy: {
        type: 'ajax',
        url: '/nodesetting/getNodeByParentId',
        extraParams:{pcid:'',type:'template'},
        reader: {
            type: 'json',
            expanded: true
        }
    },
    root: {
        text: '数据分类',
        expanded: true

    },
    listeners:{
        nodebeforeexpand:function(node, deep, animal) {
            // if((node.raw)){
                this.proxy.extraParams.pcid = node.get('fnid');
                this.proxy.url = '/nodesetting/getNodeByParentId';
            // };
        }
    }
});