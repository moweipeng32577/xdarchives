/**
 * Created by Administrator on 2020/4/28.
 */


Ext.define('PlaceOrder.controller.PlaceOrderController', {
    extend: 'Ext.app.Controller',

    views: ['PlaceOrderView','PlaceOrderManageView','PlaceOrderManageGridView','PlaceOrderLookView',
        'PlaceOrderLookGridView','PlaceOrderLookFromView','PlaceOrderFormView',
        'PlaceOrderCancelFormView','PlaceOrderAuditGridView','PlaceOrderAuditFormView',
        'PlaceOrderAdminView'],//加载view
    stores: ['PlaceOrderManageGridStore','PlaceOrderGridStore','PlaceOrderNodeStore',
        'PlaceOrderLookGridStore','ApproveManStore','NextNodeStore','PlaceOrderAuditGridStore',
        'ApproveOrganStore','NextSpmanStore'],//加载store
    models: ['PlaceOrderManageGridModel','PlaceOrderGridModel','PlaceOrderLookGridModel',
        'PlaceOrderAuditGridModel'],//加载model
    init: function () {
        this.control({
            'placeOrderAdminView': {
                afterrender:function (view) {
                    if(iflag=='1'){
                        var placeOrderAuditFormView = view.down('placeOrderAuditFormView');
                        view.setActiveItem(placeOrderAuditFormView);
                        var orderid = this.getPlaceOrderid();
                        var nextNode = placeOrderAuditFormView.down('[itemId=nextNodeId]');
                        nextNode.getStore().proxy.extraParams.orderid = orderid;
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
                    }
                }
            },

            'placeOrderView': {
                afterrender: function (view) {
                    if(iflag=='1'){  //通知进入预约审核
                        view.setActiveItem(1);
                        var placeOrderAuditGridView = view.down('placeOrderAuditGridView');
                        placeOrderAuditGridView.initGrid({taskid:taskId});
                    }else {
                        var placeOrderManageGridView = view.down('placeOrderManageGridView');
                        window.placeOrderManageGridView = placeOrderManageGridView;
                        placeOrderManageGridView.initGrid();
                        var southgrid = view.down('[itemId=southgrid]');
                        //southgrid.initGrid();//覆盖了后面的默认的参数
                        southgrid.getStore().reload();
                        //查询权限按钮
                        for(var i=0;i<functionButton.length;i+=2){
                            if (view.down('[itemId=' + functionButton[i].itemId + ']') != null) {
                                view.down('[itemId=' + functionButton[i].itemId + ']').show();
                            }
                        }
                    }
                },
                tabchange: function (view) {
                    if(view.activeTab.title=='预约'){
                        var placeOrderManageGridView = view.down('placeOrderManageGridView');
                        placeOrderManageGridView.initGrid();
                        var southgrid = view.down('[itemId=southgrid]');
                        southgrid.initGrid();
                    }else{
                        var placeOrderAuditGridView = view.down('placeOrderAuditGridView');
                        placeOrderAuditGridView.initGrid();
                    }
                }
            },
            'placeOrderManageView [itemId=southgrid] [itemId=add]':{  //新增场地预约
                click:function (view) {
                    var placeOrderManageView = view.findParentByType('placeOrderManageView');
                    var placeOrderManageGridView = placeOrderManageView.down('placeOrderManageGridView');
                    var select = placeOrderManageGridView.getSelectionModel().getSelection();
                    var southgrid = placeOrderManageView.down('[itemId=southgrid]');
                    if(select.length != 1){
                        XD.msg('只能选择一个场地');
                        return;
                    }
                    if(select[0].get('state')=='维修中'){
                        XD.msg('场地处于维修中状态');
                        return;
                    }
                    var title = select[0].get('floor')+'场地预约';
                    var placeid = select[0].get('id');
                    var placeOrderFormView = Ext.create('PlaceOrder.view.PlaceOrderFormView');
                    placeOrderFormView.title = title;
                    placeOrderFormView.down('[itemId=auditlinkId]').getStore().load();
                    var form = placeOrderFormView.down('form');
                    form.load({
                        url:'/placeOrder/placeOrderFormLoad',
                        method:'GET',
                        success:function () {
                        },
                        failure:function () {
                            XD.msg('获取表单信息失败');
                        }
                    });
                    placeOrderFormView.placeid = placeid;
                    placeOrderFormView.southgrid = southgrid;
                    placeOrderFormView.show();
                }
            },
            'placeOrderFormView button[itemId=placeOrderSubmit]':{   //提交预约
                click:function (view) {
                    var placeOrderFormView = view.findParentByType('placeOrderFormView');
                    var form = placeOrderFormView.down('form');
                    var starttime = placeOrderFormView.down('[name=starttime]').getValue();
                    var endtime = placeOrderFormView.down('[name=endtime]').getValue();
                    var spnodeid = form.down('[itemId=auditlinkId]').getValue();
                    var spmanid = form.down('[itemId=spmanId]').getValue();
                    if(starttime>endtime){
                        XD.msg('开始时间不能大于结束时间');
                        return;
                    }
                    if(endtime<new Date()){
                        XD.msg('不能预约过去的时间');
                        return;
                    }
                    if(!form.isValid()){
                        XD.msg('存在必填项没有填写');
                        return;
                    }
                    Ext.MessageBox.wait('正在提交请稍后...','提示');
                    form.submit({
                        url:'/placeOrder/placeOrderFormSubmit',
                        method:'POST',
                        params:{
                            spnodeid:spnodeid,
                            spmanid:spmanid,
                            placeid:placeOrderFormView.placeid
                        },
                        success:function (form,action) {
                            Ext.MessageBox.hide();
                            var respText = Ext.decode(action.response.responseText);
                            if(respText.data){
                                var carorder = respText.data;
                                var text;
                                if(carorder.length>=2){
                                    text =  '提交失败！'+carorder[0].starttime+' 至 '+carorder[0].endtime+' 已被 '+carorder[0].placeuser+' 预约、'+
                                        carorder[1].starttime+' 至 '+carorder[1].endtime+' 已被 '+carorder[0].placeuser+' 预约，请另选使用场地时间段！';
                                }else{
                                    text =  '提交失败！'+carorder[0].starttime+' 至 '+carorder[0].endtime+' 已被 '+carorder[0].placeuser+' 预约，请另选使用场地时间段！';
                                }
                                XD.msg(text);
                            }else{
                                XD.msg('预约成功');
                                placeOrderFormView.southgrid.getStore().reload();
                                placeOrderFormView.close();
                            }
                        },
                        failure:function () {
                            Ext.MessageBox.hide();
                            XD.msg('操作失败')
                        }
                    });
                }
            },

            'placeOrderFormView button[itemId=placeOrderClose]':{
                click:function (view) {
                    view.findParentByType('placeOrderFormView').close();
                }
            },

            'placeOrderManageView [itemId=southgrid] [itemId=cancel]':{   //取消预约
                click:function (view) {
                    var placeOrderManageView = view.findParentByType('placeOrderManageView');
                    var southgrid = placeOrderManageView.down('[itemId=southgrid]');
                    var select = southgrid.getSelectionModel().getSelection();
                    if(select.length != 1){
                        XD.msg('只能选择一条数据');
                        return;
                    }
                    if(select[0].get('state').search('预约成功')==-1){
                        XD.msg('该场地预约状态不是预约成功');
                        return;
                    }
                    if(orderAuditState!='true'){
                        XD.msg('无权限取消别人的预约单！');
                        return;
                    }
                    if(select[0].get('returnstate').search('未归还')==-1){
                        XD.msg('该场地预约已归还');
                        return;
                    }
                    var placeOrderCancelFormView = Ext.create('PlaceOrder.view.PlaceOrderCancelFormView');
                    var form = placeOrderCancelFormView.down('form');
                    form.load({
                        url:'/placeOrder/placeOrderCancelFormLoad',
                        method:'GET',
                        success:function () {
                        },
                        failure:function () {
                            XD.msg('获取表单信息失败');
                        }
                    });
                    placeOrderCancelFormView.orderid = select[0].get('id');
                    placeOrderCancelFormView.southgrid = southgrid;
                    placeOrderCancelFormView.show();
                }
            },

            'placeOrderCancelFormView button[itemId=cancelSubmit]':{ //取消预约 提交
                click:function (view) {
                    var placeOrderCancelFormView = view.findParentByType('placeOrderCancelFormView');
                    var form = placeOrderCancelFormView.down('form');
                    var canceluser = placeOrderCancelFormView.down('[name=canceluser]').getValue();
                    var canceltime = placeOrderCancelFormView.down('[name=canceltime]').getValue();
                    var cancelreason = placeOrderCancelFormView.down('[name=cancelreason]').getValue();
                    if(!form.isValid()){
                        XD.msg('存在必填项没有填写');
                        return;
                    }
                    Ext.MessageBox.wait('正在提交请稍后...','提示');
                    form.submit({
                        url:'/placeOrder/placeOrderCancelFormSubmit',
                        method:'POST',
                        params:{
                            orderid:placeOrderCancelFormView.orderid,
                            canceluser:canceluser,
                            canceltime:canceltime,
                            cancelreason:cancelreason
                        },
                        success:function (form,action) {
                            Ext.MessageBox.hide();
                            var respText = Ext.decode(action.response.responseText);
                            if(respText.data){
                                XD.msg('取消预约成功');
                                placeOrderCancelFormView.southgrid.getStore().reload();
                                placeOrderCancelFormView.close();
                                window.placeOrderManageGridView.getStore().loadPage(1);
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
            'placeOrderManageView [itemId=southgrid] [itemId=del]':{   //删除预约
                click:function (view) {
                    var placeOrderManageView = view.findParentByType('placeOrderManageView');
                    var southgrid = placeOrderManageView.down('[itemId=southgrid]');
                    var select = southgrid.getSelectionModel().getSelection();
                    if(select.length < 1){
                        XD.msg('请至少选择一条数据');
                        return;
                    }
                    var flag = false;
                    var orderids = [];
                    for(var i=0;i<select.length;i++){
                        if(select[0].get('state').search('取消预约')==-1&&select[0].get('state').search('预约失败')==-1){
                            flag = true;
                            break;
                        }
                        orderids.push(select[i].get('id'));
                    }
                    if(flag){
                        XD.msg('存在选择的预约记录不是取消预约或者预约失败状态，请先取消预约！');
                        return;
                    }
                    XD.confirm('是否删除这'+orderids.length+'条数据',function () {
                        Ext.Ajax.request({
                            url:'/placeOrder/placeOrderDelete',
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

            'placeOrderManageView [itemId=southgrid] [itemId=look]':{   //查看预约
                click:function (view) {
                    var placeOrderManageView = view.findParentByType('placeOrderManageView');
                    var southgrid = placeOrderManageView.down('[itemId=southgrid]');
                    var select = southgrid.getSelectionModel().getSelection();
                    if(select.length != 1){
                        XD.msg('只能选择一条数据');
                        return;
                    }
                    var state = select[0].get('state');
                    var placeOrderLookView = Ext.create('PlaceOrder.view.PlaceOrderLookView');
                    var form = placeOrderLookView.down('form');
                    if(state.search('取消预约')!=-1){
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
            'placeOrderLookView button[itemId=lookOrderClose]':{  // 查看预约 关闭
                click:function (view) {
                    view.findParentByType('window').close();
                }
            },

            'placeOrderAuditGridView button[itemId=placeOrderAudit]':{  // 审核
                click:function (view) {
                    var placeOrderAuditGridView = view.findParentByType('placeOrderAuditGridView');
                    var select = placeOrderAuditGridView.getSelectionModel().getSelection();
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
                            xtype: 'placeOrderAuditFormView'
                        }]
                    });
                    var placeOrderAuditFormView = auditView.down('placeOrderAuditFormView');
                    var nextNode = placeOrderAuditFormView.down('[itemId=nextNodeId]');
                    nextNode.getStore().proxy.extraParams.orderid = select[0].get('id');
                    nextNode.getStore().load();
                    var form = placeOrderAuditFormView.down('form');
                    form.load({
                        url:'/placeOrder/placeOrderLookFormLoad',
                        method:'GET',
                        params:{
                            orderid: select[0].get('id')
                        },
                        success:function (form,action) {
                            var data = Ext.decode(action.response.responseText).data;
                            placeOrderAuditFormView.data = data;
                        },
                        failure:function () {
                            XD.msg('获取表单信息失败');
                        }
                    });
                    placeOrderAuditFormView.orderid = select[0].get('id');
                    placeOrderAuditFormView.placeOrderAuditGridView = placeOrderAuditGridView;
                    auditView.show();
                }
            },

            'placeOrderAuditFormView button[itemId=placeOrderApproveSubmit]':{  //提交审核
                click:function(view){
                    var placeOrderAuditFormView = view.findParentByType('placeOrderAuditFormView');
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

                    /*if(placeOrderAuditFormView.down('[itemId=nextNodeId]').rawValue!='结束'
                        &&(placeOrderAuditFormView.down('[itemId=nextSpmanId]').rawValue=='')){
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
                            orderid:placeOrderAuditFormView.orderid,
                            selectApprove:selectApprove
                        },
                        url: '/placeOrder/auditOrderSubmit',
                        method: 'POST',
                        async: false,
                        success: function (resp) {
                            var respText = Ext.decode(resp.responseText);
                            XD.msg(respText.msg);
                            Ext.defer(function(){
                                if(iflag=='0'){
                                    view.findParentByType('window').close();
                                    placeOrderAuditFormView.placeOrderAuditGridView.getStore().reload();
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

            'placeOrderAuditFormView button[itemId=placeOrderApproveClose]':{  //审核 关闭
                click:function(view){
                    if(iflag=='0'){
                        view.findParentByType('window').close();
                    }else{
                        parent.closeObj.close(parent.layer.getFrameIndex(window.name));
                    }
                }
            },

            'placeOrderManageView [itemId=northgrid]':{
                itemclick: this.itemclickHandler
            }
        });
    },

    //勾选场地管理条目时，显示场地订单
    itemclickHandler: function (view, record, item, index, e) {
        var placeId = record.get('id');
        var v = view.up('placeOrderManageView').down('[itemId=southgrid]');
        var selectTime = v.down('[itemId=selectTimeId]').getSelection();
        var text = '所选场地楼层为 '+record.get('floor')+'，所选时间范围为 '+selectTime.get('text');
        v.setTitle(text);
        v.initGrid({id:placeId,selectTime:selectTime.get('value')});
    },

    findInnerGrid:function(btn){
        return this.findView(btn).down('');
    },

    findView:function(btn){
      return btn.up('');
    },

    //获取单据id
    getPlaceOrderid:function () {
        var docid;
        Ext.Ajax.request({
            url: '/placeOrder/getPlaceOrderid',
            async:false,
            params:{
                taskid:taskId
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

