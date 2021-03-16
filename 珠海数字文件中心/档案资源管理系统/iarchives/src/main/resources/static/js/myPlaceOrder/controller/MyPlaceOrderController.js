/**
 * Created by Administrator on 2020/6/11.
 */


Ext.define('MyPlaceOrder.controller.MyPlaceOrderController', {
    extend: 'Ext.app.Controller',

    views: ['MyOrderGridView','PlaceOrderLookFromView', 'PlaceOrderLookGridView','PlaceOrderLookView'],//加载view
    stores: ['MyOrderGridStore','PlaceOrderLookGridStore'],//加载store
    models: ['MyOrderGridModel','PlaceOrderLookGridModel'],//加载model
    init: function () {
        this.control({
            'myOrderGridView': {
                afterrender: function (view) {
                    view.initGrid();
                }
            },
            'myOrderGridView button[itemId=look]':{   //我的预约 查看
                click:function (view) {
                    var myOrderGridView = view.findParentByType('myOrderGridView');
                    var select = myOrderGridView.getSelectionModel().getSelection();
                    if(select.length != 1){
                        XD.msg('只能选择一条数据');
                        return;
                    }
                    var state = select[0].get('state');
                    var floor=select[0].get('floor');
                    var placeName=select[0].get('placeName');
                    var placeOrderLookView = Ext.create('MyPlaceOrder.view.PlaceOrderLookView',{
                        title:'查看'+floor+placeName+'的预约'
                    });
                    var form = placeOrderLookView.down('form');
                    if(state=='取消预约'){
                        form.down('[itemId=canceluserId]').show();
                        form.down('[itemId=cancelreasonId]').show();
                        form.down('[itemId=canceltimeId]').show();
                        form.down('[itemId=cancedisId]').show();
                    }
                    var placeOrderLookGridView = placeOrderLookView.down('placeOrderLookGridView');
                    placeOrderLookGridView.initGrid({orderid:select[0].get('id')});
                    form.load({
                        url:'/placeOrder/placeOrderLookFormLoad',
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
                    placeOrderLookView.show();
                }
            },
            'placeOrderLookFromView button[itemId=lookOrderClose]':{   //查看 返回
                click:function (view) {
                    view.findParentByType('placeOrderLookView').close();
                }
            },
            'myOrderGridView [itemId=printId]': {//我的预约 打印
                click: function (btn) {
                    var reportGrid = btn.findParentByType('myOrderGridView');
                    var entryId=[];
                    var record = reportGrid.getSelection();
                    if(record.length>1||record.length<=0){
                        XD.msg('请选择一条记录');
                        return;
                    }
                    for(var i = 0; i < record.length; i++){
                        entryId.push(record[i].get('id'));
                    }
                    var params = {};
                    params['id'] = entryId;
                    XD.UReportPrint("查档申请管理_我的预约", "查档申请管理_我的预约", params);
                }
            },
            'myOrderGridView button[itemId=cancel]':{  //取消预约
                click:function (view) {
                    var myOrderGridView = view.findParentByType('myOrderGridView');
                    var select = myOrderGridView.getSelectionModel().getSelection();
                    if(select.length < 1){
                        XD.msg('请至少选择一条需要操作的数据');
                        return;
                    }
                    if(select[0].get('state')=='预约成功'){
                        XD.msg('只能取消预约成功的预约记录！');
                        return;
                    }
                    var myOrderCancelFormView = Ext.create('MyPlaceOrder.view.PlaceOrderCancelFormView');
                    var form = myOrderCancelFormView.down('form');
                    form.load({
                        url:'/placeOrder/placeOrderCancelFormLoad',
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
                }
            },
            'placeOrderCancelFormView button[itemId=cancelSubmit]':{ //取消预约 提交
                click:function (view) {
                    var myOrderCancelFormView = view.findParentByType('placeOrderCancelFormView');
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
                        url:'/placeOrder/placeOrderCancelFormSubmit',
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
            'placeOrderCancelFormView button[itemId=cancelClose]':{ //取消预约 关闭
                click:function (view) {
                    view.findParentByType('placeOrderCancelFormView').close();
                }
            },
        });
    }
});