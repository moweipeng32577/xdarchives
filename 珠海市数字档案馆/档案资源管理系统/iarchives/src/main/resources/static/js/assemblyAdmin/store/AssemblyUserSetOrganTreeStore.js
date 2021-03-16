/**
 * Created by Administrator on 2019/7/3.
 */


Ext.define('AssemblyAdmin.store.AssemblyUserSetOrganTreeStore', {
    extend: 'Ext.data.TreeStore',
    model: 'AssemblyAdmin.model.AssemblyUserSetOrganTreeModel',
    proxy: {
        type: 'ajax',
        url: '/nodesetting/getAssemblyOrganByParentId',//直接引用nodesetting的方法
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
                this.proxy.url = '/nodesetting/getAssemblyOrganByParentId';
            }
        }
    }
});