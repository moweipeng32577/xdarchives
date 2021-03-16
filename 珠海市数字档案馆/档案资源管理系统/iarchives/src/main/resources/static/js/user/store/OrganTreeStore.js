/**
 * Created by Administrator on 2020/7/27.
 */


Ext.define('User.store.OrganTreeStore', {
    extend: 'Ext.data.TreeStore',
    model: 'User.model.OrganTreeModel',
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
        expanded: true,
        fnid: '0'
    },
    listeners: {
        nodebeforeexpand: function (node) {
            if (node.get('fnid')) {
                this.proxy.extraParams.pcid = node.get('fnid');
                this.proxy.url = '/nodesetting/getOrganByParentId';
            }
        }
    }
});
