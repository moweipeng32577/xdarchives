/**
 * Created by yl on 2017/10/27.
 */
Ext.define('ThematicProd.view.ThematicProdGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'thematicProdGridView',
    searchstore:[{item: "title", name: "专题名称"},{item: "thematiccontent", name: "专题描述"},{item:"thematictypes",name:"专题类型"},{item: "publishstate", name: "发布状态"}],
    tbar:{
        // [
    //     {
    //     xtype: 'button',
    //     itemId:'thematicAddBtnID',
    //     iconCls:'fa fa-plus',
    //     text: '增加'
    // }, '-', {
    //     xtype: 'button',
    //     itemId:'thematicDeleteBtnID',
    //     iconCls:' fa fa-trash-o',
    //     text: '删除'
    // }, '-', {
    //     xtype: 'button',
    //     itemId:'thematicUpdateBtnID',
    //     iconCls:'fa fa-pencil-square-o',
    //     text: '修改'
    // }, '-', {
    //     xtype: 'button',
    //     itemId:'thematicSeeBtnID',
    //     iconCls:'fa fa-eye',
    //     text: '查看'
    // }, '-', {
    //     xtype: 'button',
    //     itemId:'compilation',
    //     iconCls:'fa fa-info-circle',
    //     text: '信息编研'
    // }, '-', {
    //     xtype: 'button',
    //     itemId:'releaseBtnID',
    //     iconCls:'fa fa-check-square',
    //     text: '发布'
    // }, '-', {
    //     xtype: 'button',
    //     itemId:'cancleReleaseBtnID',
    //     iconCls:'fa fa-minus-square',
    //     text: '取消发布'
    // }, '-', {
    //     xtype: 'button',
    //     itemId:'releaseResourceId',
    //     iconCls:'fa fa-check-square',
    //     text: '发布数字资源'
    // }, '-', {
    //     xtype: 'button',
    //     itemId:'releasenetwork',
    //     iconCls:'fa fa-check-square',
    //     text: '发布政务网'
    // }
    // ],
        items:functionbtn},
    store: 'ThematicProdGridStore',
    columns: [
        {text: '专题名称', dataIndex: 'title', flex: 0.45, menuDisabled: true},
        {text: '专题描述', dataIndex: 'thematiccontent', flex: 0.45, menuDisabled: true},
        {text: '专题类型', dataIndex: 'thematictypes', flex: 0.1, menuDisabled: true},
        {text: '发布状态', dataIndex: 'publishstate', flex: 0.1, menuDisabled: true}
    ]
});