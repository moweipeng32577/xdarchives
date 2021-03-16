Ext.define('Notice.view.NoticeGridView',{
    extend: 'Comps.view.BasicGridView',
    xtype: 'noticeGridView',
    itemId: 'noticeGridViewID',
    bodyBorder: false,
    store: 'NoticeStore',
    hasCloseButton: false,
    head: false,
    searchstore: [
        {item: "title", name: "标题"},
        {item: "content", name: "内容"},
        {item: "publishtime", name: "发布时间"}
    ],
    tbar: functionButton,
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