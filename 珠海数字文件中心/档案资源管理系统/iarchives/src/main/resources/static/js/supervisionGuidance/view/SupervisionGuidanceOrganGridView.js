/**
 * Created by Administrator on 2020/9/28.
 */


Ext.define('SupervisionGuidance.view.SupervisionGuidanceOrganGridView',{
    extend: 'Comps.view.BasicGridView',
    xtype:'supervisionGuidanceOrganGridView',
    title: '档案机构 <button style="float:right;margin-right: 10px" onclick="organDelete()">删除</button><button style="float:right;margin-right: 20px" onclick="organAdd()">新增</button>',
    itemId:'supervisionGuidanceOrganGridViewId',
    hasSearchBar:false,//无需基础表格组件中的检索栏
    hasPageBar:false,
    store: 'SupervisionGuidanceOrganGridStore',
    stripeRows:true, //斑马线效果
    selModel: 'cellmodel',
    plugins: {
        ptype: 'cellediting',//编辑单元格插件
        clicksToEdit: 1//单击编辑
    },
    columns: [
        {text: '机构名称', dataIndex: 'organname', flex: 1, menuDisabled: true,editor: 'textfield'},
        {text: '级别', dataIndex: 'classtype', flex: 1, menuDisabled: true,editor: 'textfield'},
        {text: '是否独立', dataIndex: 'isindependent', flex: 1, menuDisabled: true,editor: 'textfield'},
        {text: '归口部门', dataIndex: 'underdepartment', flex: 1, menuDisabled: true,editor: 'textfield'},
        {text: '部门负责人姓名', dataIndex: 'username', flex: 1, menuDisabled: true,editor: 'textfield'},
        {text: '职务', dataIndex: 'post', flex: 1, menuDisabled: true,editor: 'textfield'},
        {text: '政治面貌', dataIndex: 'politicstate', flex: 1, menuDisabled: true,editor: 'textfield'},
        {text: '联系电话', dataIndex: 'mobilephone', flex: 1, menuDisabled: true,editor: 'textfield'},
        {text: '专职档案员数（单位：人）', dataIndex: 'fulltimenum', flex: 2, menuDisabled: true,editor: 'textfield'},
        {text: '兼职档案员数（单位：人）', dataIndex: 'parttimenum', flex: 2, menuDisabled: true,editor: 'textfield'}
    ]
});


function organAdd() {  //新增
    var grid = window.supervisionGuidanceOrganGridView;
    var num = grid.getStore().getCount();
    var p ={
        organname:'',
        classtype:'',
        isindependent:'',
        underdepartment:'',
        username:'',
        post:'',
        politicstate:'',
        mobilephone:'',
        fulltimenum:'',
        parttimenum:''
    };
    grid.getStore().insert(num,p);
}

function organDelete() {   //删除
    var grid = window.supervisionGuidanceOrganGridView;
    var select = grid.getSelectionModel().getSelection();
    if(select.length < 1){
        XD.msg('请至少选择一条数据');
        return;
    }
    var ids = [];
    for(var i=0;i<select.length;i++){
        ids.push(select[i].get("id"));
    }
    XD.confirm("是否要删除这 "+select.length+" 条数据？",function () {
        Ext.Ajax.request({
            method:'POST',
            params: {
                ids:ids,
                type:'organ'
            },
            url:'/supervisionGuidance/deleteSuperGuidanceByType',
            success:function(response){
                XD.msg('删除成功');
                grid.getStore().reload();
            },
            failure:function(){
                XD.msg('操作失败');
            }
        });
    });
}