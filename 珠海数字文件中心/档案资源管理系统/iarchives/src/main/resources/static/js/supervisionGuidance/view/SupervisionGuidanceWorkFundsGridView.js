/**
 * Created by Administrator on 2020/9/28.
 */

Ext.define('SupervisionGuidance.view.SupervisionGuidanceWorkFundsGridView',{
    extend: 'Comps.view.BasicGridView',
    xtype:'supervisionGuidanceWorkFundsGridView',
    title: '工作经费 <button style="float:right;margin-right: 10px" onclick="workFundsDelete()">删除</button><button style="float:right;margin-right: 20px" onclick="workFundsAdd()">新增</button>',
    itemId:'supervisionGuidanceWorkFundsGridViewId',
    hasSearchBar:false,//无需基础表格组件中的检索栏
    hasPageBar:false,
    store: 'SupervisionGuidanceWorkFundsGridStore',
    stripeRows:true, //斑马线效果
    selModel: 'cellmodel',
    plugins: {
        ptype: 'cellediting',//编辑单元格插件
        clicksToEdit: 1//单击编辑
    },
    columns: [
        {text: '年度', dataIndex: 'selectyear', flex: 1, menuDisabled: true,editor: 'textfield'},
        {text: '档案经费（万元）', dataIndex: 'archivesfunds', flex: 1, menuDisabled: true,editor: 'textfield'},
        {text: '落实情况', dataIndex: 'situatuion', flex: 1, menuDisabled: true,editor: 'textfield'},
        {text: '备注', dataIndex: 'remark', flex: 1, menuDisabled: true,editor: 'textfield'}
    ]
});


function workFundsAdd() {  //新增
    var grid = window.supervisionGuidanceWorkFundsGridView;
    var supervisionGuidanceView = grid.findParentByType('supervisionGuidanceView');
    var selectYear = supervisionGuidanceView.down('[itemId=selectYearId]').getValue();
    var num = grid.getStore().getCount();
    var p ={
        selectyear:selectYear,
        archivesfunds:'',
        situatuion:'',
        remark:''
    };
    grid.getStore().insert(num,p);
}

function workFundsDelete() {   //删除
    var grid = window.supervisionGuidanceWorkFundsGridView;
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
                type:'workfunds'
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
