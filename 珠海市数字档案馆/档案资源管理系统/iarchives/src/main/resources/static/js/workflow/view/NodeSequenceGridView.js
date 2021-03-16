/**
 * Created by luzc on 2020/6/16.
 */

Ext.define('Workflow.view.NodeSequenceGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype:'nodeSequenceGridView',
    itemId:'nodeSequenceGridViewID',
    store: 'NodeSequenceStore',
    columns: [
        {text: '节点名称', dataIndex: 'text', flex: 2, menuDisabled: true},
        {text: '节点描述', dataIndex: 'desci', flex: 2, menuDisabled: true},
        {text: '下一节点', dataIndex: 'nexttext', flex: 2, menuDisabled: true},
        {text: '节点顺序', dataIndex: 'orders', flex: 2, menuDisabled: true}
    ],
    hasSearchBar:false
});