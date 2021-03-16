/**
 * Created by yl on 2017/11/13.
 */
Ext.define('Appraisal.store.AppraisalTreeStore',{
    extend:'Ext.data.TreeStore',
    model:'Appraisal.model.AppraisalTreeModel',
    proxy: {
        type: 'ajax',
        url: '/nodesetting/getNodeByParentId',
        extraParams:{
            pcid:'',
            type: '到期鉴定'
        },
        timeout:XD.timeout,
        reader: {
            type: 'json',
            expanded: true
        }
    },
    root: {
        text: '分类检索',
        expanded: true
    },
    listeners:{
        nodebeforeexpand:function(node, deep, animal) {
            if((node.raw)){
                this.proxy.extraParams.pcid = node.raw.fnid;
                this.proxy.extraParams.type = '到期鉴定';
            }
        }
    }
});