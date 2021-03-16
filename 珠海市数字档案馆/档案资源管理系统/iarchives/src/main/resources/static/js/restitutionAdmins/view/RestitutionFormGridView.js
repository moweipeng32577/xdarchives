/**
 * Created by yl on 2017/11/3.
 */
Ext.define('Restitution.view.RestitutionFormGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'restitutionFormGridView',
    itemId: 'restitutionFormGridViewId',
    region: 'south',
    title:'条目',
    height: '49%',
    // hasCheckColumn:false,
    autoScroll: true,
    store: 'RestitutionFormGridStore',
    tbar: [{
        itemId:'importId',
        xtype: 'button',
        text: '导入条目',
        iconCls:'fa fa-share'
    },'-',{
        itemId:'removeId',
        xtype: 'button',
        text: '移除',
        iconCls:'fa fa-trash-o'
    }],
    columns: [
        {text: '题名', dataIndex: 'title', flex: 2, menuDisabled: true},
        {text: '文件编号', dataIndex: 'filenumber', flex: 2, menuDisabled: true},
        {text: '档号', dataIndex: 'archivecode', flex: 2, menuDisabled: true},
        {text: '全宗号', dataIndex: 'funds', flex: 2, menuDisabled: true},
        {text: '目录号', dataIndex: 'catalog', flex: 2, menuDisabled: true}
    ],
    hasSearchBar: false
});