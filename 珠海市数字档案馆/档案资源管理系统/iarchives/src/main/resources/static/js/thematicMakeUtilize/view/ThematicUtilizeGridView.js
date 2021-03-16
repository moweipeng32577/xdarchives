/**
 * Created by yl on 2017/10/27.
 */
Ext.define('ThematicUtilize.view.ThematicUtilizeGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'thematicUtilizeGridView',
    region: 'center',
    searchstore:[{item: "title", name: "专题名称"}],
    tbar: [{
        xtype: 'button',
        text: '下载',
        iconCls:'fa fa-download',
        itemId:'download'
    }],
    store: 'ThematicUtilizeGridStore',
    columns: [
        {text: '成果名称', dataIndex: 'title', width:300, menuDisabled: true},
        {text: '专题大小', dataIndex: 'filesize', width:300, menuDisabled: true},
        {text: '备注', dataIndex: 'content', width:600, menuDisabled: true}
    ]
});
