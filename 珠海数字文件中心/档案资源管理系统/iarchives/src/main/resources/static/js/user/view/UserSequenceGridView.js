/**
 * Created by Administrator on 2018/9/12.
 */

Ext.define('User.view.UserSequenceGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype:'userSequenceGridView',
    itemId:'userSequenceGridViewID',
    store: 'UserSequenceStore',
    columns: [
        {text: '帐号', dataIndex: 'loginname', flex: 3, menuDisabled: true},
        {text: '姓名', dataIndex: 'realname', flex: 2, menuDisabled: true},
        {text: '性别', dataIndex: 'sex', flex: 1, menuDisabled: true},
        {
            text: '所属机构',
            dataIndex: 'organ',
            flex: 2,
            menuDisabled: true,
            renderer: function (value) {
                return value['organname'];
            }
        },
        {text: '状态', dataIndex: 'status', flex: 2, menuDisabled: true},
        {text: '创建时间', dataIndex: 'createtime', flex: 2, menuDisabled: true}
    ],
    hasSearchBar:false
});