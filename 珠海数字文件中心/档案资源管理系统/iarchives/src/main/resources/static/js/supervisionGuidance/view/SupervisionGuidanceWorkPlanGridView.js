/**
 * Created by Administrator on 2020/9/28.
 */


Ext.define('SupervisionGuidance.view.SupervisionGuidanceWorkPlanGridView',{
    extend: 'Comps.view.BasicGridView',
    xtype:'supervisionGuidanceWorkPlanGridView',
    title: '工作计划 <button style="float:right;margin-right: 10px" onclick="workPlanDelete()">删除</button><button style="float:right;margin-right: 20px" onclick="workPlanAdd()">新增</button>',
    itemId:'supervisionGuidanceWorkPlanGridViewId',
    hasSearchBar:false,//无需基础表格组件中的检索栏
    hasPageBar:false,
    store: 'SupervisionGuidanceWorkPlanGridStore',
    stripeRows:true, //斑马线效果
    selModel: 'cellmodel',
    plugins: {
        ptype: 'cellediting',//编辑单元格插件
        clicksToEdit: 1//单击编辑
    },
    columns: [
        {text: '年度', dataIndex: 'selectyear', flex: 1, menuDisabled: true,editor: 'textfield'},
        {text: '是否纳入年度计划', dataIndex: 'isyearplan', flex: 1, menuDisabled: true,editor: 'textfield'},
        {text: '是否纳入年度总结', dataIndex: 'isyearconclusion', flex: 1, menuDisabled: true,editor: 'textfield'},
        {text: '是否纳入目标考核', dataIndex: 'isyearaduit', flex: 1, menuDisabled: true,editor: 'textfield'},
        {text: '附件', dataIndex: 'attachment', flex: 1, menuDisabled: true,editor: 'textfield'}
    ]
});


function workPlanAdd() {  //新增
    var grid = window.supervisionGuidanceWorkPlanGridView;
    var supervisionGuidanceView = grid.findParentByType('supervisionGuidanceView');
    var selectYear = supervisionGuidanceView.down('[itemId=selectYearId]').getValue();
    var num = grid.getStore().getCount();
    var p ={
        selectyear:selectYear,
        isyearplan:'',
        isyearconclusion:'',
        isyearaduit:'',
        attachment:''
    };
    grid.getStore().insert(num,p);
}

function workPlanDelete() {   //删除
    var grid = window.supervisionGuidanceWorkPlanGridView;
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
                type:'workplan'
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
