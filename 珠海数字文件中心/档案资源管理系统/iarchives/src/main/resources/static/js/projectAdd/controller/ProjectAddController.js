/**
 * Created by Administrator on 2020/7/20.
 */
Ext.define('ProjectAdd.controller.ProjectAddController', {
    extend: 'Ext.app.Controller',

    views: [
        'ProjectAddView','AddDclView', 'AddYclView','ProjectManageAddView',
        'ProjectAddLookView','ProjectAddLookFormView','ProjectLogLookGridView',
        'ApproveAddView'
    ],//加载view
    stores: ['AddDclStore','ProjectLogLookGridStore','AddYclStore','ApproveManStore',
        'ApproveOrganStore','ProjectNodeStore'],//加载store
    models: ['AddDclModel','ProjectLogLookGridModel','AddYclModel'],//加载model

    init: function () {
        this.control({
            'projectAddView':{
                tabchange:function (view) {//tab页面切换触发
                    if (view.activeTab.title == '待处理') {
                        var gridcard=view.down('addDclView');
                        gridcard.getStore().reload();
                    }else if(view.activeTab.title == '已处理'){
                        var gridcard=view.down('addYclView');
                        gridcard.getStore().reload();
                    }
                }
            },
            'addYclView':{
                afterrender:function (view) {
                    // view.initGrid();
                }
            },
            'addDclView':{
                afterrender:function (view) {
                    view.initGrid({projectstatus:"新增项目,部门审核不通过,副领导审阅不通过,领导审阅不发布"});
                }
            },
            'addDclView button[itemId=add]':{  //新增项目
                click:function (view) {
                    var addDclGridView = view.findParentByType('addDclView');
                    var projectManageAddView = Ext.create('ProjectAdd.view.ProjectManageAddView');
                    projectManageAddView.addDclGridView = addDclGridView;
                    projectManageAddView.show();
                }
            },
            'projectManageAddView button[itemId=projectCheck]':{  //确定新增
                click:function (view) {
                    var projectManageAddView = view.findParentByType('projectManageAddView');
                    var form = projectManageAddView.down('form');
                    if(!form.isValid()){
                        XD.msg('存在必填项未填写');
                        return;
                    }
                    form.submit({
                        url:'/projectRate/projectManageSubmit',
                        method:'POST',
                        scope: this,
                        success:function (form,action) {
                            if(projectManageAddView.title=='修改项目'){
                                XD.msg('修改成功');
                            }else{
                                XD.msg('新增成功');
                            }
                            projectManageAddView.addDclGridView.getStore().reload();
                            projectManageAddView.close();
                        },
                        failure:function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },
            'projectManageAddView button[itemId=projectClose]':{  //关闭
                click:function (view) {
                    view.findParentByType('projectManageAddView').close();
                }
            },
            'addDclView button[itemId=modify]':{  //修改项目
                click:function (view) {
                    var addDclGridView = view.findParentByType('addDclView');
                    var select = addDclGridView.getSelectionModel().getSelection();
                    if(select.length!=1){
                        XD.msg('只能选择一条数据');
                        return;
                    }
                    var projectManageAddView = Ext.create('ProjectAdd.view.ProjectManageAddView');
                    var form = projectManageAddView.down('form');
                    form.load({
                        url:'/projectRate/getProjectManageByid',
                        method:'POST',
                        params:{
                            id:select[0].get('id')
                        },
                        success:function (form,action) {
                            var respText = Ext.decode(action.response.responseText);
                            if(!respText.success){
                                XD.msg('获取表单信息失败');
                            }
                            projectManageAddView.setTitle('修改项目');
                            projectManageAddView.addDclGridView = addDclGridView;
                            projectManageAddView.show();
                        },
                        failure:function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },
            'addDclView button[itemId=del]':{  //删除项目
                click:function (view) {
                    var addDclGridView = view.findParentByType('addDclView');
                    var select = addDclGridView.getSelectionModel().getSelection();
                    if(select.length <1){
                        XD.msg('请至少选择一条数据');
                        return;
                    }
                    var ids = [];
                    for(var i=0;i<select.length;i++){
                        ids.push(select[i].get('id'));
                    }
                    XD.confirm('是否删除这'+select.length+'条数据',function () {
                        Ext.Ajax.request({
                            url:'/projectRate/deleteProjectManageByid',
                            method:'POST',
                            params:{
                                ids:ids
                            },
                            success:function (rep) {
                                var respText = Ext.decode(rep.responseText);
                                if(!respText.success){
                                    XD.msg('删除失败');
                                }else{
                                    XD.msg('删除成功');
                                    addDclGridView.getStore().reload();
                                }
                            },
                            failure:function () {
                                XD.msg('操作失败');
                            }
                        });
                    });
                }
            },
            'addDclView button[itemId=look]':{  //查看项目
                click:function (view) {
                    var addDclGridView = view.findParentByType('addDclView');
                    var select = addDclGridView.getSelectionModel().getSelection();
                    if(select.length !=1){
                        XD.msg('只能选择一条数据');
                        return;
                    }
                    var projectAddLookView = Ext.create('ProjectAdd.view.ProjectAddLookView');
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
            'addDclView button[itemId=submit]':{  //提交部门审核
                click:function (view) {
                    var addDclGridView = view.findParentByType('addDclView');
                    var select = addDclGridView.getSelectionModel().getSelection();
                    if(select.length <1){
                        XD.msg('请至少选择一条数据');
                        return;
                    }
                    var ids = [];
                    for(var i=0;i<select.length;i++){
                        ids.push(select[i].get('id'));
                    }
                    var approveAddView = Ext.create('ProjectAdd.view.ApproveAddView',{ids:ids,grid:addDclGridView});
                    approveAddView.down('[itemId=auditlinkId]').getStore().reload();
                    approveAddView.show();
                }
            },
            'approveAddView button[itemId=approveAddSubmit]':{
                click:function (view) {
                    var approveAddView = view.findParentByType('approveAddView');
                    var dclGridView = approveAddView.grid;
                    var spnodeid = approveAddView.down('[itemId=auditlinkId]').getValue();
                    var spmanid = approveAddView.down('[itemId=spmanId]').getValue();
                    Ext.Ajax.request({
                        url:'/projectRate/updateProjectStatusByid',
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
                                var yclGridView = dclGridView.findParentByType('projectAddView').down('addYclView');
                                dclGridView.getStore().reload();
                                yclGridView.getStore().reload();
                                approveAddView.close();
                            }
                        },
                        failure:function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },
            'approveAddView button[itemId=approveAddClose]':{
                click:function (view) {
                    view.findParentByType('approveAddView').close();
                }
            },
            'addYclView button[itemId=look]':{  //已处理-查看项目
                click:function (view) {
                    var addYclGridView = view.findParentByType('addYclView');
                    var select = addYclGridView.getSelectionModel().getSelection();
                    if(select.length !=1){
                        XD.msg('只能选择一条数据');
                        return;
                    }
                    var projectAddLookView = Ext.create('ProjectAdd.view.ProjectAddLookView');
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
        });
    }
});
