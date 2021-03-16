/**
 * Created by Administrator on 2020/4/21.
 */


Ext.define('CarOrder.controller.CarOrderController', {
    extend: 'Ext.app.Controller',

    views: ['CarOrderView','CarOrderManageView','CarOrderManageGridView','CarOrderFormView',
    'CarOrderCancelFormView','CarOrderCancelFormView','CarOrderLookGridView','CarOrderLookFromView',
    'CarOrderLookView','CarOrderAuditGridView','CarOrderAuditFormView','CarOrderAdminView'],//加载view
    stores: ['CarOrderManageGridStore','OrderManageGridStore','ApproveManStore',
    'CarOrderNodeStore','CarOrderLookGridStore','CarOrderAuditGridStore','NextNodeStore',
    'ApproveOrganStore','NextSpmanStore'],//加载store
    models: ['CarOrderManageGridModel','OrderManageGridModel','CarOrderLookGridModel',
    'CarOrderAuditGridModel'],//加载model
    init: function () {
        this.control({
            'carOrderAdminView': {
                afterrender:function (view) {
                    if(iflag=='1'){
                        var carOrderAuditFormView = view.down('carOrderAuditFormView');
                        view.setActiveItem(carOrderAuditFormView);
                        var orderid = this.getCarOrderid();
                        var nextNode = carOrderAuditFormView.down('[itemId=nextNodeId]');
                        nextNode.getStore().proxy.extraParams.orderid = orderid;
                        nextNode.getStore().load();
                        var form = carOrderAuditFormView.down('form');
                        form.load({
                            url:'/carOrder/carOrderLookFormLoad',
                            method:'GET',
                            params:{
                                orderid: orderid
                            },
                            success:function (form,action) {
                                var data = Ext.decode(action.response.responseText).data;
                                carOrderAuditFormView.data = data;
                            },
                            failure:function () {
                                XD.msg('获取表单信息失败');
                            }
                        });
                        carOrderAuditFormView.orderid = orderid;
                    }
                }
            },

            'carOrderView': {
                afterrender: function (view) {
                    if(iflag=='1'){  //通知进入预约审核
                        view.setActiveItem(1);
                        var carOrderAuditGridView = view.down('carOrderAuditGridView');
                        carOrderAuditGridView.initGrid({taskid:taskId});
                    }else{
                        var carOrderManageGridView = view.down('carOrderManageGridView');
                        carOrderManageGridView.initGrid();
                        var southgrid = view.down('[itemId=southgrid]');
                        // southgrid.dataParams={
                        //     sort:"[{'property':'ordertime','direction':'desc'}]"
                        // };
                        //southgrid.initGrid();//覆盖了后面默认的参数
                        southgrid.getStore().reload();
                        //查询权限按钮
                        for(var i=0;i<functionButton.length;i+=2){
                            if (view.down('[itemId=' + functionButton[i].itemId + ']') != null) {
                                view.down('[itemId=' + functionButton[i].itemId + ']').show();
                            }
                        }
                        Ext.Ajax.request({//根据审批id判断是否可以催办
                            url: '/carOrder/findByWorkId',
                            method: 'GET',
                            success: function (resp) {
                                var respDate = Ext.decode(resp.responseText).data;
                                if(respDate.urgingstate=="1"){
                                    southgrid.down('[itemId=urging]').show();
                                    southgrid.down('[itemId=message]').show();
                                }
                            }
                        });
                    }
                },
                tabchange: function (view) {
                    var carOrderManageGridView;
                    if(view.activeTab.title=='预约'){
                        carOrderManageGridView = view.down('carOrderManageGridView');
                        carOrderManageGridView.initGrid();
                        var southgrid = view.down('[itemId=southgrid]');
                        southgrid.initGrid();
                    }else{
                        var carOrderAuditGridView = view.down('carOrderAuditGridView');
                        carOrderAuditGridView.initGrid();
                    }
                }
            },
            'carOrderManageView [itemId=southgrid] [itemId=add]':{
                click:function (view) {
                    var carOrderManageView = view.findParentByType('carOrderManageView');
                    var carOrderManageGridView = carOrderManageView.down('carOrderManageGridView');
                    var select = carOrderManageGridView.getSelectionModel().getSelection();
                    var southgrid = carOrderManageView.down('[itemId=southgrid]');
                    if(select.length != 1){
                        XD.msg('只能选择一辆公车');
                        return;
                    }
                    if(select[0].get('state')=='保养中'||select[0].get('state')=='维修中'){
                        XD.msg('公车处于保养中或者修车中状态');
                        return;
                    }
                    var title = select[0].get('carnumber')+'公车预约';
                    var carid = select[0].get('id');
                    var carOrderFormView = Ext.create('CarOrder.view.CarOrderFormView');
                    carOrderFormView.title = title;
                    carOrderFormView.down('[itemId=auditlinkId]').getStore().reload();
                    var form = carOrderFormView.down('form');
                    form.load({
                        url:'/carOrder/carOrderFormLoad',
                        method:'GET',
                        success:function () {
                        },
                        failure:function () {
                            XD.msg('获取表单信息失败');
                        }
                    });
                    carOrderFormView.carid = carid;
                    carOrderFormView.southgrid = southgrid;
                    carOrderFormView.show();
                }
            },
            'carOrderFormView button[itemId=carOrderSubmit]':{   //提交预约
                click:function (view) {
                    var carOrderFormView = view.findParentByType('carOrderFormView');
                    var form = carOrderFormView.down('form');
                    var starttime = carOrderFormView.down('[name=starttime]').getValue();
                    var endtime = carOrderFormView.down('[name=endtime]').getValue();
                    var spnodeid = form.down('[itemId=auditlinkId]').getValue();
                    var spmanid = form.down('[itemId=spmanId]').getValue();
                    if(starttime>endtime){
                        XD.msg('开始时间不能大于结束时间');
                        return;
                    }
                    if(!form.isValid()){
                        XD.msg('存在必填项没有填写');
                        return;
                    }
                    Ext.MessageBox.wait('正在提交请稍后...','提示');
                    form.submit({
                        url:'/carOrder/carOrderFormSubmit',
                        method:'POST',
                        params:{
                            spnodeid:spnodeid,
                            spmanid:spmanid,
                            carid:carOrderFormView.carid
                        },
                        success:function (form,action) {
                            Ext.MessageBox.hide();
                            var respText = Ext.decode(action.response.responseText);
                            if(respText.data){
                                var carorder = respText.data;
                                var text;
                                if(carorder.length>=2){
                                    text =  '提交失败！'+carorder[0].starttime+' 至 '+carorder[0].endtime+' 已被 '+carorder[0].caruser+' 预约、'+
                                        carorder[1].starttime+' 至 '+carorder[1].endtime+' 已被 '+carorder[0].caruser+' 预约，请另选用车时间段！';
                                }else{
                                    text =  '提交失败！'+carorder[0].starttime+' 至 '+carorder[0].endtime+' 已被 '+carorder[0].caruser+' 预约，请另选用车时间段！';
                                }
                                XD.msg(text);
                            }else{
                                XD.msg('预约成功');
                                carOrderFormView.southgrid.getStore().reload();
                                carOrderFormView.close();
                            }
                        },
                        failure:function () {
                            Ext.MessageBox.hide();
                            XD.msg('操作失败')
                        }
                    });
                }
            },

            'carOrderFormView button[itemId=carOrderClose]':{
                click:function (view) {
                    view.findParentByType('carOrderFormView').close();
                }
            },

            'carOrderManageView [itemId=southgrid] [itemId=cancel]':{   //取消预约
                click:function (view) {
                    var carOrderManageView = view.findParentByType('carOrderManageView');
                    var southgrid = carOrderManageView.down('[itemId=southgrid]');
                    var select = southgrid.getSelectionModel().getSelection();
                    if(select.length != 1){
                        XD.msg('只能选择一条数据');
                        return;
                    }
                    if(orderAuditState!='true'){
                        XD.msg('无权限取消别人的预约单！');
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
                        var carOrderCancelFormView = Ext.create('CarOrder.view.CarOrderCancelFormView');
                        var form = carOrderCancelFormView.down('form');
                        form.load({
                            url: '/carOrder/carOrderCancelFormLoad',
                            method: 'GET',
                            success: function () {
                            },
                            failure: function () {
                                XD.msg('获取表单信息失败');
                            }
                        });
                        carOrderCancelFormView.orderid = select[0].get('id');
                        carOrderCancelFormView.southgrid = southgrid;
                        carOrderCancelFormView.show();
                    }else if(select[0].get('returnstate').search('未归还')!=-1&&select[0].get('state').search('预约成功')!=-1){
                        XD.msg('不可取消未归还的预约记录！请先进行归还！');
                        return;
                    }else  if(select[0].get('returnstate').search('已归还')!=-1&&select[0].get('state').search('预约成功')!=-1){
                        XD.msg('已完成预约流程，不可取消预约！');
                        return;
                    }
                }
            },

            'carOrderCancelFormView button[itemId=cancelSubmit]':{ //取消预约 提交
                click:function (view) {
                    var carOrderCancelFormView = view.findParentByType('carOrderCancelFormView');
                    var form = carOrderCancelFormView.down('form');
                    var canceluser = carOrderCancelFormView.down('[name=canceluser]').getValue();
                    var canceltime = carOrderCancelFormView.down('[name=canceltime]').getValue();
                    var cancelreason = carOrderCancelFormView.down('[name=cancelreason]').getValue();
                    if(!form.isValid()){
                        XD.msg('存在必填项没有填写');
                        return;
                    }
                    Ext.MessageBox.wait('正在提交请稍后...','提示');
                    form.submit({
                        url:'/carOrder/carOrderCancelFormSubmit',
                        method:'POST',
                        params:{
                            orderid:carOrderCancelFormView.orderid,
                            canceluser:canceluser,
                            canceltime:canceltime,
                            cancelreason:cancelreason
                        },
                        success:function (form,action) {
                            Ext.MessageBox.hide();
                            var respText = Ext.decode(action.response.responseText);
                            if(respText.data){
                                XD.msg('取消预约成功');
                                carOrderCancelFormView.southgrid.getStore().reload();
                                carOrderCancelFormView.close();
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

            'carOrderCancelFormView button[itemId=cancelClose]':{ //取消预约 关闭
                click:function (view) {
                    view.findParentByType('carOrderCancelFormView').close();
                }
            },

            'carOrderManageView [itemId=southgrid] [itemId=del]':{   //删除预约
                click:function (view) {
                    var carOrderManageView = view.findParentByType('carOrderManageView');
                    var southgrid = carOrderManageView.down('[itemId=southgrid]');
                    var select = southgrid.getSelectionModel().getSelection();
                    if(select.length < 1){
                        XD.msg('请至少选择一条数据');
                        return;
                    }
                    var orderids = [];
                    for(var i=0;i<select.length;i++){
                        if(select[0].get('state').search('预约成功')!=-1){
                            XD.msg('不能删除预约成功的单据，请先取消预约！');
                            return;
                        }else if(select[0].get('state').search('提交预约')!=-1||select[0].get('state').search('审批中')!=-1){
                            XD.msg('不能删除正在预约审批的单据，请先取消预约！');
                            return;
                        }
                        orderids.push(select[i].get('id'));
                    }
                    XD.confirm('是否删除这'+orderids.length+'条数据',function () {
                        Ext.Ajax.request({
                            url:'/carOrder/carOrderDelete',
                            method:'POST',
                            params:{
                                orderids:orderids
                            },
                            success:function (rep) {
                                var respText = Ext.decode(rep.responseText);
                                if(!respText.success){
                                    XD.msg('删除失败');
                                }else{
                                    XD.msg('删除成功');
                                    southgrid.getStore().reload();
                                }
                            },
                            failure:function () {
                                XD.msg('操作失败');
                            }
                        });
                    });
                }
            },

            'carOrderManageView [itemId=southgrid] [itemId=look]':{   //查看预约
                click:function (view) {
                    var carOrderManageView = view.findParentByType('carOrderManageView');
                    var southgrid = carOrderManageView.down('[itemId=southgrid]');
                    var select = southgrid.getSelectionModel().getSelection();
                    if(select.length != 1){
                        XD.msg('只能选择一条数据');
                        return;
                    }
                    var state = select[0].get('state');
                    var carOrderLookView = Ext.create('CarOrder.view.CarOrderLookView');
                    var form = carOrderLookView.down('form');
                    if(state.search('取消预约')!=-1){
                        form.down('[itemId=canceluserId]').show();
                        form.down('[itemId=cancelreasonId]').show();
                        form.down('[itemId=canceltimeId]').show();
                        form.down('[itemId=cancedisId]').show();
                    }
                    var carOrderLookGridView = carOrderLookView.down('carOrderLookGridView');
                    carOrderLookGridView.initGrid({orderid:select[0].get('id')});
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
                    carOrderLookView.show();
                }
            },
            'carOrderManageView button[itemId=lend]': {//借出
                click: function (view) {
                    var carOrderManageView = view.findParentByType('carOrderManageView');
                    var southgrid = carOrderManageView.down('[itemId=southgrid]');
                    var details = southgrid.getSelectionModel().getSelection();
                    if (details.length != 1) {
                        XD.msg('请选择一条数据!');
                        return;
                    }
                    if(details[0].get("state").search("预约成功")==-1){
                        XD.msg('预约成功才可借出!');
                        return;
                    }
                    Ext.MessageBox.wait('正在处理请稍后...');
                    Ext.Ajax.request({
                        params: {
                            ordercode: details[0].get("id"),
                            state: '未归还'
                        },
                        url: '/carOrder/updateReturnstate',
                        method: 'POST',
                        success: function (response) {
                            Ext.MessageBox.hide();
                            var respText = Ext.decode(response.responseText);
                            XD.msg(respText.msg);
                            southgrid.getStore().reload();
                        },
                        failure: function () {
                            Ext.MessageBox.hide();
                            XD.msg('操作失败');
                        }
                    });
                }
            },
            'carOrderManageView button[itemId=urging]': {//催办
                click: function (view) {
                    var carOrderManageView = view.findParentByType('carOrderManageView');
                    var southgrid = carOrderManageView.down('[itemId=southgrid]');
                    var details = southgrid.getSelectionModel().getSelection();
                    if(details.length!=1){
                        XD.msg('请选择一条数据!');
                        return;
                    }
                    if(details[0].get("state").search("提交预约")!=-1||details[0].get("state").search("审批中")!=-1) {
                        Ext.MessageBox.wait('正在处理请稍后...');
                        Ext.Ajax.request({
                            params: {
                                ordercode: details[0].get("ordercode"),
                                sendMsg: carOrderManageView.down("[itemId=message]").checked
                            },
                            url: '/carOrder/manualUrging',
                            method: 'POST',
                            sync: true,
                            success: function (response) {
                                Ext.MessageBox.hide();
                                var respText = Ext.decode(response.responseText);
                                XD.msg(respText.msg);
                            },
                            failure: function () {
                                Ext.MessageBox.hide();
                                XD.msg('操作失败');
                            }
                        });
                    }else {
                        XD.msg('请选择正确的数据催办!');
                        return;
                    }
                }
            },
            'carOrderLookView button[itemId=lookOrderClose]':{  // 查看预约 关闭
                click:function (view) {
                    view.findParentByType('window').close();
                }
            },

            'carOrderAuditGridView button[itemId=carOrderAudit]':{  // 审核
                click:function (view) {
                    var carOrderAuditGridView = view.findParentByType('carOrderAuditGridView');
                    var select = carOrderAuditGridView.getSelectionModel().getSelection();
                    if(select.length != 1){
                        XD.msg('只能选择一条数据');
                        return;
                    }
                    var auditView = Ext.create('Ext.window.Window', {
                        width: '100%',
                        height: '100%',
                        draggable: false,//禁止拖动
                        resizable: false,//禁止缩放
                        modal: true,
                        closeToolText: '关闭',
                        title: '审核预约',
                        closeAction: 'hide',
                        layout: 'fit',
                        items: [{
                            xtype: 'carOrderAuditFormView'
                        }]
                    });
                    var carOrderLookView = auditView.down('carOrderAuditFormView');
                    var nextNode = carOrderLookView.down('[itemId=nextNodeId]');
                    nextNode.getStore().proxy.extraParams.orderid = select[0].get('id');
                    nextNode.getStore().load();
                    var form = carOrderLookView.down('form');
                    form.load({
                        url:'/carOrder/carOrderLookFormLoad',
                        method:'GET',
                        params:{
                            orderid: select[0].get('id')
                        },
                        success:function (form,action) {
                            var data = Ext.decode(action.response.responseText).data;
                            carOrderLookView.data = data;
                        },
                        failure:function () {
                            XD.msg('获取表单信息失败');
                        }
                    });
                    carOrderLookView.orderid = select[0].get('id');
                    carOrderLookView.carOrderAuditGridView = carOrderAuditGridView;
                    auditView.show();
                }
            },

            'carOrderAuditFormView button[itemId=carOrderApproveSubmit]':{  //提交审核
                click:function(view){
                    var carOrderAuditFormView = view.findParentByType('carOrderAuditFormView');
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

                    /*if(carOrderAuditFormView.down('[itemId=nextNodeId]').rawValue!='结束'
                        &&(carOrderAuditFormView.down('[itemId=nextSpmanId]').rawValue=='')){
                        XD.msg('下一环节审批人不能为空');
                        return;
                    }*/

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
                            Ext.defer(function(){
                                if(iflag=='0'){
                                    carOrderAuditFormView.findParentByType('window').close();
                                    carOrderAuditFormView.carOrderAuditGridView.getStore().reload();
                                }else{
                                    parent.closeObj.close(parent.layer.getFrameIndex(window.name));
                                }
                            },1000);
                        },
                        failure : function() {
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            'carOrderAuditFormView button[itemId=carOrderApproveClose]':{  //审核 关闭
                click: function (view) {
                    if (iflag == '0') {
                        view.findParentByType('window').close();
                    } else {
                        parent.closeObj.close(parent.layer.getFrameIndex(window.name));
                    }
                }
            },

            'carOrderManageView [itemId=northgrid]':{
                itemclick: this.itemclickHandler
            }
        });
    },
    //获取单据id
    getCarOrderid:function () {
        var docid;
        Ext.Ajax.request({
            url: '/carOrder/getCarOrderid',
            async:false,
            params:{
                taskid:taskId
            },
            success: function (response) {
                docid = Ext.decode(response.responseText);
            }
        });
        return docid;
    },

    //勾选公车管理条目时，显示公车订单
    itemclickHandler: function (view, record, item, index, e) {
        var carId = record.get('id');
        var v = view.up('carOrderManageView').down('[itemId=southgrid]');
        var selectTime = v.down('[itemId=selectTimeId]').getSelection();
        var text = '所选车辆车牌号 '+record.get('carnumber')+'，所选时间范围为 '+selectTime.get('text');
        v.setTitle(text);
        v.initGrid({id:carId,selectTime:selectTime.get('value')});
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
