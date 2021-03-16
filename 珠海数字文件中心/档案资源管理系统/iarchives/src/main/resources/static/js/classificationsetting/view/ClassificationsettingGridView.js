/**
 * Created by tanly on 2017/11/1 0024.
 */
Ext.define('Classificationsetting.view.ClassificationsettingGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'classificationsettingGridView',
    region: 'center',
    itemId: 'classChangeViewID',
    allowDrag: true,
    searchstore: [{item: "classname", name: "分类名称"}, {item: "code", name: "分类编码"}],
    tbar: [{
        xtype: 'button',
        text: '增加',
        iconCls:'fa fa-plus-circle',
        itemId: 'addclassbtnid'
    }, '-', {
        xtype: 'button',
        text: '修改',
        iconCls:'fa fa-pencil-square-o',
        itemId: 'updateclassbtnid'
    }, '-', {
        xtype: 'button',
        text: '删除',
        iconCls:' fa fa-trash-o',
        itemId: 'deleteclassbtnid'
    }, '-', {
        xtype: 'button',
        text: '调序',
        iconCls:' fa fa-bars',
        itemId: 'sequence'
    },
        '-', {
            xtype: 'button',
            text: '是否为声像',
            iconCls:' fa fa-bars',
            itemId: 'isMedia'
        }],
    store: 'ClassificationsettingGridStore',
    columns: [
        {text: '分类名称', dataIndex: 'classname', flex: 2, menuDisabled: true},
        {text: '分类编码', dataIndex: 'code', flex: 2, menuDisabled: true},
        {text: '分类类型', dataIndex: 'classlevel', flex: 2, menuDisabled: true},
        {text:'是否为声像档案',dataIndex:'isMedia',flex: 2, menuDisabled: true}
    ]
});