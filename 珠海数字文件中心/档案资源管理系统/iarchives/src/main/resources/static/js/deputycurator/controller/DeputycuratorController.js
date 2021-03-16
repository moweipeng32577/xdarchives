/**
 * Created by Administrator on 2020/7/20.
 */
Ext.define('Deputycurator.controller.DeputycuratorController', {
    extend: 'Ext.app.Controller',

    views: [
        'CuratorView','CuratorDclView','CuratorYclView','ProjectAddLookFormView',
        'ProjectAddLookView','ProjectLogLookGridView','ApproveAddView'
    ],//加载view
    stores: ['CuratorDclStore','CuratorYclStore','ProjectLogLookGridStore','NextNodeStore',
        'ApproveOrganStore','NextSpmanStore'],//加载store
    models: ['CuratorDclModel','CuratorYclModel','ProjectLogLookGridModel'],//加载model

    init: function () {
        this.control({
            'curatorView':{
                tabchange:function (view) {//tab页面切换触发
                    if (view.activeTab.title == '待处理') {
                        var gridcard=view.down('curatorDclView');
                        gridcard.getStore().reload();
                    }else if(view.activeTab.title == '已处理') {
                        var gridcard = view.down('curatorYclView');
                        gridcard.getStore().reload();
                    }
                }
            },
            'curatorYclView':{
                afterrender:function (view) {
                    view.initGrid({projectstatus:'副领导审阅通过,副领导审阅不通过'});
                }
            },
            'curatorDclView':{
                afterrender:function (view) {
                    view.initGrid({projectstatus:"部门审核通过,提交副馆长审阅"});
                }
            },
            'curatorDclView button[itemId=look]':{  //待处理-查看
                click:function (view) {
                    var curatorDclGridView = view.findParentByType('curatorDclView');
                    var select = curatorDclGridView.getSelectionModel().getSelection();
                    if(select.length !=1){
                        XD.msg('只能选择一条数据');
                        return;
                    }
                    var projectAddLookView = Ext.create('Deputycurator.view.ProjectAddLookView');
                    var form = projectAddLookView.down('form');
                    var projectLogLookGridView = projectAddLookView.down('projectLogLookGridView');
                    projectLogLookGridView.initGrid({id:select[0].get('id')});
                    form.load({
                        url:'/projectRate/getProjectManageByid',
                        method:'GET',
                        params:{
                            id:select[0].get('id')
                        },
                        success:function () {
                        },
                        failure:function () {
                            XD.msg('获取表单信息失败');
                        }
                    });
                    projectAddLookView.show();
                }
            },
            'projectAddLookFormView button[itemId=close]':{  //查看 关闭
                click:function (view) {
                    view.findParentByType('window').close();
                }
            },
            'curatorDclView button[itemId=review]': {  //待处理-审阅
                click:function(view){
                    var curatorDclGridView = view.findParentByType('curatorDclView');
                    var select = curatorDclGridView.getSelectionModel().getSelection();
                    if(select.length !=1){
                        XD.msg('请选择一条操作数据');
                        return;
                    }
                    var ids = [];
                    for(var i=0;i<select.length;i++){
                        ids.push(select[i].get('id'));
                    }
                    var approveAddView = Ext.create('Deputycurator.view.ApproveAddView',{ids:ids,grid:curatorDclGridView});
                    var nextNode = approveAddView.down('[itemId=nextNodeId]');
                    nextNode.getStore().proxy.extraParams.spnodeid = select[0].get('spnodeid');
                    nextNode.getStore().load();
                    approveAddView.down('[itemId=approveId]').setValue(select[0].get('approve'));
                    approveAddView.show();
                }
            },
            'approveAddView button[itemId=approveAddSubmit]':{ //审阅-提交
                click:function(view){
                    var approveAddView = view.up('approveAddView');
                    var curatorDclGridView = view.up('approveAddView').grid;
                    var approveresult = approveAddView.down('[itemId=selectApproveId]').getValue();//审核结果
                    var textArea = approveAddView.down('[itemId=approveId]').getValue();//批示
                    var addprove = approveAddView.down('[itemId=addproveId]').getValue();//添加批示
                    var spnodeid = approveAddView.down('[itemId=nextNodeId]').getValue();
                    var spmanid = approveAddView.down('[itemId=nextSpmanId]').getValue();
                    if(''==addprove){
                        XD.msg('请添加批示');
                        return;
                    }
                    var curdate = getNowFormatDate();
                    var rname = window.parent.realname ? window.parent.realname : window.parent.parent.realname;
                    if (textArea != '') {
                        textArea += '\n\n意见：' + addprove + '\n' + approveAddView.title + '：' + rname + '\n' + curdate;
                    } else {
                        textArea += '意见：' + addprove + '\n' + approveAddView.title + '：' + rname + '\n' + curdate;
                    }
                    Ext.Ajax.request({
                        url:'/projectRate/updateProjectStatusByDeputycurator',
                        method:'POST',
                        params:{
                            spnodeid:spnodeid,
                            spmanid:spmanid,
                            ids:approveAddView.ids,
                            approveresult:approveresult,
                            areaText:addprove,//当前环节审批意见
                            allapprove:textArea//所有环节审批意见
                        },
                        success:function (rep) {
                            var respText = Ext.decode(rep.responseText);
                            approveAddView.close();
                            if(!respText.success){
                                XD.msg('提交失败');
                            }else{
                                XD.msg('提交成功');
                                var curatorYclGridView = curatorDclGridView.findParentByType('curatorView').down('curatorYclView');
                                curatorDclGridView.getStore().reload();
                                curatorYclGridView.getStore().reload();
                            }
                        },
                        failure:function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },
            'approveAddView button[itemId=approveAddClose]':{ //审核-关闭
                click:function(view){
                    view.findParentByType("approveAddView").close();
                }
            },
            'approveAddView':{
                render:function(field){
                    field.down('[itemId=selectApproveId]').on('change',function(val){
                        field.down('[itemId=addproveId]').setValue(val.value);
                    });
                },
                afterrender:function (field) {
                    // if(typeof window.wareatext!='undefined'){
                    //     field.down('[itemId=approveId]').setValue(window.wareatext);
                    // }
                }
            },
            'curatorYclView button[itemId=look]':{  //已处理-查看项目
                click:function (view) {
                    var curatorYclGridView = view.findParentByType('curatorYclView');
                    var select = curatorYclGridView.getSelectionModel().getSelection();
                    if(select.length !=1){
                        XD.msg('只能选择一条数据');
                        return;
                    }
                    var projectAddLookView = Ext.create('Deputycurator.view.ProjectAddLookView');
                    var form = projectAddLookView.down('form');
                    var projectLogLookGridView = projectAddLookView.down('projectLogLookGridView');
                    projectLogLookGridView.initGrid({id:select[0].get('id')});
                    form.load({
                        url:'/projectRate/getProjectManageByid',
                        method:'GET',
                        params:{
                            id:select[0].get('id')
                        },
                        success:function () {
                        },
                        failure:function () {
                            XD.msg('获取表单信息失败');
                        }
                    });
                    projectAddLookView.show();
                }
            }
        });
    }
});

function getNowFormatDate() {
    var date = new Date();
    var seperator1 = "";
    var seperator2 = ":";
    var month = date.getMonth() + 1;
    var strDate = date.getDate();
    var hour= date.getHours();
    var minutes=date.getMinutes();
    var second = date.getSeconds();
    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
    }
    if (hour >= 0 && hour <= 9) {
        hour = "0" + hour;
    }
    if (minutes >= 0 && minutes <= 9) {
        minutes = "0" + minutes;
    }
    if (second >= 0 && second <= 9) {
        second = "0" + second;
    }
    var currentdate = date.getFullYear() + '年' + month + '月' + strDate + '日 '+hour+":"+minutes+":"+second;
    return currentdate;
}
