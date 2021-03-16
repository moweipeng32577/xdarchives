/**
 * Created by tanly on 2017/10/24 0024.
 */
Ext.define('Nodesetting.view.NodesettingGridView',{
    extend:'Comps.view.BasicGridView',
    xtype:'nodesettingGridView',
    region: 'center',
    itemId:'nodesettingGridViewID',
    searchstore:[{item: "nodename", name: "节点名称"}, {item: "nodecode", name: "节点编码"}],
    allowDrag:true,
    tbar: [
    //     {
    //     xtype: 'button',
    //     text: '增加机构节点',
    //     itemId:'addOrganBtn'
    // }, '-', {
    //     xtype: 'button',
    //     text: '增加分类节点',
    //     itemId:'addClassBtn'
    // }, '-', {
    //     xtype: 'button',
    //     text: '修改',
    //     itemId:'updateNodeBtnID'
    // }, '-', {
    //     xtype: 'button',
    //     text: '删除',
    //     itemId:'deleteNodeBtnID'
    // }
    /*, '-', {//暂时不开放此功能
        xtype: 'button',
        text: '初始化',
        itemId:'changeModelBtnID'
    }*/
    ],
    store: 'NodesettingGridStore',
    columns: [
        {text: '节点名称', dataIndex: 'nodename', flex: 2, menuDisabled: true},
        {text: '节点编码', dataIndex: 'nodecode', flex: 2, menuDisabled: true},
        {text: '叶子节点', dataIndex: 'leaf', flex: 2, menuDisabled: true}
    ]
});