/**
 * Created by Administrator on 2020/4/27.
 */

Ext.define('MyOrder.controller.MyOrderController', {
    extend: 'Ext.app.Controller',

    views: ['MyOrderGridView','MyOrderLookView','MyOrderLookGridView','MyOrderLookFormView'],//加载view
    stores: ['MyOrderGridStore','MyOrderLookGridStore'],//加载store
    models: ['MyOrderGridModel','MyOrderLookGridModel'],//加载model
    init: function () {
        this.control({
            'myOrderGridView': {
                afterrender: function (view) {
                    view.initGrid();
                }
            },

            'myOrderGridView button[itemId=look]': {  //查看
                click: function (view) {
                    var myOrderGridView = view.findParentByType('myOrderGridView');
                    var select = myOrderGridView.getSelectionModel().getSelection();
                    if(select.length!=1){
                        XD.msg('只能选择一条数据');
                        return;
                    }
                    var carNumber= select[0].get('carnumber');
                    var state = select[0].get('state');
                    var myOrderLookView = Ext.create('MyOrder.view.MyOrderLookView',{
                        title:"查看"+carNumber+"的预约"
                    });
                    var form = myOrderLookView.down('form');
                    if(state.search('取消预约')!=-1){
                        form.down('[itemId=canceluserId]').show();
                        form.down('[itemId=cancelreasonId]').show();
                        form.down('[itemId=canceltimeId]').show();
                        form.down('[itemId=cancedisId]').show();
                    }
                    var myOrderLookGridView = myOrderLookView.down('myOrderLookGridView');
                    myOrderLookGridView.initGrid({orderid:select[0].get('id')});
                    form.load({
                        url:'/carOrder/carOrderLookFormLoad',
                        method:'GET',
                        params:{
                            orderid: select[0].get('id')
                        },
                        success:function () {
                        },
                        failure:function () {
                            XD.msg('获取表单信息失败');
                        }
                    });
                    myOrderLookView.show();
                }
            },

            'myOrderLookFormView button[itemId=myOrderClose]':{  //查看 关闭
                click:function (view) {
                    view.findParentByType('window').close();
                }
            },
            'myOrderGridView button[itemId=printId]':{//我的预约 打印
                click: function (btn) {
                    var reportGrid = btn.findParentByType('myOrderGridView');
                    var entryId=[];
                    var record = reportGrid.getSelection();
                    if(record.length>1 || record.length<=0){
                        XD.msg('请选择一条记录');
                        return;
                    }
                    for(var i = 0; i < record.length; i++){
                        entryId.push(record[i].get('id'));
                    }
                    var params = {};
                    params['id'] = entryId;
                    XD.UReportPrint("查档申请管理_我的公车预约","查档申请管理_我的公车预约",params);
                }
            },
            'myOrderGridView button[itemId=cancel]':{  //取消预约
                click:function (view) {
                    var myOrderGridView = view.findParentByType('myOrderGridView');
                    var select = myOrderGridView.getSelectionModel().getSelection();
                    if(select.length != 1){
                        XD.msg('只能选择一条数据');
                        return;
                    }
                    if(select[0].get('state').search('提交预约')!=-1||select[0].get('state').search('审批中')!=-1){
                        XD.msg('只能取消预约成功的预约记录！');
                        return;
                    }
                    if(select[0].get('state').search('预约失败')!=-1){
                        XD.msg('只能取消预约成功的预约记录！其它预约状态的可直接删除预约！');
                        return;
                    }
                    if(select[0].get('state').search('取消预约')!=-1){
                        XD.msg('该预约已取消，请勿重复取消！');
                        return;
                    }
                    if(select[0].get('returnstate')==''&&select[0].get('state').search('预约成功')!=-1) {
                        var myOrderCancelFormView = Ext.create('MyOrder.view.MyOrderCancelFormView');
                        var form = myOrderCancelFormView.down('form');
                        form.load({
                            url:'/carOrder/carOrderCancelFormLoad',
                            method:'GET',
                            success:function () {
                            },
                            failure:function () {
                                XD.msg('获取表单信息失败');
                            }
                        });
                        myOrderCancelFormView.orderid = select[0].get('id');
                        myOrderCancelFormView.southgrid = myOrderGridView;
                        myOrderCancelFormView.show();
                    }else if(select[0].get('returnstate').search('未归还')!=-1&&select[0].get('state').search('预约成功')!=-1){
                        XD.msg('不可取消未归还的预约记录！请先进行归还！');
                        return;
                    }else  if(select[0].get('returnstate').search('已归还')!=-1&&select[0].get('state').search('预约成功')!=-1){
                        XD.msg('已完成预约流程，不可取消预约！');
                        return;
                    }

                }
            },
            'myOrderCancelFormView button[itemId=cancelSubmit]':{ //取消预约 提交
                click:function (view) {
                    var myOrderCancelFormView = view.findParentByType('myOrderCancelFormView');
                    var form = myOrderCancelFormView.down('form');
                    var canceluser = myOrderCancelFormView.down('[name=canceluser]').getValue();
                    var canceltime = myOrderCancelFormView.down('[name=canceltime]').getValue();
                    var cancelreason = myOrderCancelFormView.down('[name=cancelreason]').getValue();
                    if(!form.isValid()){
                        XD.msg('存在必填项没有填写');
                        return;
                    }
                    Ext.MessageBox.wait('正在提交请稍后...','提示');
                    form.submit({
                        url:'/carOrder/carOrderCancelFormSubmit',
                        method:'POST',
                        params:{
                            orderid:myOrderCancelFormView.orderid,
                            canceluser:canceluser,
                            canceltime:canceltime,
                            cancelreason:cancelreason
                        },
                        success:function (form,action) {
                            Ext.MessageBox.hide();
                            var respText = Ext.decode(action.response.responseText);
                            if(respText.success){
                                XD.msg('取消预约成功');
                                myOrderCancelFormView.southgrid.getStore().reload();
                                myOrderCancelFormView.close();
                            }else{
                                XD.msg('取消预约失败');
                            }
                        },
                        failure:function () {
                            Ext.MessageBox.hide();
                            XD.msg('操作失败')
                        }
                    });
                }
            },
            'myOrderCancelFormView button[itemId=cancelClose]':{ //取消预约 关闭
                click:function (view) {
                    view.findParentByType('myOrderCancelFormView').close();
                }
            },
            'myOrderGridView button[itemId=returnId]': {  //归还
                click: function (view) {
                    var myOrderGridView = view.findParentByType('myOrderGridView');
                    var select = myOrderGridView.getSelectionModel().getSelection();
                    if(select.length < 1){
                        XD.msg('请至少选择一条需要操作的数据');
                        return;
                    }
                    var orderids = [];
                    var flag = false;
                    for(var i=0;i<select.length;i++){
                        if(select[i].get('state').search('预约成功')==-1||select[i].get('returnstate').search('未归还')==-1){
                            flag = true;
                            break;
                        }
                        orderids.push(select[i].get('id'));
                    }
                    if(flag){
                        XD.msg('存在公车预约状态不是预约成功或者不是未归还状态');
                        return;
                    }

                    XD.confirm("确定归还这 "+orderids.length+" 条公车预约",function () {
                        Ext.Ajax.request({
                            params: {
                                orderids:orderids
                            },
                            url: '/myOrder/returnUserOrder',
                            method: 'POST',
                            async: false,
                            success: function (resp) {
                                XD.msg('归还成功');
                                myOrderGridView.getStore().reload();
                            },
                            failure : function() {
                                XD.msg('操作失败');
                            }
                        });
                    });
                }
            }
        });
    }
});