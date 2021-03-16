/**
 * Created by tanly on 2017/11/1 0024.
 */
Ext.define('Classificationsetting.store.ClassificationsettingTreeStore',{
    extend:'Ext.data.TreeStore',
    model:'Classificationsetting.model.ClassificationsettingTreeModel',
    proxy: {
        type: 'ajax',
        url: '/nodesetting/getClassificationByParentClassId',//直接引用nodesetting的方法
        extraParams:{pcid:''},
        reader: {
            type: 'json',
            expanded: true
        }
    },
    root: {
        text: '分类设置',
        expanded: true,
        fnid:''
    },
    listeners:{
        nodebeforeexpand:function(node) {
            if(node.get('fnid')){
                this.proxy.extraParams.pcid = node.get('fnid');
                this.proxy.extraParams.xtType =window.classViewTab;
                this.proxy.url = '/nodesetting/getClassificationByParentClassId';
            }
        }
    }
});