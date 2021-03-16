/**
 * Created by tanly on 2017/11/1 0024.
 */
Ext.define('SystemConfig.view.SystemConfigSxGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'systemConfigSxGridView',
    region: 'center',
    itemId: 'systemConfigSxGridViewID',
    allowDrag: true, //允许拖拉
    searchstore: [{item: "configcode", name: "参数名称"}, {item: "configvalue", name: "参数值"}],
    tbar: [{
        xtype: 'button',
        text: '增加',
        iconCls:'fa fa-plus-circle',
        itemId: 'add'
    }, '-', {
        xtype: 'button',
        text: '修改',
        iconCls:'fa fa-pencil-square-o',
        itemId: 'update'
    }, '-', {
        xtype: 'button',
        text: '删除',
        iconCls:'fa fa-trash-o',
        itemId: 'delete'
    },
        {
            iconCls: '',
            itemId: "exportID",
            menu: [
                {
                    text: '导出XLS',
                    itemId: 'exportXLS',
                    iconCls: 'fa fa-download'
                },{
                    text:'导出xlsx',
                    itemId:'exportXLSX',
                    iconCls:'fa fa-download'
                }, {
                    text: '导入参数',
                    itemId: 'import',
                    iconCls: 'fa fa-floppy-o'
                }
            ],
            text: '导入导出'
        }],
    store: 'SystemConfigGridStore',
    columns: [
        {text: '参数名称', dataIndex: 'configcode', flex: 2, menuDisabled: true},
        {text: '参数值', dataIndex: 'configvalue', flex: 2, menuDisabled: true},
        {text: '排序', dataIndex: 'sequence', flex: 2, menuDisabled: true}
    ]
});