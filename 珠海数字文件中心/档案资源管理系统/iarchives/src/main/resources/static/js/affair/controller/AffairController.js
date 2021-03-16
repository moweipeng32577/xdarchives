/**
 * Created by Administrator on 2020/7/20.
 */
Ext.define('Affair.controller.AffairController', {
    extend: 'Ext.app.Controller',

    views: [
        'AffairView','AffairDclView','AffairYclView','ProjectAddLookFormView',
        'ProjectAddLookView','ProjectLogLookGridView','ApproveAddView'
    ],//加载view
    stores: ['AffairDclStore','AffairYclStore','ProjectLogLookGridStore','AffairNodeStore',
        'ApproveOrganStore','ApproveManStore'],//加载store
    models: ['AffairDclModel','AffairYclModel','ProjectLogLookGridModel'],//加载model

    init: function () {
        this.control({
            'affairView':{
                tabchange:function (view) {//tab页面切换触发
                    if (view.activeTab.title == '待处理') {
                        var gridcard=view.down('affairDclView');
                        gridcard.getStore().reload();
                    }
                }
            },
            'affairDclView button[itemId=look]':{  //待处理-查看
                click:function (view) {
                    var affairDclGridView = view.findParentByType('affairDclView');
                    var select = affairDclGridView.getSelectionModel().getSelection();
                    if(select.length !=1){
                        XD.msg('只能选择一条数据');
                        return;
                    }
                    var projectAddLookView = Ext.create('Affair.view.ProjectAddLookView');
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
            'affairDclView button[itemId=review]': {  //待处理-提交审阅
                click:function(view){
                    var affairDclGridView = view.findParentByType('affairDclView');
                    var select = affairDclGridView.getSelectionModel().getSelection();
                    if(select.length <1){
                        XD.msg('请至少选择一条数据');
                        return;
                    }
                    var ids = [];
                    for(var i=0;i<select.length;i++){
                        ids.push(select[i].get('id'));
                    }
                    var approveAddView = Ext.create('Affair.view.ApproveAddView',{ids:ids,grid:affairDclGridView});
                    approveAddView.down('[itemId=auditlinkId]').getStore().reload();
                    approveAddView.show();
                }
            },
            'approveAddView button[itemId=approveAddSubmit]':{
                click:function (view) {
                    var approveAddView = view.findParentByType('approveAddView');
                    var affairDclGridView = approveAddView.grid;
                    var spnodeid = approveAddView.down('[itemId=auditlinkId]').getValue();
                    var spmanid = approveAddView.down('[itemId=spmanId]').getValue();
                    XD.confirm('确定提交这'+approveAddView.ids.length+'条数据至副领导审阅',function () {
                        Ext.Ajax.request({
                            url:'/projectRate/updateProjectAffairStatusByid',
                            method:'POST',
                            params:{
                                spnodeid:spnodeid,
                                spmanid:spmanid,
                                ids:approveAddView.ids
                            },
                            success:function (rep) {
                                var respText = Ext.decode(rep.responseText);
                                if(!respText.success){
                                    XD.msg('提交失败');
                                }else{
                                    XD.msg('提交成功');
                                    var affairYclGridView = affairDclGridView.findParentByType('affairView').down('affairYclView');
                                    affairDclGridView.getStore().reload();
                                    affairYclGridView.getStore().reload();
                                    approveAddView.close();
                                }
                            },
                            failure:function () {
                                XD.msg('操作失败');
                            }
                        });
                    });
                }
            },
            'approveAddView button[itemId=approveAddClose]':{
                click:function (view) {
                    view.findParentByType('approveAddView').close();
                }
            },
            'affairYclView button[itemId=look]':{  //已处理-查看项目
                click:function (view) {
                    var affairYclGridView = view.findParentByType('affairYclView');
                    var select = affairYclGridView.getSelectionModel().getSelection();
                    if(select.length !=1){
                        XD.msg('只能选择一条数据');
                        return;
                    }
                    var projectAddLookView = Ext.create('Affair.view.ProjectAddLookView');
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
