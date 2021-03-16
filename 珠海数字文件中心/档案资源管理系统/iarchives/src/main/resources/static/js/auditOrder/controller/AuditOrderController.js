/**
 * Created by Administrator on 2020/6/13.
 */

Ext.define('AuditOrder.controller.AuditOrderController', {
    extend: 'Ext.app.Controller',

    views: ['AuditOrderGridView','CarOrderAuditFormView','PlaceOrderAuditFormView','AuditOrderAdminView',
    'InformOrderGridView','LookInformOrderView'],//加载view
    stores: ['AuditOrderGridStore','ApproveManStore','NextNodeStore','InformOrderGridStore',
    'ApproveOrganStore'],//加载store
    models: ['AuditOrderGridModel','AuditOrderGridModel'],//加载model
    init: function () {
        this.control({
            'auditOrderAdminView': {
                afterrender:function (view) {
                    if(auditType=='audit'){
                        var auditOrderGridView = view.down('auditOrderGridView');
                        auditOrderGridView.initGrid();
                        view.setActiveItem(auditOrderGridView);
                    }else{
                        var informOrderGridView = view.down('informOrderGridView');
                        informOrderGridView.initGrid();
                        view.setActiveItem(informOrderGridView);
                    }
                }
            },

            'auditOrderGridView button[itemId=orderAudit]':{  // 审核
                click:function (view) {
                    var auditOrderGridView = view.findParentByType('auditOrderGridView');
                    var auditOrderAdminView = auditOrderGridView.findParentByType('auditOrderAdminView');
                    var select = auditOrderGridView.getSelectionModel().getSelection();
                    if(select.length != 1){
                        XD.msg('只能选择一条数据');
                        return;
                    }
                    if(select[0].get('type')=='公车预约'){
                        var orderid = this.getCarOrderid(select[0].get('id'),'carOrder');
                        var carOrderAuditFormView = auditOrderAdminView.down('carOrderAuditFormView');
                        var nextNode = carOrderAuditFormView.down('[itemId=nextNodeId]');
                        nextNode.getStore().proxy.extraParams.orderid = orderid;
                        nextNode.getStore().proxy.extraParams.type = 'carOrder';
                        nextNode.getStore().load();
                        var form = carOrderAuditFormView.down('form');
                        form.load({
                            url: '/carOrder/carOrderLookFormLoad',
                            method: 'GET',
                            params: {
                                orderid: orderid
                            },
                            success: function (form, action) {
                                var data = Ext.decode(action.response.responseText).data;
                                carOrderAuditFormView.data = data;
                            },
                            failure: function () {
                                XD.msg('获取表单信息失败');
                            }
                        });
                        carOrderAuditFormView.orderid = orderid;
                        auditOrderAdminView.setActiveItem(carOrderAuditFormView);
                    }else if(select[0].get('type')=='场地预约'){
                        var orderid = this.getCarOrderid(select[0].get('id'),'placeOrder');
                        var placeOrderAuditFormView = auditOrderAdminView.down('placeOrderAuditFormView');
                        var nextNode = placeOrderAuditFormView.down('[itemId=nextNodeId]');
                        nextNode.getStore().proxy.extraParams.orderid = orderid;
                        nextNode.getStore().proxy.extraParams.type = 'placeOrder';
                        nextNode.getStore().load();
                        var form = placeOrderAuditFormView.down('form');
                        form.load({
                            url:'/placeOrder/placeOrderLookFormLoad',
                            method:'GET',
                            params:{
                                orderid: orderid
                            },
                            success:function (form,action) {
                                var data = Ext.decode(action.response.responseText).data;
                                placeOrderAuditFormView.data = data;
                            },
                            failure:function () {
                                XD.msg('获取表单信息失败');
                            }
                        });
                        placeOrderAuditFormView.orderid = orderid;
                        auditOrderAdminView.setActiveItem(placeOrderAuditFormView);
                    }else if(select[0].get('type')=='部门审核'){
                        location.href = '/projectRate/auditmain';//跳转部门审核模块
                    }else if(select[0].get('type')=='副馆长审阅'){
                        location.href = '/projectRate/deputyCuratormain';//跳转副馆长审阅模块
                    }else if(select[0].get('type')=='馆长审阅'){
                        location.href = '/projectRate/curatormain';//跳转馆长审阅模块
                    }
                }
            },

            'carOrderAuditFormView button[itemId=carOrderApproveSubmit]':{  //提交 公车预约审核
                click:function(view){
                    var carOrderAuditFormView = view.findParentByType('carOrderAuditFormView');
                    var auditOrderAdminView = carOrderAuditFormView.findParentByType('auditOrderAdminView');
                    var auditOrderGridView = auditOrderAdminView.down('auditOrderGridView');
                    var textArea = carOrderAuditFormView.down('[itemId=approveId]').getValue();
                    var nextNode = carOrderAuditFormView.down('[itemId=nextNodeId]').getValue();
                    var nextSpman = carOrderAuditFormView.down('[itemId=nextSpmanId]').getValue();
                    var flowsText = carOrderAuditFormView.data.auditlink;
                    var addprove = carOrderAuditFormView.down('[itemId=addproveId]').getValue();
                    var selectApprove = carOrderAuditFormView.down('[itemId=selectApproveId]').getValue();
                    if(selectApprove == '同意' && nextNode == null && nextSpman == null){
                        XD.msg('下一环节审批人不能为空');
                        return;
                    }

                    var curdate = getNowFormatDate();
                    var rname = window.parent.realname ? window.parent.realname : window.parent.parent.realname;
                    if (textArea != '') {
                        textArea += '\n\n意见：' + addprove + '\n' + flowsText + '：' + rname + '\n' + curdate;
                    } else {
                        textArea += '意见：' + addprove + '\n' + flowsText + '：' + rname + '\n' + curdate;
                    }
                    Ext.Ajax.request({
                        params: {
                            textArea:textArea,
                            nextNode:nextNode,
                            nextSpman:nextSpman,
                            orderid:carOrderAuditFormView.orderid,
                            selectApprove:selectApprove
                        },
                        url: '/carOrder/auditOrderSubmit',
                        method: 'POST',
                        async: false,
                        success: function (resp) {
                            var respText = Ext.decode(resp.responseText);
                            XD.msg(respText.msg);
                            auditOrderGridView.getStore().reload();
                            auditOrderAdminView.setActiveItem(auditOrderGridView);
                        },
                        failure : function() {
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            'carOrderAuditFormView button[itemId=carOrderApproveClose],placeOrderAuditFormView button[itemId=placeOrderApproveClose]':{  //审核 关闭
                click: function (view) {
                    var auditOrderAdminView = view.findParentByType('auditOrderAdminView');
                    var auditOrderGridView = auditOrderAdminView.down('auditOrderGridView');
                    auditOrderAdminView.setActiveItem(auditOrderGridView);
                }
            },

            'placeOrderAuditFormView button[itemId=placeOrderApproveSubmit]':{  //提交 场地预约审核
                click:function(view){
                    var placeOrderAuditFormView = view.findParentByType('placeOrderAuditFormView');
                    var auditOrderAdminView = placeOrderAuditFormView.findParentByType('auditOrderAdminView');
                    var auditOrderGridView = auditOrderAdminView.down('auditOrderGridView');
                    var textArea = placeOrderAuditFormView.down('[itemId=approveId]').getValue();
                    var nextNode = placeOrderAuditFormView.down('[itemId=nextNodeId]').getValue();
                    var nextSpman = placeOrderAuditFormView.down('[itemId=nextSpmanId]').getValue();
                    var flowsText = placeOrderAuditFormView.data.auditlink;
                    var addprove = placeOrderAuditFormView.down('[itemId=addproveId]').getValue();
                    var selectApprove = placeOrderAuditFormView.down('[itemId=selectApproveId]').getValue();
                    if(selectApprove == '同意' && nextNode == null && nextSpman == null){
                        XD.msg('下一环节审批人不能为空');
                        return;
                    }

                    var curdate = getNowFormatDate();
                    var rname = window.parent.realname ? window.parent.realname : window.parent.parent.realname;
                    if (textArea != '') {
                        textArea += '\n\n意见：' + addprove + '\n' + flowsText + '：' + rname + '\n' + curdate;
                    } else {
                        textArea += '意见：' + addprove + '\n' + flowsText + '：' + rname + '\n' + curdate;
                    }
                    Ext.Ajax.request({
                        params: {
                            textArea:textArea,
                            nextNode:nextNode,
                            nextSpman:nextSpman,
                            orderid:placeOrderAuditFormView.orderid,
                            selectApprove:selectApprove
                        },
                        url: '/placeOrder/auditOrderSubmit',
                        method: 'POST',
                        async: false,
                        success: function (resp) {
                            var respText = Ext.decode(resp.responseText);
                            XD.msg(respText.msg);
                            auditOrderGridView.getStore().reload();
                            auditOrderAdminView.setActiveItem(auditOrderGridView);
                        },
                        failure : function() {
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            'auditOrderGridView button[itemId=backId],informOrderGridView button[itemId=backId]':{
                click:function (view) {
                    window.parent.location.href = "/index?sysType=10";
                }
            },

            'informOrderGridView':{
                itemclick:function(view,record){
                    var informs =record;
                    var win = Ext.create('AuditOrder.view.LookInformOrderView',{});
                    win.informOrderGridView = view;
                    win.show();
                    setTimeout(function () {
                        Ext.Ajax.request({
                            method: 'POST',
                            url: '/inform/getInform',
                            params:{
                                id:informs.get('id')
                            },
                            scope: this,
                            success: function (response, opts) {
                                var data = Ext.decode(response.responseText).data;
                                win.down('[itemId=title]').setText(data['title']);
                                win.down('[itemId=date]').setText('发布日期：'+new Date(data['informdate']).format("yyyy-MM-dd hh:mm:ss"));
                                document.getElementById('editFrame').contentWindow.setHtml(data['text']);
                                document.getElementById('editFrame').contentWindow.hideButton();
                                Ext.Ajax.request({
                                    method: 'POST',
                                    url: '/inform/clearInform',
                                    params:{
                                        id:informs.get('id')
                                    },
                                    scope: this,
                                    success: function (response, opts) {
                                    }
                                });
                            }
                        });
                    }, 100);
                }
            }
        });
    },
    //获取单据id
    getCarOrderid:function (taskid,type) {
        var docid;
        Ext.Ajax.request({
            url: '/carOrder/getCarOrderidByType',
            async:false,
            params:{
                taskid:taskid,
                type:type
            },
            success: function (response) {
                docid = Ext.decode(response.responseText);
            }
        });
        return docid;
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
