/**
 * Created by Administrator on 2020/10/15.
 */


Ext.define('YearlyCheckAudit.controller.YearlyCheckAuditController',{
    extend : 'Ext.app.Controller',
    views :  [
        'YearlyCheckAuditFormDealView','YearlyCheckAuditFormGridView','YearlyCheckAuditFormView',
        'YearlyCheckAuditGridView','YearlyCheckAuditView','ApproveAddView'
    ],
    stores:  [
        'NextNodeStore','NextSpmanStore','YearlyCheckAuditFormGridStore','YearlyCheckAuditGridStore'
    ],
    models:  [
        'YearlyCheckAuditGridModel','YearlyCheckAuditFormGridStoreModel'
    ],
    init : function() {
        var isAddPostil = false;//判断是否已经添加过批注
        this.control({
            'yearlyCheckAuditView':{
                afterrender:function (view) {
                    if(taskId!=''){
                        var yearlyCheckAuditFormDealView = view.down('yearlyCheckAuditFormDealView');
                        var yearlyCheckAuditFormView = yearlyCheckAuditFormDealView.down('yearlyCheckAuditFormView');
                        yearlyCheckAuditFormDealView.down('form').load({
                            url: '/yearlyCheckAudit/getYearlyCheckApproveDoc',
                            params: {
                                id:taskId
                            },
                            success: function (form,action) {
                                var data = Ext.decode(action.response.responseText).data;
                                yearlyCheckAuditFormView.data = data;
                                window.wapprove = data.approve;
                            },
                            failure: function () {
                                XD.msg('操作中断');
                            }
                        });
                        yearlyCheckAuditFormDealView.down('yearlyCheckAuditFormGridView').initGrid({taskid:taskId});
                        var nextNodeStore = yearlyCheckAuditFormDealView.down('[itemId=nextNodeId]').getStore();
                        nextNodeStore.proxy.extraParams.taskid = taskId;
                        nextNodeStore.load();
                        yearlyCheckAuditFormView.taskid = taskId;
                        view.setActiveItem(yearlyCheckAuditFormDealView);
                    }else{
                        view.down('yearlyCheckAuditGridView').initGrid();
                    }
                }
            },

            'yearlyCheckAuditGridView button[itemId=auditId]':{  //审核
                click:function (view) {
                    var yearlyCheckAuditGridView = view.findParentByType('yearlyCheckAuditGridView');
                    var yearlyCheckAuditView = view.findParentByType('yearlyCheckAuditView');
                    var yearlyCheckAuditFormDealView = yearlyCheckAuditView.down('yearlyCheckAuditFormDealView');
                    var yearlyCheckAuditFormView = yearlyCheckAuditFormDealView.down('yearlyCheckAuditFormView');
                    var select = yearlyCheckAuditGridView.getSelectionModel().getSelection();
                    if(select.length !=1){
                        XD.msg('只能选择一条数据');
                        return;
                    }
                    yearlyCheckAuditFormDealView.down('form').load({
                        url: '/yearlyCheckAudit/getYearlyCheckApproveDoc',
                        params: {
                            id:select[0].get('id')
                        },
                        success: function (form,action) {
                            var data = Ext.decode(action.response.responseText).data;
                            yearlyCheckAuditFormView.data = data;
                            window.wapprove = data.approve;
                        },
                        failure: function () {
                            XD.msg('操作中断');
                        }
                    });
                    yearlyCheckAuditFormDealView.down('yearlyCheckAuditFormGridView').initGrid({taskid:select[0].get('id')});
                    var nextNodeStore = yearlyCheckAuditFormDealView.down('[itemId=nextNodeId]').getStore();
                    nextNodeStore.proxy.extraParams.taskid = select[0].get('id');
                    nextNodeStore.load();
                    yearlyCheckAuditFormView.taskid = select[0].get('id');
                    yearlyCheckAuditView.setActiveItem(yearlyCheckAuditFormDealView);
                }
            },

            'yearlyCheckAuditFormView button[itemId=approveFormSubmit]':{  //审核-提交
                click:function (view) {
                    var yearlyCheckAuditFormView = view.findParentByType('yearlyCheckAuditFormView');
                    var yearlyCheckAuditView = yearlyCheckAuditFormView.findParentByType('yearlyCheckAuditView');
                    var yearlyCheckAuditGridView = yearlyCheckAuditView.down('yearlyCheckAuditGridView');
                    var textArea = yearlyCheckAuditFormView.down('[itemId=approveId]').getValue();
                    var nextNode = yearlyCheckAuditFormView.down('[itemId=nextNodeId]').getValue();
                    var nextSpman = yearlyCheckAuditFormView.down('[itemId=nextSpmanId]').getValue();
                    if(nextNode==null){
                        XD.msg('下一环节不能为空');
                        return ;
                    }

                    if(yearlyCheckAuditFormView.down('[itemId=nextNodeId]').rawValue!='结束'
                        &&(yearlyCheckAuditFormView.down('[itemId=nextSpmanId]').rawValue=='')){
                        XD.msg('下一环节审批人不能为空');
                        return ;
                    }
                    var flowsText = yearlyCheckAuditFormView.data.approvetext;
                    if(''==textArea||!isAddPostil) {
                        var curdate = getNowFormatDate();
                        var rname = window.parent.realname ? window.parent.realname : window.parent.parent.realname;
                        if (textArea != ''&&textArea!=undefined) {
                            textArea += '\n\n意见：通过\n' + flowsText + '：' + rname + '\n' + curdate;
                        } else {
                            textArea += '意见：通过\n' + flowsText + '：' + rname + '\n' + curdate;
                        }
                    }
                    Ext.Ajax.request({
                        params: {
                            textArea:textArea,
                            nextNode:nextNode,
                            nextSpman:nextSpman,
                            taskid:yearlyCheckAuditFormView.taskid
                        },
                        url: '/yearlyCheckAudit/approveFormSubmit',
                        method: 'POST',
                        success: function (resp) {
                            var respText = Ext.decode(resp.responseText);
                            XD.msg(respText.msg);
                            Ext.defer(function(){
                                if(taskId!=''){
                                    parent.closeObj.close(parent.layer.getFrameIndex(window.name));
                                }else{
                                    yearlyCheckAuditGridView.getStore().reload();
                                    yearlyCheckAuditView.setActiveItem(yearlyCheckAuditGridView);
                                }
                            },1000);
                        },
                        failure : function() {
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            'yearlyCheckAuditFormView button[itemId=approveFormBack]':{ //审核-退回
                click:function(view){
                    XD.confirm('是否确定退回',function () {
                        var yearlyCheckAuditFormView = view.findParentByType('yearlyCheckAuditFormView');
                        var yearlyCheckAuditView = yearlyCheckAuditFormView.findParentByType('yearlyCheckAuditView');
                        var yearlyCheckAuditGridView = yearlyCheckAuditView.down('yearlyCheckAuditGridView');
                        var textArea = yearlyCheckAuditFormView.down('[itemId=approveId]').getValue();
                        var flowsText = yearlyCheckAuditFormView.data.approvetext;
                        var curdate = getNowFormatDate();
                        var rname = window.parent.realname ? window.parent.realname : window.parent.parent.realname;
                        if (textArea == '') {
                            textArea += '意见：不通过\n'+flowsText+'：' + rname +'\n'+curdate;
                        } else if (textArea.indexOf('驳回') < 0) {
                            textArea += '\n\n意见：不通过\n'+flowsText+'：' + rname +'\n'+curdate;
                        }
                        Ext.Ajax.request({
                            params: {
                                textarea:textArea,
                                taskid:yearlyCheckAuditFormView.taskid
                            },
                            url: '/yearlyCheckAudit/pproveFormBack',
                            method: 'POST',
                            success: function (resp) {
                                XD.msg('审批完成');
                                Ext.defer(function(){
                                    if(taskId!=''){
                                        parent.closeObj.close(parent.layer.getFrameIndex(window.name));
                                    }else{
                                        yearlyCheckAuditGridView.getStore().reload();
                                        yearlyCheckAuditView.setActiveItem(yearlyCheckAuditGridView);
                                    }
                                },1000);
                            },
                            failure : function() {
                                XD.msg('操作失败');
                            }
                        });
                    },this);
                }
            },

            'yearlyCheckAuditFormView button[itemId=approveFormClose]':{  //审核-关闭
                click:function (view) {
                    var yearlyCheckAuditFormView = view.findParentByType('yearlyCheckAuditFormView');
                    var yearlyCheckAuditView = yearlyCheckAuditFormView.findParentByType('yearlyCheckAuditView');
                    var yearlyCheckAuditGridView = yearlyCheckAuditView.down('yearlyCheckAuditGridView');
                    yearlyCheckAuditGridView.getStore().reload();
                    yearlyCheckAuditView.setActiveItem(yearlyCheckAuditGridView);
                }
            },

            'yearlyCheckAuditFormView button[itemId=approveAdd]':{
                click:function(view){
                    var yearlyCheckAuditFormView = view.findParentByType('yearlyCheckAuditFormView');
                    var approveAddView = Ext.create('YearlyCheckAudit.view.ApproveAddView');
                    approveAddView.data = yearlyCheckAuditFormView.data;
                    approveAddView.yearlyCheckAuditFormView = yearlyCheckAuditFormView;
                    approveAddView.show();
                }
            },

            'approveAddView':{
                render:function(field){
                    field.down('[itemId=selectApproveId]').on('change',function(val){
                        field.down('[itemId=approveId]').setValue(val.value);
                    });
                },
                afterrender:function (field) {
                    if(typeof window.wareatext!='undefined'){
                        field.down('[itemId=approveId]').setValue(window.wareatext);
                    }
                }
            },
            'approveAddView button[itemId=approveAddSubmit]':{
                click:function(view){
                    var approveAddView = view.up('approveAddView');
                    var areaText = approveAddView.down('[itemId=approveId]').getValue();
                    if(''==areaText){
                        XD.msg('请输入批示');
                        return;
                    }

                    if(isAddPostil){
                        XD.msg('您已添加过批示');
                        return;
                    }

                    window.wareatext=areaText;
                    var flowsText = approveAddView.data.approvetext;
                    var curdate=getNowFormatDate();
                    var rname = window.parent.realname ? window.parent.realname : window.parent.parent.realname;
                    var text = '意见：'+areaText+'\n'+flowsText+'：' + rname +'\n'+curdate;
                    if(window.wapprove!=''&&window.wapprove!=undefined){
                        text = window.wapprove+'\n\n'+text;
                    }
                    approveAddView.yearlyCheckAuditFormView.down('[itemId=approveId]').setValue(text);
                    approveAddView.close();
                    isAddPostil = true;
                }
            },

            'approveAddView button[itemId=approveAddClose]':{
                click:function(view){
                    view.findParentByType("approveAddView").close();
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

