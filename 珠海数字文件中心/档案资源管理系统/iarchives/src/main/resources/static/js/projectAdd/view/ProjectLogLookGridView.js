/**
 * Created by Administrator on 2020/7/21.
 */
Ext.define('ProjectAdd.view.ProjectLogLookGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype:'projectLogLookGridView',
    title: '业务日志',
    hasSearchBar:false,
    hasCloseButton:false,
    hasPageBar:false,
    hasCheckColumn:false,
    region: 'south',
    height: '45%',
    store: 'ProjectLogLookGridStore',
    columns: [
        {text: '操作人', dataIndex: 'realname', flex: 1, menuDisabled: true},
        {text: '操作时间', dataIndex: 'startTime', flex: 1, menuDisabled: true},
        {text: '描述', dataIndex: 'desci', flex: 2, menuDisabled: true}
    ]
});
