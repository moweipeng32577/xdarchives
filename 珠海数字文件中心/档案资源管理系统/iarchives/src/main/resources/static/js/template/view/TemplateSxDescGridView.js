/**
 * Created by yl on 2021-01-20.
 */
/**
 * Created by zengdw on 2019/10/31 0001.
 */

Ext.define('Template.view.TemplateSxDescGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'templateSxDescGridView',
    region: 'center',
    itemId: 'templateSxDescGridViewID',
    searchstore: [{item: "fieldcode", name: "字段编码"}, {item: "descs", name: "字段描述"}],
    tbar: {
        items:[{
            xtype: 'button',
            text: '更新所有字段的描述',
            iconCls:'fa fa-columns',
            itemId: 'updateAlltnid'
        },'-',{
            xtype: 'button',
            text: '返回',
            iconCls:'fa fa-undo',
            itemId: 'backtnid'
        }]
    },
    store: 'TemplateSxDescGridStore',
    columns: [
        {text: '字段编码', dataIndex: 'fieldcode', flex: 1, menuDisabled: true},
        {text: '字段描述', dataIndex: 'descs', flex: 9, menuDisabled: true}
    ]
});