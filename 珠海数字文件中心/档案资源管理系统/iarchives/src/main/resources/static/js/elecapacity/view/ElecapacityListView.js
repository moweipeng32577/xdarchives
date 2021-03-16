/**
 * Created by xd on 2017/10/21.
 */
Ext.define('Elecapacity.view.ElecapacityListView',{
    extend:'Comps.view.BasicGridView',
    xtype:'elecapacityListView',
    title: '',
    region: 'center',
    itemId:'elecapacityListViewID',
    hasSearchBar:false,
    tbar: [],
    store: 'ElecapacityListStore',
    columns: [
        {text: '部门', dataIndex: 'organ', flex: 4, menuDisabled: true},
        {text: '全宗', dataIndex: 'funds', flex: 2, menuDisabled: true},
        {text: '文件数量', dataIndex: 'count', flex: 2, menuDisabled: true},
        {text: '已用空间', dataIndex: 'size', flex: 2, menuDisabled: true}
    ]
});
