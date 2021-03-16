/**
 * Created by xd on 2017/10/21.
 */
var dbGridView ;
Ext.define('BillApproval.controller.BillApprovalController', {
    extend: 'Ext.app.Controller',

    views: ['BillApprovalView', 'BillApprovalGridView', 'BillApprovalFormView','BillApprovalAddView', 'SetQxView', 'SetEtView'],//加载view
    stores: ['BillApprovalGridStore', 'NextNodeStore', 'NextSpmanStore','DestructionBillDetailGridStore',
        'ApproveOrganStore'],//加载store
    models: ['BillApprovalGridModel','DestructionBillDetailGridModel'],//加载model
    init: function (view) {
        var isAddPostil = false;//判断是否已经添加过批注
        var billApprovalFormView;
        var billApprovalGridView;
        var destructionBillDetailGridView;
        var billId;
        var formNodeId;
        var count = 0;
        this.control({
            'billApprovalFormView': {
                render: function (view) {
                    billApprovalFormView = view;
                    view.load({
                        url: '/destructionBill/getBillApproval',
                        params: {
                            taskid: taskid
                        },
                        success: function () {
                            window.wapprove = view.getValues()['approve'];
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
                    view.down('[itemId=nextNodeId]').setHidden(appraisalSendmsg==true?false:true);
                    var nextNode = view.down('[itemId=nextNodeId]');
                    var nextSpman = view.down('[itemId=nextSpmanId]');
                    var spmanOrgan = view.down("[itemId=approveOrgan]");
                    nextNode.on('change', function (val) {
                        nextSpman.getStore().proxy.extraParams.nodeId = val.value;
                        spmanOrgan.getStore().proxy.extraParams.type = "approve"; //审批时获取审批单位
                        spmanOrgan.getStore().proxy.extraParams.taskid = taskid;
                        spmanOrgan.getStore().proxy.extraParams.nodeid = val.value;
                        spmanOrgan.getStore().proxy.extraParams.worktext = null;
                        spmanOrgan.getStore().proxy.extraParams.approveType = "bill"; //审批类型
                        spmanOrgan.getStore().reload(); //刷新审批单位
                    });
                }
            },
            'billApprovalFormView button[itemId=approveAdd]': {
                click: function () {
                    Ext.create('BillApproval.view.BillApprovalAddView').show();
                }
            },
            'billApprovalAddView': {
                render: function (field) {
                    field.down('[itemId=selectApproveId]').on('change', function (val) {
                        field.down('[itemId=approveId]').setValue(val.value);
                    });
                },
                afterrender:function (field) {
                    if(typeof window.wareatext!="undefined"){
                        field.down('[itemId=approveId]').setValue(window.wareatext);
                    }
                }
            },
            'billApprovalAddView button[itemId=approveAddSubmit]': {
                click: function (view) {
                    var areaText = view.up('billApprovalAddView').down('[itemId=approveId]').getValue();
                    if ('' == areaText) {
                        XD.msg('请输入批示');
                        return;
                    }

                    if(isAddPostil){
                        XD.msg('您已添加过批示');
                        return;
                    }

                    window.wareatext=areaText;
                    var curdate=getNowFormatDate();
                    var rname = window.parent.realname ? window.parent.realname : window.parent.parent.realname;
                    var text = '意见：'+areaText+'\n'+flowsText+'：' + rname +'\n'+curdate;
                    if(window.wapprove!=''){
                        text = window.wapprove+'\n\n'+text;
                    }
                    billApprovalFormView.getComponent('approveId').setValue(text);
                    view.findParentByType('billApprovalAddView').close();
                    isAddPostil = true;
                }
            },
            'billApprovalAddView button[itemId=approveAddClose]': {
                click: function (view) {
                    view.findParentByType('billApprovalAddView').close();
                }
            },
            'billApprovalFormView button[itemId=billApproveFormSubmit]':{
                click:function(view){
                    var textArea = billApprovalFormView.getComponent('approveId').getValue();
                    var nextNode = view.up('billApprovalFormView').down('[itemId=nextNodeId]').getValue();
                    var nextSpman = view.up('billApprovalFormView').down('[itemId=nextSpmanId]').getValue();
                    var sendMsg = view.up('billApprovalFormView').down('[itemId=sendmsgId]').getValue();
                    if(nextNode==null){
                        XD.msg('下一环节不能为空');
                        return ;
                    }

                    if(view.up('billApprovalFormView').down('[itemId=nextNodeId]').rawValue!='结束'
                        &&(view.up('billApprovalFormView').down('[itemId=nextSpmanId]').rawValue=='')){
                        XD.msg('下一环节审批人不能为空');
                        return ;
                    }

                    var grid = view.findParentByType('billApprovalView').down('billApprovalGridView');
                    var count =0;
                    var gridcount = grid.getStore().getCount();
                    for(var i =0;i<gridcount;i++){
                        if(grid.getStore().getAt(i).data.stateValue=='不通过'){
                            count++;
                        }
                    }

                    if(''==textArea||!isAddPostil){
                        var curdate=getNowFormatDate();
                        var rname = window.parent.realname ? window.parent.realname : window.parent.parent.realname;
                        if (textArea != '') {
                            if(count==gridcount){
                                textArea += '\n\n意见：不通过\n'+flowsText+'：' + rname +'\n'+curdate;
                            }else{
                                textArea += '\n\n意见：通过\n'+flowsText+'：' + rname +'\n'+curdate;
                            }
                        } else {
                            if(count==gridcount){
                                textArea += '意见：不通过\n'+flowsText+'：' + rname +'\n'+curdate;
                            }else{
                                textArea += '意见：通过\n'+flowsText+'：' + rname +'\n'+curdate;
                            }
                        }
                    }
                    Ext.Ajax.request({
                        params: {
                            textArea:textArea,
                            nextNode:nextNode,
                            nextSpman:nextSpman,
                            taskid:taskid,
                            nodeId:nodeId,
                            billids:view.up('billApprovalFormView').up('billApprovalView').down('billApprovalGridView').getStore().getAt(0).get('billid'),
                            sendMsg:sendMsg
                        },
                        url: '/destructionBill/billApprovalSubmit',
                        method: 'POST',
                        async: false,
                        success: function (resp) {
                            var respText = Ext.decode(resp.responseText);
                            XD.msg(respText.msg);
                            Ext.defer(function(){
                            if(iflag=='1'){
                                parent.window.wgridView.notResetInitGrid({state:'待处理',type:'销毁'});
                                parent.approve.close();
                            }else{
                                    parent.closeObj.close(parent.layer.getFrameIndex(window.name));
                            }
                            },1000);
                        },
                        failure : function() {
                            XD.msg('审批失败');
                        }
                    });
                }
            },
            'billApprovalFormView button[itemId=billApproveFormZz]':{
                click:function(view){
                    var textArea = billApprovalFormView.getComponent('approveId').value;
                    XD.confirm('确定退回吗？',function(){
                        Ext.Ajax.request({
                            params: {
                                textArea:textArea,
                                taskid:taskid,
                                nodeId:nodeId
                            },
                            url: '/destructionBill/returnBillApproval',
                            method: 'POST',
                            sync: true,
                            success: function (resp) {
                                XD.msg('审批完成');
                                if(iflag=='1'){
                                    parent.window.wgridView.notResetInitGrid({state:'待处理',type:'销毁'});
                                    parent.approve.close();
                                }else{
                                    setTimeout(function(){
                                        parent.closeObj.close(parent.layer.getFrameIndex(window.name));
                                    },1000);
                                }
                            },
                            failure : function() {
                                XD.msg('审批失败');
                            }
                        });
                    },this);
                }
            },
            'billApprovalFormView button[itemId=billApproveFormClose]': {
                click: function () {
                    if (iflag == '1') {
                        parent.approve.close();
                    } else {
                        parent.closeObj.close(parent.layer.getFrameIndex(window.name));
                    }
                }
            },
            'billApprovalGridView ': {
                afterrender: function (view) {
                    view.initGrid({taskid:taskid});
                    if (type == '完成') {//当类型为'完成'时,界面只显示查看按钮
                        var buttons = view.down("toolbar").query('button');
                        var tbseparator = view.down("toolbar").query('tbseparator');
                        //隐藏设置利用权限按钮
                        hideToolbarBtnTbsByItemId('setlyqx',buttons,tbseparator);

                        var formView = view.findParentByType('billApprovalView').down('billApprovalFormView');
                        var store = formView.down("toolbar").items.items;
                        for (var i = 0; i < store.length; i++) {
                            //隐藏下一环节&审批人&添加批示&完成&退回
                            if (i < store.length - 1) {
                                store[i].hide();
                            }
                        }
                    }
                }
            },
            'billApprovalGridView button[itemId=setlyqx]':{
                click:function(view){
                    billApprovalGridView= view.findParentByType('billApprovalGridView');
                    var select = billApprovalGridView.getSelectionModel();
                    if (select.getCount()<1) {
                        XD.msg('请至少选择一条数据');
                        return;
                    }
                    Ext.create('BillApproval.view.SetQxView',{type:'billgrid'}).show();
                }
            },

            //设置状态
            'setQxView button[itemId=setQxAddSubmit]':{
                click:function(btn){
                    var view = btn.up('setQxView');
                    var setQxId = btn.up('setQxView').down('[itemId="setQxId"]').getRawValue();
                    var destructionAppraiseType = btn.up('setQxView').down('[itemId="destructionAppraiseType"]').getRawValue();
                    var billids=[];
                    var entryids = [];
                    //修改查看详情中的条目
                    if(view.Type =='detail'){
                        entryids = view.entryid;
                    }
                    else {
                        //修改单据中全部条目
                        if (view.type == 'billgrid') {
                            var select = billApprovalGridView.getSelectionModel().getSelection();
                            for (var i = 0; i < select.length; i++) {
                                billids.push(select[i].get("id"));
                            }
                        }
                        //修改单据中选择的条目
                        else if (view.type = 'entrygrid') {
                            var select = dbGridView.getSelectionModel().getSelection();
                            for (var i = 0; i < select.length; i++) {
                                entryids.push(select[i].get("entryid"))
                            }
                        }
                     }
                    if (setQxId == null) {
                        XD.msg('请选择状态');
                    }
                    else if (setQxId == '变更') {
                        // 显示选择保管期限下拉框
                        Ext.create('BillApproval.view.SetEtView',{
                            billids : billids,
                            entryids:entryids,
                            type:view.type,
                            Type:view.Type,
                            destructionAppraise:destructionAppraiseType,
                        }).show();
                        btn.findParentByType('setQxView').close();
                    }
                    else {
                        Ext.Ajax.request({
                            params: {
                                state:setQxId, //状态(销毁，维持)
                                destructionAppraise:destructionAppraiseType ,//销毁鉴定依据
                                billids:billids,//审批单据id
                                entryids:entryids //审批单据中 条目id
                            },
                            url: '/destructionBill/updataBillState',
                            method: 'POST',
                            sync: true,
                            success: function (resp) {
                                XD.msg('设置成功');
                                btn.findParentByType('setQxView').close();
                                billApprovalGridView.initGrid({taskid:taskid});
                                if (typeof(dbGridView) != 'undefined') {// 刷新
                                    dbGridView.getStore().reload();
                                }
                            },
                            failure : function() {
                                XD.msg('设置失败');
                            }
                        });
                    }
                }
            },
            // 设置状态 - 关闭
            'setQxView button[itemId=setQxAddClose]':{
                click:function(view){
                    view.findParentByType('setQxView').close();
                }
            },
            // 设置保管期限 - 提交
            'setEtView button[itemId=setEtAddSubmit]':{
                click: function(btn) {
                    var view = btn.up('setEtView');
                    var com = btn.up('setEtView').down('combobox').getValue();

                    if(view.type =='entrygrid'||view.Type =='detail'){ //设置条目权限
                        Ext.Ajax.request({
                            params: {
                                state: "变更"+"("+com+")",
                                destructionAppraise:view.destructionAppraise ,//销毁鉴定依据
                                entryids:view.entryids,
                            },
                            url: '/destructionBill/updataBillState',
                            method: 'POST',
                            sync: true,
                            success: function (resp) {
                                XD.msg('设置成功');
                                btn.findParentByType('setEtView').close();
                                billApprovalGridView.initGrid({taskid:taskid});
                                if (typeof(dbGridView) != 'undefined') {
                                    dbGridView.getStore().reload();
                                }
                            },
                            failure : function() {
                                XD.msg('设置失败');
                            }
                        });
                    }
                    else {   //设置单据（的条目）权限
                        Ext.Ajax.request({
                            params: {
                                state: "变更" + "(" + com + ")",
                                destructionAppraise:view.destructionAppraise ,//销毁鉴定依据
                                billids: view.billids,
                            },
                            url: '/destructionBill/updataBillState',
                            method: 'POST',
                            sync: true,
                            success: function (resp) {
                                XD.msg('设置成功');
                                btn.findParentByType('setEtView').close();
                                billApprovalGridView.initGrid({taskid: taskid});
                                if (typeof(dbGridView) != 'undefined') {
                                    dbGridView.getStore().reload();
                                }
                            },
                            failure: function () {
                                XD.msg('设置失败');
                            }
                        });
                    }
                }
            },
            // 设置保管期限 - 关闭
            'setEtView button[itemId=setEtAddClose]':{
                click:function(view){
                    view.findParentByType('setEtView').close();
                }
            },
            'billApprovalGridView button[itemId=look]':{
                click:function (view) {
                    billApprovalGridView = view.up('billApprovalGridView');
                    var select = billApprovalGridView.getSelectionModel().getSelection();
                    if (select.length == 0) {
                        XD.msg('请至少选择一条需要查看的数据');
                        return;
                    } else if (select.length != 1) {
                        XD.msg('查看只能选中一条数据');
                        return;
                    } else {
                        billId = select[0].get("id");
                        formNodeId=select[0].get("nodeid");
                        var window = Ext.create('DestructionBill.view.DestructionBillDetailView');
                        window.show();
                    }
                }
            },
            'destructionBillInfoView': {
                afterRender: function (view) {
                    view.loadRecord(billApprovalGridView.getSelectionModel().getLastSelected());
                }
            },
            'destructionBillDetailGridView': {
                render: function (view) {
                    var toolbar = [{
                        xtype: 'button',
                        itemId:'backBill',
                        text: '返回'
                    }, '-', {
                        xtype: 'button',
                        text: '查看',
                        itemId:'lookBill'
                    }, '-', {
                        xtype: 'button',
                        text: '设置权限',
                        itemId: 'setQxBtn'
                    }
                    ];
                    view.getDockedItems('toolbar[dock="top"]')[0].add(toolbar);
                    view.getStore().proxy.extraParams.billId = billId;
                    view.getStore().loadPage(1);
                }
            },
            //查看详情时候的设置权限
            'destructionbilldynamicform button[itemId=setQxBtn]': {
                click: function (btn) {
                    entryid = btn.findParentByType('destructionbilldynamicform').entryid;
                    var setQxView =  Ext.create('BillApproval.view.SetQxView',{Type:"detail",entryid:entryid});
                    setQxView.setTitle("设置权限");
                    setQxView.show();
                }
            },



            // 查看单据 - 设置权限
            'destructionBillDetailGridView button[itemId=setQxBtn]': {
                click: function (btn) {
                    dbGridView = btn.findParentByType('destructionBillDetailGridView');
                    destructionBillDetailGridView = dbGridView;
                    var select = destructionBillDetailGridView.getSelectionModel();
                    if (select.getCount()<1) {
                        XD.msg('请至少选择一条数据');
                        return;
                    }
                    var setQxView =  Ext.create('BillApproval.view.SetQxView',{type:"entrygrid"});
                    setQxView.setTitle("设置权限");
                    setQxView.show();
                }
            },
            'destructionBillDetailGridView button[itemId=backBill]': {
                click: function (btn) {
                    this.findView(btn).close();
                }
            },
            'destructionBillDetailGridView button[itemId=lookBill]': {
                click: function (btn) {
                    destructionBillDetailGridView = btn.findParentByType('destructionBillDetailGridView');
                    var select = destructionBillDetailGridView.getSelectionModel().getSelection();
                    if (select.length == 0) {
                        XD.msg('请至少选择一条需要查看的数据');
                        return;
                    }
                    var window = Ext.create('DestructionBill.view.DestructionBillWindow');
                    window.show();
                }
            },
            'destructionbillEntryFormView':{
                Render: function (view) {
                    var select = destructionBillDetailGridView.getSelectionModel().getSelection();
                    var entryid = select[0].get("entryid");
                    var entryids = [];
                    for (var i = 0; i < select.length; i++) {
                        entryids.push(select[i].get('entryid'));
                    }
                    var form = view.down('destructionbilldynamicform');
                    form.nodeid = formNodeId;//用左侧树节点的id初始化form的nodeid参数
                    form.operate = 'look';
                    form.entryids = entryids;
                    form.entryid = entryids[0];
                    this.initFormField(form, 'hide', formNodeId);
                    this.initFormData('look', form, entryid);
                }
            },
            'destructionbillEntryFormView button[itemId=back]': {
                click: function (btn) {
                    destructionBillDetailGridView.getSelectionModel().clearSelections();
                    destructionBillDetailGridView.getStore().reload();
                    btn.up('destructionBillWindow').close();
                }
            },
            'destructionbillEntryFormView [itemId=preBtn]':{
                click:this.preHandler
            },
            'destructionbillEntryFormView [itemId=nextBtn]':{
                click:this.nextHandler
            }
        });
    },

    initFormField:function(form, operate, nodeid){
//        if(form.nodeid!=nodeid){
        form.nodeid = nodeid;//用左侧树节点的id初始化form的nodeid参数
        form.removeAll();//移除form中的所有表单控件
        var field = {
            xtype: 'hidden',
            name: 'entryid'
        };
        form.add(field);
        var formField = form.getFormField();//根据节点id查询表单字段
        if(formField.length==0){
            XD.msg('请检查模板设置信息是否正确');
            return;
        }
        form.templates = formField;
        form.initField(formField,operate);//重新动态添加表单控件
//        }
        return '加载表单控件成功';
    },
    getCurrentOpenApproveform:function(btn) {
        return btn.up('destructionbillEntryFormView');
    },


    getCurrentBillApprovalform:function(btn) {
        return btn.up('destructionbillEntryFormView');
    },

//点击上一条
    preHandler:function(btn){
        var currentBillApprovalform = this.getCurrentBillApprovalform(btn);
        var form = currentBillApprovalform.down('destructionbilldynamicform');
        this.refreshFormData(form, 'pre');
    },

//点击下一条
    nextHandler:function(btn){
        var currentBillApprovalform = this.getCurrentBillApprovalform(btn);
        var form = currentBillApprovalform.down('destructionbilldynamicform');
        this.refreshFormData(form, 'next');
    },

    refreshFormData:function(form, type){
        var entryids = form.entryids;
        var currentEntryid = form.entryid;
        var entryid;
        for(var i=0;i<entryids.length;i++){
            if(type == 'pre' && entryids[i] == currentEntryid){
                if(i==0){
                    i=entryids.length;
                }
                entryid = entryids[i-1];
                break;
            }else if(type == 'next' && entryids[i] == currentEntryid){
                if(i==entryids.length-1){
                    i=-1;
                }
                entryid = entryids[i+1];
                break;
            }
        }
        form.entryid = entryid;
        if(form.operate != 'undefined'){
            this.initFormData(form.operate, form, entryid);
            return;
        }
        this.initFormData('look', form, entryid);
    },

    initFormData:function (operate, form, entryid) {
        var formview = form.up('destructionbillEntryFormView');
        var nullvalue = new Ext.data.Model();
        var fields = form.getForm().getFields().items;
        if(operate == 'look') {
            for (var i = 0; i < form.entryids.length; i++) {
                if (form.entryids[i] == entryid) {
                    count = i + 1;
                    break;
                }
            }
            var total = form.entryids.length;
            var totaltext = form.down('[itemId=totalText]');
            totaltext.setText('当前共有  ' + total + '  条，');
            var nowtext = form.down('[itemId=nowText]');
            nowtext.setText('当前记录是第  ' + count + '  条');

            Ext.each(fields,function (item) {
                item.setReadOnly(true);
            });
        } else {
            Ext.each(fields,function (item) {
                if(!item.freadOnly){
                    item.setReadOnly(false);
                }
            });
        }
        for(var i = 0; i < fields.length; i++){
            nullvalue.set(fields[i].name, null);
        }
        form.loadRecord(nullvalue);
        var etips = formview.down('[itemId=etips]');
        etips.show();
        form.reset();
        Ext.Ajax.request({
            method:'GET',
            scope:this,
            url:'/management/entries/'+entryid,
            success:function(response){
                var entry = Ext.decode(response.responseText);
                form.loadRecord({getData:function(){return entry;}});
                //字段编号，用于特殊的自定义字段(范围型日期)
                var fieldCode = form.getRangeDateForCode();
                if(fieldCode!=null){
                    //动态解析数据库日期范围数据并加载至两个datefield中
                    form.initDaterangeContent(entry);
                }
                //初始化原文数据
                var eleview = formview.down('electronic');
                eleview.initData(entryid);
                var solidview = formview.down('solid');
                solidview.initData(entryid);
                // var longview = formview.down('long');
                // longview.initData(entryid);
//                form.formStateChange(operate);
                form.fileLabelStateChange(eleview,operate);
                form.fileLabelStateChange(solidview,operate);
                // form.fileLabelStateChange(longview,operate);
            }
        });
    },
    findView: function (btn) {
        return btn.findParentByType('destructionBillDetailView');
    }
});
//itemId为要隐藏的按钮functioncode
function hideToolbarBtnTbsByItemId(itemId,btns,tbs) {
    for (var num in btns) {
        if (itemId == btns[num].itemId) {
            btns[num].hide();
            if (num >= 1) {
                tbs[num-1].hide();
            } else {
                tbs[num].hide();
            }
        }
    }
}
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
    // + " " + date.getHours() + seperator2 + date.getMinutes();
    // + seperator2 + date.getSeconds();
    return currentdate;
}