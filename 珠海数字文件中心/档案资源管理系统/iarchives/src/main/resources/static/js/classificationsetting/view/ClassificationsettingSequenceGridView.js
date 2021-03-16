/**
 * Created by Administrator on 2018/9/12.
 */

Ext.define('Classificationsetting.view.ClassificationsettingSequenceGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype:'classificationsettingSequenceGridView',
    itemId:'classificationsettingSequenceGridViewID',
    store: 'ClassificationsettingSequenceStore',
    columns: [
        {text: '分类名称', dataIndex: 'classname', flex: 2, menuDisabled: true},
        {text: '分类编码', dataIndex: 'code', flex: 2, menuDisabled: true},
        {text: '分类类型', dataIndex: 'classlevel', flex: 2, menuDisabled: true}
    ],
    hasSearchBar:false
});

