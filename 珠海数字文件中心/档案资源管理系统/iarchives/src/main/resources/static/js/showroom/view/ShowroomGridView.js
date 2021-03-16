/**
 * Created by zdw on 2020/03/20
 */
Ext.define('Showroom.view.ShowroomGridView',{
    extend: 'Comps.view.BasicGridView',
    xtype:'showroomGridView',
    itemId:'showroomGridViewID',
    hasCloseButton:false,
    bodyBorder: false,
    store: 'ShowroomGridStore',
    searchstore:[
        {item: "title", name: "展厅名称"},
        {item: "flag", name: "展厅状态"},
        {item: "content", name: "展厅介绍"}
    ],
    tbar: [{
        itemId:'showroomAdd',
        xtype: 'button',
        text: '增加',
        iconCls:'fa fa-plus-circle'
    },'-',{
        itemId:'showroomEdit',
        xtype: 'button',
        text: '修改',
        iconCls:'fa fa-pencil-square-o'
    },'-',{
        itemId:'showroomDel',
        xtype: 'button',
        text: '删除',
        iconCls:'fa fa-trash-o'
    }/*,'-',{
        itemId:'showroomReply',
        xtype: 'button',
        text: '设置参观人数',
        iconCls:'fa fa-pencil-square-o'
    }*/],
    columns: [
        {text: '展厅名称', dataIndex: 'title', flex: 2, menuDisabled: true},
        {text: '展厅介绍', dataIndex: 'content', flex: 4, menuDisabled: true,renderer: function(value, cellmeta, record) {
            var reTag = /<(?:.|\s)*?>/g;
            return value.replace(reTag,"");
        } },
        {text: '展厅附件', dataIndex: 'appendix', flex:2, menuDisabled: true},
        {text: '展厅状态', dataIndex: 'flag', flex: 1, menuDisabled: true,renderer: function(value, cellmeta, record) {
            if(value.indexOf('1') > -1){
                return "<span style=\"color:blue\">已满</span>"
            }else if(value.indexOf('0') > -1){
                return "<span style=\"color:green\">正常</span>"
            }else{
                return "<span style=\"color:red\">维护中</span>"
            }
        }},
        {text: '每日参观人数', dataIndex: 'audiences', flex:1, menuDisabled: true}
    ]
});