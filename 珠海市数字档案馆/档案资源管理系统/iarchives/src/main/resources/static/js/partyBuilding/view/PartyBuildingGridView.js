Ext.define('PartyBuilding.view.PartyBuildingGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'partyBuildingGridView',
    itemId: 'partyBuildingGridViewID',
    bodyBorder: false,
    store: 'PartyBuildingGridStore',
    hasCloseButton: false,
    head: false,
    searchstore: [
        {item: "title", name: "标题"},
        {item: "content", name: "内容"},
        {item: "publishtime", name: "发布时间"}
    ],
    tbar: functionButton,
    //     [{
    //     itemId: 'add',
    //     xtype: 'button',
    //     iconCls: 'fa fa-plus-circle',
    //     text: '增加'
    // }, '-', {
    //     itemId: 'edit',
    //     xtype: 'button',
    //     iconCls: 'fa fa-pencil-square-o',
    //     text: '修改'
    // }, '-', {
    //     itemId: 'delete',
    //     xtype: 'button',
    //     iconCls: 'fa fa-trash-o',
    //     text: '删除'
    // }, '-', {
    //     itemId: 'look',
    //     xtype: 'button',
    //     iconCls: 'fa fa-eye',
    //     text: '查看'
    // }, '-', {
    //     itemId: 'publishBtnID',
    //     xtype: 'button',
    //     iconCls: 'fa fa-check-square',
    //     text: '发布'
    // }, '-', {
    //     itemId: 'canclePublishBtnID',
    //     xtype: 'button',
    //     iconCls: 'fa fa-minus-square',
    //     text: '取消发布'
    // }, '-', {
    //     itemId: 'stick',
    //     xtype: 'button',
    //     iconCls: 'fa fa-upload',
    //     text: '置顶'
    // }, '-', {
    //     itemId: 'cancelStick',
    //     xtype: 'button',
    //     iconCls: 'fa fa-download',
    //     text: '取消置顶'
    // }],
    columns: [
        {text: '标题', dataIndex: 'title', flex: 2, menuDisabled: true},
        {text: '内容', dataIndex: 'content', flex: 4, menuDisabled: true,renderer: function(value, cellmeta, record) {
                var reTag = /<(?:.|\s)*?>/g;
                return value.replace(reTag,"");
            } },
        {text: '发布机构', dataIndex: 'organ', flex: 1, menuDisabled: true},
        {text: '发布时间', dataIndex: 'publishtime', flex: 1, menuDisabled: true},
        {
            text: '发布状态', dataIndex: 'publishstate', flex: 1, menuDisabled: true,
            renderer: function (value, cellmeta, record) {
                if (value == '1') {
                    return '已发布';
                } else {
                    return '未发布';
                }
            }
        },
        {text: '置顶等级', dataIndex: 'stick', flex: 1, menuDisabled: true}
    ]
});