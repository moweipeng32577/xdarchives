/**
 * Created by Administrator on 2018/11/8.
 */

Ext.define('Thematicelectronic.view.ThematicDetailGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'thematicDetailGridView',
    hasSearchBar:false,
    head:false,
    tbar: [{
        xtype: 'button',
        itemId:'seeBtnID',
        iconCls:'fa fa-eye',
        text: '查看'
    }],
    store: 'ThematicDetailGridStore',
    columns: [
        {text: '题名', dataIndex: 'title', flex: 0.3, menuDisabled: true},
        {text: '时间', dataIndex: 'filedate', flex: 0.1, menuDisabled: true},
        {text: '责任者', dataIndex: 'responsibleperson',flex: 0.1, menuDisabled: true},
        {text: '文件编号', dataIndex: 'filecode', flex: 0.1, menuDisabled: true},
        {text: '主题词', dataIndex: 'subheadings', flex: 0.2, menuDisabled: true},
        {text: '电子文件', dataIndex: 'mediatext',flex: 0.2, menuDisabled: true}
    ]
});
