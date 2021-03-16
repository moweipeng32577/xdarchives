/**
 * Created by Administrator on 2018/9/12.
 */

Ext.define('Organ.view.OrganSequenceGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype:'organSequenceGridView',
    itemId:'organSequenceGridViewID',
    store: 'OrganSequenceStore',
    columns: [
        {text: '机构名称', dataIndex: 'organname', flex: 2, menuDisabled: true},
        {text: '机构分类号', dataIndex: 'code', flex: 2, menuDisabled: true},
        {text: '服务名称', dataIndex: 'servicesname', flex: 2, menuDisabled: true},
        {text: '系统名称', dataIndex: 'systemname', flex: 2, menuDisabled: true},
        {text: '机构类型', dataIndex: 'organtype', flex: 1, menuDisabled: true},
        {text: '状态', dataIndex: 'status', flex: 1, menuDisabled: true},
        {text: '机构编码', dataIndex: 'refid', flex: 2, menuDisabled: true},
        {text: '备注', dataIndex: 'desciption', flex: 2, menuDisabled: true}
    ],
    hasSearchBar:false
});

