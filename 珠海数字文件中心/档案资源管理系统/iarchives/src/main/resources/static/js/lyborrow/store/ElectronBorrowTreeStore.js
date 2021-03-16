/**
 * Created by xd on 2017/10/21.
 */
Ext.define('Borrow.store.ElectronBorrowTreeStore',{
    extend:'Ext.data.TreeStore',
    model:'Borrow.model.ElectronBorrowTreeModel',
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
        text: '单位',
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
