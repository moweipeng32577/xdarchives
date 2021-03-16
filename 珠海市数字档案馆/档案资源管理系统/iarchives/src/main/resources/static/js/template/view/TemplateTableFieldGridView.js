Ext.define('Template.view.TemplateTableFieldGridView',{
    extend: 'Comps.view.BasicGridView',
    xtype:'templateTableFieldGridView',
    store:'TableFieldStore',
    hasSearchBar:false,
    hasCloseButton: false,
    hasCancelButton: false,
    hasCheckColumn: false,
    hasPageBar: false,
    hasRownumber: false,
    columns: [
        {text: '字段名称', dataIndex: 'fieldname', flex: 2, menuDisabled: true},
        {text: '字段编码', dataIndex: 'fieldcode', flex: 2, menuDisabled: true},
        {text: '所在表', dataIndex:'fieldtable',flex:2,menuDisabled: true},
        {text: '是否只读',dataIndex:'freadonly',flex:1,menuDisabled: true, renderer: function (value, cellmeta, record) {
                if(value == true){
                    return '是'
                }else{
                    return '否'
                }
            }},
        {text: '是否必填',dataIndex:'frequired',flex:1,menuDisabled: true,renderer:function (value, cellmeta, record) {
                if(value == true){
                    return '是'
                }else{
                    return '否'
                }
            }}
        // {text: '内容', dataIndex: 'content', flex: 4, menuDisabled: true,renderer: function(value, cellmeta, record) {
        //         var reTag = /<(?:.|\s)*?>/g;
        //         return value.replace(reTag,"");
        //     } },
        // {text: '发布机构', dataIndex: 'organ', flex: 1, menuDisabled: true},
        // {text: '发布时间', dataIndex: 'publishtime', flex: 1, menuDisabled: true},
        // {
        //     text: '发布状态', dataIndex: 'publishstate', flex: 1, menuDisabled: true,
        //     renderer: function (value, cellmeta, record) {
        //         if (value == '1') {
        //             return '已发布';
        //         } else {
        //             return '未发布';
        //         }
        //     }
        // },
        // {text: '置顶等级', dataIndex: 'stick', flex: 1, menuDisabled: true}
    ]

})