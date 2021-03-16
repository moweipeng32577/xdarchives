/**
 * Created by Administrator on 2018/12/3.
 */

Ext.define('CheckGroup.store.CheckGroupOrganTreeStore', {
    extend: 'Ext.data.TreeStore',
    model: 'CheckGroup.model.CheckGroupOrganTreeModel',
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
