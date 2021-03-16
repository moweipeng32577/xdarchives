/**
 * Created by SunK on 2018/10/26 0026.
 */
Ext.define('Restitution.view.RestitutionUpdateGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'restitutionUpdateGridView',
    itemId: 'restitutionUpdateGridView',
    region: 'south',
    title:'条目',
    height: '49%',
    // hasCheckColumn:false,
    autoScroll: true,
    store: 'RestitutionFormUpdateGridStore',
    tbar: [{
     itemId:'UpdateAddId',
     xtype: 'button',
     text: '添加条目',
     iconCls:'fa fa-share'
     },'-',{
     itemId:'UpdateRemoveId',
     xtype: 'button',
     text: '移除条目',
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

