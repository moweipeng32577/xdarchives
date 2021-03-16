/**
 * Created by Administrator on 2020/9/28.
 */

var sexComboBox = Ext.create('Ext.form.ComboBox', {
    queryMode: 'local',
    valueField:'value',
    displayField:'text',
    store: {
        fields:["text","value"],
        data:[
            {text:"男",value:"男"},
            {text:"女",value:"女"}
        ]
    }
});

Ext.define('SupervisionGuidance.view.SupervisionGuidanceFileUserGridView',{
    extend: 'Comps.view.BasicGridView',
    xtype:'supervisionGuidanceFileUserGridView',
    title: '档案员配置情况 <button style="float:right;margin-right: 10px" onclick="fileUserDelete()">删除</button><button style="float:right;margin-right: 20px" onclick="fileUserAdd()">新增</button>',
    itemId:'supervisionGuidanceFileUserGridViewId',
    hasSearchBar:false,//无需基础表格组件中的检索栏
    hasPageBar:false,
    store: 'SupervisionGuidanceFileUserGridStore',
    stripeRows:true, //斑马线效果
    selModel: 'cellmodel',
    plugins: {
        ptype: 'cellediting',//编辑单元格插件
        clicksToEdit: 1//单击编辑
    },
    columns: [
        {text: '姓名', dataIndex: 'username', flex: 1, menuDisabled: true,editor: 'textfield'},
        {text: '性别', dataIndex: 'sex', flex: 1, menuDisabled: true,editor: sexComboBox},
        {text: '上岗证号', dataIndex: 'workno', flex: 1, menuDisabled: true,editor: 'textfield'},
        {text: '办公室电话', dataIndex: 'officephone', flex: 1, menuDisabled: true,editor: 'textfield'},
        {text: '手机号码', dataIndex: 'mobilephone', flex: 1, menuDisabled: true,editor: 'textfield'},
        {text: '是否专职', dataIndex: 'isfulltime', flex: 1, menuDisabled: true,editor: 'textfield'},
        {text: '上岗日期', dataIndex: 'workdate', flex: 1, menuDisabled: true,editor: 'textfield'},
        {text: '最新年审日期', dataIndex: 'aduitdate', flex: 1, menuDisabled: true,editor: 'textfield'}
    ]
});



function fileUserAdd() {  //新增
    var grid = window.supervisionGuidanceFileUserGridView;
    var num = grid.getStore().getCount();
    var p ={
        username:'',
        sex:'',
        workno:'',
        officephone:'',
        mobilephone:'',
        isfulltime:'',
        workdate:'',
        mobilephone:'',
        aduitdate:''
    };
    grid.getStore().insert(num,p);
}

function fileUserDelete() {   //删除
    var grid = window.supervisionGuidanceFileUserGridView;
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
                type:'fileuser'
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

