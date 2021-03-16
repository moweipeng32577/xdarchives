/**
 * Created by tanly on 2018/9/17 0024.
 */
Ext.define('User.store.UserOrganTreeStore', {
    extend: 'Ext.data.TreeStore',
    model: 'User.model.UserOrganTreeModel',
    proxy: {
        type: 'ajax',
        url: '/nodesetting/getOrganByParentId',
        extraParams: {pcid: '0'},
        reader: {
            type: 'json',
            expanded: true
        }
    },
    root: {
        text: '机构选择',
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