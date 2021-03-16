/**
 * Created by Administrator on 2019/12/14.
 */


Ext.define('UserGroup.store.OrganTreeStore', {
    extend: 'Ext.data.TreeStore',
    model: 'UserGroup.model.OrganTreeModel',
    proxy: {
        type: 'ajax',
        url: '/nodesetting/getOrganByParentId',//直接引用nodesetting的方法
        extraParams: {pcid: '0'},
        reader: {
            type: 'json',
            expanded: true
        }
    },
    root: {
        text: '机构设置',
        expanded: true
    },
    listeners: {
        nodebeforeexpand: function (node) {
            if (node.get('fnid')) {
                this.proxy.extraParams.pcid = node.get('fnid');
            }
        }
    }
});
