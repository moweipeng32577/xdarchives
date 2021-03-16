/**
 * Created by yl on 2017/10/27.
 */
Ext.define('ThematicProd.view.ThematicBackGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'thematicBackGridView',
    // searchstore:[{item: "title", name: "专题名称"},{item: "thematiccontent", name: "专题描述"},{item:"thematictypes",name:"专题类型"},{item: "publishstate", name: "发布状态"}],
    hasSearchBar:false,
    hasCloseButton:false,
    hasPageBar:false,
    hasCheckColumn:false,
    store: 'ThematicBackGridStore',
    columns: [
        {text: '专题名称', dataIndex: 'title', flex: 0.45, menuDisabled: true},
        {text: '专题描述', dataIndex: 'thematiccontent', flex: 0.45, menuDisabled: true},
        {text: '专题类型', dataIndex: 'thematictypes', flex: 0.1, menuDisabled: true},
        {text: '发布状态', dataIndex: 'publishstate', flex: 0.1, menuDisabled: true},
        {text: '意见', dataIndex: 'approvetext', flex: 0.45, menuDisabled: true}
    ]
});