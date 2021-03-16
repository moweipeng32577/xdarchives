/**
 * Created by Administrator on 2020/7/8.
 */

Ext.define('SupervisionGuidance.view.SupervisionGuidanceLeaderGridView',{
    extend: 'Comps.view.BasicGridView',
    xtype:'supervisionGuidanceLeaderGridView',
    title: '分管领导 <button style="float:right;margin-right: 10px" onclick="leaderDelete()">删除</button><button style="float:right;margin-right: 20px" onclick="leaderAdd()">新增</button>',
    itemId:'supervisionGuidanceLeaderGridViewId',
    hasSearchBar:false,//无需基础表格组件中的检索栏
    hasPageBar:false,
    store: 'SupervisionGuidanceLeaderGridStore',
    stripeRows:true, //斑马线效果
    selModel: 'cellmodel',
    plugins: {
        ptype: 'cellediting',//编辑单元格插件
        clicksToEdit: 1//单击编辑
    },
    columns: [
        {text: '姓名', dataIndex: 'username', flex: 1, menuDisabled: true,editor: 'textfield'},
        {text: '职务', dataIndex: 'post', flex: 1, menuDisabled: true,editor: 'textfield'},
        {text: '政治面貌', dataIndex: 'politicstate', flex: 1, menuDisabled: true,editor: 'textfield'},
        {text: '开始分管时间', dataIndex: 'starttime', flex: 1, menuDisabled: true,editor: 'textfield'},
        {text: '联系电话', dataIndex: 'mobilephone', flex: 1, menuDisabled: true,editor: 'textfield'}
    ]
});


function leaderAdd() {  //新增
    var grid = window.supervisionGuidanceLeaderGridView;
    var num = grid.getStore().getCount();
    if(num>0){
        XD.msg('分管领导已存在');
        return;
    }
    var p ={
        username:'',
        post:'',
        politicstate:'',
        starttime:'',
        mobilephone:''
    };
    grid.getStore().insert(num,p);
}

function leaderDelete() {   //删除
    var grid = window.supervisionGuidanceLeaderGridView;
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
                type:'leader'
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
