/**
 * Created by Administrator on 2020/10/13.
 */



Ext.define('BusinessYearlyCheck.controller.BusinessYearlyCheckController',{
    extend : 'Ext.app.Controller',
    views :  [
        'BusinessYearlyCheckView','BusinessYearlyCheckReportView','BusinessNewYearlyCheckReportView',
        'BusinessYearlyCheckAddFormView','BusinessYearlyCheckFormView','BusinessYearlyCheckSubmitFormView',
        'BusinessYearlyCheckSubmitView','BusinessYearlyCheckSubmitGridView'
    ],
    stores:  [
        'BusinessYearlyCheckReportStore','BusinessNewYearlyCheckReportStore','BusinessYearlyCheckSubmitGridStore',
        'ApproveManStore'
    ],
    models:  [
        'BusinessYearlyCheckReportModel','BusinessYearlyCheckSubmitGridModel'
    ],
    init : function() {
        this.control({
            'businessYearlyCheckView':{
                afterrender:function (view) {
                    view.down('businessYearlyCheckReportView').initGrid({state:'已审批'});
                },
                tabchange:function (view) {
                    if(view.activeTab.title == '档案年检报表'){
                        view.down('businessYearlyCheckReportView').initGrid({state:'已审批'});
                    }else if(view.activeTab.title == '新建年检报表'){
                        view.down('businessNewYearlyCheckReportView').initGrid();
                    }
                }
            },

            'businessNewYearlyCheckReportView button[itemId=addId]':{  //新增
                click:function (view) {
                    var businessNewYearlyCheckReportView = view.findParentByType('businessNewYearlyCheckReportView');
                    var businessYearlyCheckAddFormView = Ext.create("BusinessYearlyCheck.view.BusinessYearlyCheckAddFormView");
                    businessYearlyCheckAddFormView.businessNewYearlyCheckReportView = businessNewYearlyCheckReportView;
                    businessYearlyCheckAddFormView.show();
                }
            },

            'businessYearlyCheckAddFormView button[itemId=addSubmit]':{  //新增-提交
                click:function (view) {
                    var businessYearlyCheckAddFormView = view.findParentByType('businessYearlyCheckAddFormView');
                    var form = businessYearlyCheckAddFormView.down('form');
                    if(!form.isValid()){
                        XD.msg('存在必填项未填写');
                        return;
                    }
                    var source = form.down('[name=source]').getValue();
                    var selectyear = form.down('[name=selectyear]').getValue();
                    var title = form.down('[name=title]').getValue();
                    form.submit({
                        url: '/businessYearlyCheck/addSubmit',
                        method: 'POST',
                        scope: this,
                        success: function (basic, action) {
                            XD.msg('保存成功');
                            businessYearlyCheckAddFormView.close();
                            businessYearlyCheckAddFormView.businessNewYearlyCheckReportView.getStore().reload();
                        },
                        failure:function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            'businessYearlyCheckAddFormView button[itemId=addClose]':{  //新增-关闭
                click:function (view) {
                    view.findParentByType('businessYearlyCheckAddFormView').close();
                }
            },

            'businessNewYearlyCheckReportView button[itemId=deleteId]':{  //删除
                click:function (view) {
                    var businessNewYearlyCheckReportView = view.findParentByType('businessNewYearlyCheckReportView');
                    var select = businessNewYearlyCheckReportView.getSelectionModel().getSelection();
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
                                ids:ids
                            },
                            url:'/businessYearlyCheck/deleteYearlyCheckElectronic',
                            success:function(response){
                                XD.msg('删除成功');
                                businessNewYearlyCheckReportView.getStore().reload();
                            },
                            failure:function(){
                                XD.msg('操作失败');
                            }
                        });
                    });
                }
            },

            'businessNewYearlyCheckReportView button[itemId=lookId]':{  //查看
                click:function (view) {
                    var businessNewYearlyCheckReportView = view.findParentByType('businessNewYearlyCheckReportView');
                    var select = businessNewYearlyCheckReportView.getSelectionModel().getSelection();
                    if(select.length != 1){
                        XD.msg('只能选择一条数据');
                        return;
                    }
                    var businessYearlyCheckFormView = Ext.create("BusinessYearlyCheck.view.BusinessYearlyCheckFormView");
                    businessYearlyCheckFormView.down('[itemId=saveSubmit]').hide();
                    businessYearlyCheckFormView.title = "查看";
                    businessYearlyCheckFormView.down('form').load({
                        url: '/businessYearlyCheck/getYearlyCheckReport',
                        params: {
                            id: select[0].get('id')
                        },
                        success: function () {
                        },
                        failure: function () {
                            XD.msg('操作中断');
                        }
                    });
                    businessYearlyCheckFormView.show();
                }
            },

            'businessNewYearlyCheckReportView button[itemId=editId]':{  //修改
                click:function (view) {
                    var businessNewYearlyCheckReportView = view.findParentByType('businessNewYearlyCheckReportView');
                    var select = businessNewYearlyCheckReportView.getSelectionModel().getSelection();
                    if(select.length != 1){
                        XD.msg('只能选择一条数据');
                        return;
                    }
                    var businessYearlyCheckFormView = Ext.create("BusinessYearlyCheck.view.BusinessYearlyCheckFormView");
                    businessYearlyCheckFormView.title = "修改";
                    businessYearlyCheckFormView.businessNewYearlyCheckReportView = businessNewYearlyCheckReportView;
                    businessYearlyCheckFormView.down('form').load({
                        url: '/businessYearlyCheck/getYearlyCheckReport',
                        params: {
                            id: select[0].get('id')
                        },
                        success: function () {
                        },
                        failure: function () {
                            XD.msg('操作中断');
                        }
                    });
                    businessYearlyCheckFormView.show();
                }
            },

            'businessYearlyCheckFormView button[itemId=saveSubmit]':{  //修改-保存
                click:function (view) {
                    var businessYearlyCheckFormView = view.findParentByType('businessYearlyCheckFormView');
                    var form = businessYearlyCheckFormView.down('form');
                    if(!form.isValid()){
                        XD.msg('存在必填项未填写');
                        return;
                    }
                    form.submit({
                        url: '/businessYearlyCheck/saveSubmit',
                        method: 'POST',
                        scope: this,
                        success: function (form, action) {
                            XD.msg('保存成功');
                            businessYearlyCheckFormView.close();
                            businessYearlyCheckFormView.businessNewYearlyCheckReportView.getStore().reload();
                        },
                        failure:function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            'businessYearlyCheckFormView button[itemId=saveClose]':{  //查看、修改-关闭
                click:function (view) {
                    view.findParentByType('businessYearlyCheckFormView').close();
                }
            },

            'businessYearlyCheckReportView [itemId=downloadBtn],businessNewYearlyCheckReportView [itemId=downloadBtn]':{  //档案年检报表-下载、新建年检报表-下载
                click:function(view, record, item, index, e){
                    var reportid = view.getStore().getAt(item).id;
                    location.href = '/businessYearlyCheck/downLoadElectronic?id='+reportid;
                }
            },

            'businessNewYearlyCheckReportView button[itemId=submitId]':{  //提交
                click:function (view) {
                    var businessNewYearlyCheckReportView = view.findParentByType('businessNewYearlyCheckReportView');
                    var select = businessNewYearlyCheckReportView.getSelectionModel().getSelection();
                    if(select.length<1){
                        XD.msg('至少选择一条数据');
                        return;
                    }
                    var ids = [];
                    for(var i=0;i<select.length;i++){
                        if(select[i].get('state')=='已提交'||select[i].get('state')=='已审批'){
                            XD.msg('存在年检报表正在审核或者已经审核完成');
                            return;
                        }
                        ids.push(select[i].get('id'));
                    }
                    var businessYearlyCheckSubmitView = Ext.create("BusinessYearlyCheck.view.BusinessYearlyCheckSubmitView");
                    businessYearlyCheckSubmitView.businessNewYearlyCheckReportView = businessNewYearlyCheckReportView;
                    businessYearlyCheckSubmitView.ids = ids;
                    businessYearlyCheckSubmitView.down('form').load({
                        url: '/businessYearlyCheck/getYearlyCheckApproveDoc',
                        params: {
                        },
                        success: function () {
                        },
                        failure: function () {
                            XD.msg('操作中断');
                        }
                    });
                    businessYearlyCheckSubmitView.down('businessYearlyCheckSubmitGridView').initGrid({ids:ids});
                    businessYearlyCheckSubmitView.down('[itemId=spmanId]').getStore().load();
                    businessYearlyCheckSubmitView.show();
                }
            },

            'businessYearlyCheckSubmitView button[itemId=approveSubmit]':{  //提交-提交
                click:function (view) {
                    var businessYearlyCheckSubmitView = view.findParentByType('businessYearlyCheckSubmitView');
                    var form = businessYearlyCheckSubmitView.down('form');
                    var spman = form.down('[itemId=spmanId]').getValue();
                    if(!form.isValid()){
                        XD.msg('存在必填项未填写');
                        return;
                    }
                    form.submit({
                        url: '/businessYearlyCheck/approveDocSubmit',
                        method: 'POST',
                        params:{
                            spman:spman,
                            ids:businessYearlyCheckSubmitView.ids
                        },
                        scope: this,
                        success: function (form, action) {
                            var respText = Ext.decode(action.response.responseText);
                            if (respText.success == true) {
                                XD.msg(respText.msg);
                                businessYearlyCheckSubmitView.close();
                                businessYearlyCheckSubmitView.businessNewYearlyCheckReportView.getStore().reload();
                            } else {
                                XD.msg(respText.msg);
                            }
                        },
                        failure:function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            'businessYearlyCheckSubmitView button[itemId=approveClose]':{  //提交-关闭
                click:function (view) {
                    view.findParentByType('businessYearlyCheckSubmitView').close();
                }
            }
        });
    }
});

