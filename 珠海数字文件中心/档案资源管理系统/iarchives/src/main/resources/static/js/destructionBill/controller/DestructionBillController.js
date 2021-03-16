/**
 * Created by yl on 2017/10/26.
 */
Ext.define('DestructionBill.controller.DestructionBillController', {
    extend: 'Ext.app.Controller',

    views: ['DestructionBillView', 'DestructionBillTreeView','DestructionBillGridView',
        'DestructionBillInfoView', 'DestructionBillDetailView', 'DestructionBillDetailGridView',
        'DestructionApprovalView', 'DestructionBillWindows','DestructionBillInstructionsView','DealDetailsGridView'],//加载view
    stores: ['DestructionBillGridStore', 'DestructionBillDetailGridStore','DealDetailsGridStore','ApproveManStore',
        'ApproveOrganStore'],//加载store
    models: ['DestructionBillGridModel', 'DestructionBillDetailGridModel','DealDetailsGridModel'],//加载model
    init: function () {
        var billId;
        var nodeId;
        var destructionBillGridView;
        var destructionBillDetailGridView;
        var treeId;
        var count = 0;
        this.control({
            'destructionBillTreeView': {
                select: function (treemodel, record) {
                    var destructionBillGridView = treemodel.view.findParentByType('destructionBillView').down('destructionBillGridView');
                    if (record.get('leaf')) {
                        destructionBillGridView.setTitle("当前位置：" + record.get('text'));
                        treeId=record.get('id');
                        destructionBillGridView.getDockedItems('toolbar[dock="top"]')[0].removeAll();
                        var toolbar = [];
                        switch (record.get('id')) {
                            case 0://未送审
                                toolbar.push(
                                    {
                                    xtype: 'button',
                                    text: '查看单据记录',
                                    itemId: 'lookDetailBill',
                                    iconCls:'fa fa-indent'
                                }, '-', {
                                    xtype: 'button',
                                    itemId: 'deleteBillID',
                                    text: '删除单据',
                                    iconCls:'fa fa-trash-o'
                                }, '-', {
                                    xtype: 'button',
                                    itemId: 'approval',
                                    text: '送审',
                                    iconCls:'fa fa-share-square-o'
                                }, '-',
                                    {
                                        itemId:'xhDealDetailsId',
                                        xtype: 'button',
                                        iconCls:'fa fa-newspaper-o',
                                        text: '办理详情'
                                    },
                                    '-',
                                    {
                                    xtype: 'button',
                                    itemId: 'print',
                                    text: '打印',
                                    iconCls:'fa fa-print'
                                });
                                break;
                            case 1://待审核
                                toolbar.push(
                                    {
                                        itemId:'xhDealDetailsId',
                                        xtype: 'button',
                                        iconCls:'fa fa-newspaper-o',
                                        text: '办理详情'
                                    },
                                    '-',
                                    {
                                    xtype: 'button',
                                    text: '查看单据记录',
                                    itemId: 'lookDetailBill',
                                    iconCls:'fa fa-indent'
                                }, '-',
                                    {
                                    xtype: 'button',
                                    itemId: 'viewApproval',
                                    text: '查看批示',
                                    iconCls:'fa fa-comment-o'
                                }, '-',
                                    {
                                        itemId:'xhDealDetailsId',
                                        xtype: 'button',
                                        iconCls:'fa fa-newspaper-o',
                                        text: '办理详情'
                                    },
                                    '-',
                                    {
                                    xtype: 'button',
                                    itemId: 'print',
                                    text: '打印',
                                    iconCls:'fa fa-print'
                                },'-',
                                {
                                    xtype: 'button',
                                    itemId: 'urging',
                                    text: '催办',
                                    iconCls:'fa fa-print',
                                    hidden :true
                                },{
                                    xtype: "checkboxfield",
                                    boxLabel : '发送短信',
                                    itemId:'message',
                                    checked:true,
                                    hidden :true
                            });
                                break;
                            case 2://已审核
                                toolbar.push(
                                    {
                                        itemId:'xhDealDetailsId',
                                        xtype: 'button',
                                        iconCls:'fa fa-newspaper-o',
                                        text: '办理详情'
                                    },
                                    '-',
                                    {
                                    xtype: 'button',
                                    text: '查看单据记录',
                                    itemId: 'lookDetailBill',
                                    iconCls:'fa fa-indent'
                                }, '-', {
                                    xtype: 'button',
                                    itemId: 'implement',
                                    text: '执行销毁',
                                    iconCls:'fa fa-check-square'
                                }, '-', {
                                    xtype: 'button',
                                    itemId: 'viewApproval',
                                    text: '查看批示',
                                    iconCls:'fa fa-comment-o'
                                }, '-',
                                    {
                                        itemId:'xhDealDetailsId',
                                        xtype: 'button',
                                        iconCls:'fa fa-newspaper-o',
                                        text: '办理详情'
                                    },
                                    '-',
                                    {
                                    xtype: 'button',
                                    itemId: 'print',
                                    text: '打印',
                                    iconCls:'fa fa-print'
                                });
                                break;
                            case 3://已审核（不通过）
                            case 4://已执行
                            case 5://已退回
                                toolbar.push(
                                    {
                                        itemId:'xhDealDetailsId',
                                        xtype: 'button',
                                        iconCls:'fa fa-newspaper-o',
                                        text: '办理详情'
                                    },
                                    '-',
                                    {
                                    xtype: 'button',
                                    text: '查看单据记录',
                                    itemId: 'lookDetailBill',
                                    iconCls:'fa fa-indent'
                                }, '-', {
                                    xtype: 'button',
                                    itemId: 'viewApproval',
                                    text: '查看批示',
                                    iconCls:'fa fa-comment-o'
                                }, '-',
                                    {
                                        itemId:'xhDealDetailsId',
                                        xtype: 'button',
                                        iconCls:'fa fa-newspaper-o',
                                        text: '办理详情'
                                    },
                                    '-',
                                    {
                                    xtype: 'button',
                                    itemId: 'print',
                                    text: '打印',
                                    iconCls:'fa fa-print'
                                });
                                break;
                            default:
                        }

                        if (record.get('id') == '1' ||record.get('id') == '6') {
                            Ext.Ajax.request({//根据审批id判断是否可以催办
                                url: '/destructionBill/findByWorkId',
                                method: 'GET',
                                success: function (resp) {
                                    var respDate = Ext.decode(resp.responseText).data;
                                    if (respDate.urgingstate == "1") {
                                        destructionBillGridView.down('[itemId=urging]').show();
                                        destructionBillGridView.down('[itemId=message]').show();
                                    }
                                }
                            });
                        	destructionBillGridView.columns[1].show();
                        	destructionBillGridView.columns[2].show();
                        } else {
                            destructionBillGridView.columns[1].hide();
                        	destructionBillGridView.columns[2].hide();
                        }
                        if(record.get('id') == '2'||record.get('id') == '3'||record.get('id') == '4'||record.get('id') == '5'){
                            destructionBillGridView.columns[8].show();
                        }else {
                            destructionBillGridView.columns[8].hide();
                        }
                        destructionBillGridView.getDockedItems('toolbar[dock="top"]')[0].add(toolbar);
                        destructionBillGridView.initGrid({ state: record.get('id') });
                    }
                }
            },
            'destructionBillGridView button[itemId=xhDealDetailsId]': {//办理详情
                click: function (view) {
                    var destructionBillGridView = view.findParentByType('destructionBillGridView');
                    var select = destructionBillGridView.getSelectionModel();
                    if (!select.hasSelection()) {
                        XD.msg('请选择一条数据!');
                        return;
                    }
                    var details = select.getSelection();
                    if(details.length!=1){
                        XD.msg('只支持单条数据查看!');
                        return;
                    }
                    var billId = details[0].get("billid");
                    this.showDealDetailsWin(billId);
                }
            },
            'destructionBillGridView button[itemId=urging]': {//催办
                click: function (view) {
                    var destructionBillGridView = view.findParentByType('destructionBillGridView');
                    var select = destructionBillGridView.getSelectionModel();
                    if (!select.hasSelection()) {
                        XD.msg('请选择一条数据!');
                        return;
                    }
                    var details = select.getSelection();
                    if(details.length!=1){
                        XD.msg('只支持单条数据催办!');
                        return;
                    }
                    Ext.MessageBox.wait('正在处理请稍后...');
                    Ext.Ajax.request({
                        params: {billids: details[0].get("billid"),sendMsg:destructionBillGridView.down("[itemId=message]").checked},
                        url: '/destructionBill/manualUrging',
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
                }
            },
            'destructionBillGridView button[itemId=deleteBillID]': {//删除单据
                click: function (view) {
                    var destructionBillGridView = view.findParentByType('destructionBillGridView');
                    var select = destructionBillGridView.getSelectionModel();
                    if (!select.hasSelection()) {
                        XD.msg('请选择操作记录!');
                    } else {
                        var selectCount = select.getCount();
                        var details = select.getSelection();
                        XD.confirm('确定删除吗？',function(){
                                var array = [];
                                for (i = 0; i < details.length; i++) {
                                    array[i] = details[i].get("billid");
                                }
                                Ext.Ajax.request({
                                    params: {billids: array},
                                    url: '/destructionBill/deleteBill',
                                    method: 'POST',
                                    sync: true,
                                    success: function (response) {
                                        var respText = Ext.decode(response.responseText);
                                        if (respText.success == true) {
                                            XD.msg(respText.msg);
                                            destructionBillGridView.delReload(selectCount);
                                        } else {
                                            XD.msg(respText.msg);
                                        }
                                    },
                                    failure: function () {
                                        XD.msg('操作失败');
                                    }
                                });
                        },this);
                    }
                }
            },
            'destructionBillGridView button[itemId=lookDetailBill]': {//查看单据记录
                click: function (btn) {
                    destructionBillGridView = btn.findParentByType('destructionBillGridView');
                    var select = destructionBillGridView.getSelectionModel().getSelection();
                    if (select.length == 0) {
                        XD.msg('请至少选择一条需要查看的数据');
                        return;
                    } else if (select.length != 1) {
                        XD.msg('查看只能选中一条数据');
                        return;
                    } else {
                        billId = select[0].get("billid");
                        nodeId = select[0].get("nodeid");
                        var window = Ext.create('DestructionBill.view.DestructionBillDetailView');
                        window.show();
                    }
                }
            },
            'destructionBillGridView button[itemId=approval]': {//送审按钮
                click: function (btn) {
                    destructionBillGridView = btn.findParentByType('destructionBillGridView');
                    var select = destructionBillGridView.getSelectionModel().getSelection();
                    if (select.length == 0) {
                        XD.msg('请至少选择一条需要送审的数据');
                        return;
                    }
                    var window = Ext.create('DestructionBill.view.DestructionApprovalView');
                    window.show();
                }
            },
            'destructionBillGridView button[itemId=viewApproval]': {//查看批示
                click: function (btn) {
                    destructionBillGridView = btn.findParentByType('destructionBillGridView');
                    var select = destructionBillGridView.getSelectionModel().getSelection();
                    if (select.length == 0) {
                        XD.msg('请至少选择一条需要查看的数据');
                        return;
                    } else if (select.length != 1) {
                        XD.msg('查看只能选中一条数据');
                        return;
                    } else {
                        var window = Ext.create('DestructionBill.view.DestructionBillInstructionsView');
                        window.show();
                    }
                }
            },
            'destructionBillGridView button[itemId=implement]': {//执行销毁
                click: function (btn) {
                    destructionBillGridView = btn.findParentByType('destructionBillGridView');
                    var select = destructionBillGridView.getSelectionModel().getSelection();
                    if (select.length == 0) {
                        XD.msg('请至少选择一条需要执行的数据');
                        return;
                    } else {
                        XD.confirm('确定要执行这' + select.length + '条数据吗',function(){
                            var billIds = [];
                            for (var i = 0; i < select.length; i++) {
                                billIds.push(select[i].get('billid'));
                            }
                            Ext.Ajax.request({
                                params: {
                                    billids:billIds
                                },
                                url: '/destructionBill/implementBill/',
                                method: 'POST',
                                sync: true,
                                success: function (resp) {
                                    var resp = Ext.decode(resp.responseText);
                                    if('无法执行'==resp.msg){
                                        var titles = resp.data;
                                        var title;
                                        for(var i=0;i<titles.length;i++){
                                            if(i==0){
                                                title = '['+titles[i]+']';
                                            }else{
                                                title = title + '，' + '['+titles[i]+']';
                                            }
                                        }
                                        //XD.msg('无法执行，这  '+titles.length+'  条题名为  '+title+'  还处于未归状态')
                                        XD.msg('无法执行销毁，请到查档的归还管理中归还后再进行销毁。借出未归还的条目:'+title);
                                    }else {
                                        XD.msg(resp.msg);
                                        destructionBillGridView.notResetInitGrid({state: '2'});
                                    }
                                },
                                failure : function() {
                                    XD.msg('操作失败');
                                }
                            });
                        },this);
                    }
                }
            },
            'destructionBillGridView button[itemId=print]': {//打印报表
                click:this.printHandler
            },
            'destructionBillDetailGridView button[itemId=backBill]': {//查看单据记录界面返回
                click: function (btn) {
                    this.findDesBillDetailView(btn).hide();
                }
            },
            'destructionBillDetailGridView button[itemId=lookBill]': {//查看条目及原文
                click: function (btn) {
                    destructionBillDetailGridView = btn.findParentByType('destructionBillDetailGridView');
                    var select = destructionBillDetailGridView.getSelectionModel().getSelection();
                    if (select.length == 0) {
                        XD.msg('请至少选择一条需要查看的数据');
                        return;
                    }
                    var window = Ext.create('DestructionBill.view.DestructionBillWindows');
                    window.show();
                }
            },
            'destructionBillInfoView': {//查看单据north位置的form
                afterRender: function (view) {
                    view.loadRecord(destructionBillGridView.getSelectionModel().getLastSelected());
                }
            },
            'destructionBillDetailGridView': {//查看单据south位置的grid
                afterrender: function (view) {
                    var toolbar = [];
                    if(treeId==4){
                        toolbar.push({
                            xtype: 'button',
                            itemId:'backBill',
                            text: '返回'
                        });
                    }else{
                        toolbar.push({
                            xtype: 'button',
                            itemId:'backBill',
                            text: '返回'
                        }, '-', {
                            xtype: 'button',
                            text: '查看',
                            itemId:'lookBill'
                        });
                    }
                    view.initGrid({billId:billId});
                    view.getDockedItems('toolbar[dock=top]')[0].add(toolbar);
                }
            },
            'destructionApprovalView': {//送审窗口
                Render: function (view) {
                    Ext.Ajax.request({
                        url: '/destructionBill/getNextNode',
                        method: 'POST',
                        sync: true,
                        success: function (response) {
                            var respText = Ext.decode(response.responseText);
                            view.down('form').getForm().findField('nextNode').setValue(respText.data.desci);
                            var spmanOrgan = view.down("[itemId=approveOrgan]");
                            var spman = view.down("[itemId=spmanId]");
                            spman.select(null);
                            spman.getStore().removeAll();
                            spman.getStore().proxy.extraParams.nodeId = respText.data.id;
                            spmanOrgan.getStore().proxy.extraParams.type = "submit"; //审批时获取审批单位
                            spmanOrgan.getStore().proxy.extraParams.taskid = null;
                            spmanOrgan.getStore().proxy.extraParams.nodeid = respText.data.id;
                            spmanOrgan.getStore().proxy.extraParams.worktext = null;
                            spmanOrgan.getStore().proxy.extraParams.approveType = "bill"; //审批类型
                            spmanOrgan.getStore().reload(); //刷新审批单位

                        }
                    });
                }
            },
            'destructionApprovalView button[itemId=submit]': {//确认送审
                click: function (btn) {
                    var form = btn.up('form');
                    if(form.getComponent('spmanId').getRawValue()==''){
                        XD.msg('审批人不能为空');
                        return;
                    }
                    var select = destructionBillGridView.getSelectionModel().getSelection();
                    var tmp = [];
                    var name = [];
                    for (var i = 0; i < select.length; i++) {
                        tmp.push(select[i].get("billid"));
                        name.push(select[i].get("submitter"));
                    }
                    for (var i = 0; name.length - 1 > i; i++) {
                        for (var j = i + 1; j < name.length; j++) {
                            if (name[j] == name[i]) {
                                name.splice(j, 1); //删除之后，数组长度随之减少
                                j--;
                            }
                        }
                    }
                    var billIds = tmp.join(',');
                    var submitusernames = name.join(',');
                    Ext.MessageBox.wait('正在送审请稍后...','提示');
                    Ext.Ajax.request({
                        params: {
                            billIds: billIds,
                            userid: form.getComponent('spmanId').getValue(),
                            username: form.getComponent('spmanId').getRawValue(),
                            submitusernames: submitusernames,
                            text: form.getComponent('nextnode').getValue()
                        },
                        url: '/destructionBill/saveBillApproval',
                        method: 'POST',
                        sync: true,
                        success: function (response) {
                            Ext.MessageBox.hide();
                            var respText = Ext.decode(response.responseText);
                            if (respText.success == true) {
                                Ext.MessageBox.alert("提示", respText.msg, callBack);
                                function callBack() {
                                    btn.up('destructionApprovalView').close();
                                    destructionBillGridView.notResetInitGrid({state:'0'});
                                }
                            } else {
                                XD.msg(respText.msg);
                            }
                        },
                        failure: function (response) {
                            Ext.MessageBox.hide();
                            var respText = Ext.decode(response.responseText);
                            XD.msg(respText.msg);
                        }
                    });
                }
            },
            'destructionApprovalView button[itemId=back]': {//送审窗口返回
                click: function (btn) {
                    btn.up('destructionApprovalView').close();
                }
            },
            'EntryFormView': {//查看原文及条目公共form
                Render: function (view) {
                    var select = destructionBillDetailGridView.getSelectionModel().getSelection();
                    var entryid = select[0].get('entryid');
                    var entryids = [];
                    for(var i=0;i<select.length;i++){
                        entryids.push(select[i].get('entryid'));
                    }
                    Ext.Ajax.request({
                        method: 'GET',
                        scope: this,
                        url: '/management/entries/' + entryid,
                        success: function (response) {
                            var form = view.down('dynamicform');
                            form.nodeid = nodeId;//用左侧树节点的id初始化form的nodeid参数
                            form.operate = 'look';
                            form.entryids = entryids;
                            form.entryid = entryids[0];
                            this.initFormData('look',form, entryid);
                        }
                    });
                }
            },
            'EntryFormView button[itemId=back]': {//查看原文及条目form　返回
                click: function (btn) {
                    destructionBillDetailGridView.getStore().reload();
                    btn.up('destructionBillWindows').close();
                }
            },
            'EntryFormView [itemId=preBtn]':{
                click:this.preHandler
            },
            'EntryFormView [itemId=nextBtn]':{
                click:this.nextHandler
            },
            'destructionBillInstructionsView':{
                render: function (view) {
                   var form =view.down('form');
                    form.load({
                        url: '/destructionBill/findByBillid',
                        params: {
                            billid: destructionBillGridView.getSelectionModel().getSelection()[0].get("billid")
                        }
                    });
                }
            }
        });
    },

    showDealDetailsWin:function(id){
        var dealDetailsWin = Ext.create('Ext.window.Window',{
            modal:true,
            width:1000,
            height:530,
            title:'办理详情',
            layout:'fit',
            closeToolText:'关闭',
            closeAction:'hide',
            items:[{
                xtype: 'DealDetailsGridView'
            }]
        });
        var store = dealDetailsWin.down('DealDetailsGridView').getStore();
        store.proxy.extraParams.billid = id;
        store.reload();
        dealDetailsWin.show();
    },

    getCurrentDestructionBillform:function (btn) {
        return btn.up('EntryFormView');
    },

    //点击上一条
    preHandler:function(btn){
        var currentDestructionBillform = this.getCurrentDestructionBillform(btn);
        var form = currentDestructionBillform.down('dynamicform');
        this.refreshFormData(form, 'pre');
    },

    //点击下一条
    nextHandler:function(btn){
        var currentDestructionBillform = this.getCurrentDestructionBillform(btn);
        var form = currentDestructionBillform.down('dynamicform');
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
        var formview = form.up('EntryFormView');
        var nullvalue = new Ext.data.Model();
        var fields = form.getForm().getFields().items;
        if(operate == 'look') {
            for (var i = 0; i < fields.length; i++) {
                fields[i].setReadOnly(true);
            }
        }else{
            for (var i = 0; i < fields.length; i++) {
                if(!fields[i].freadOnly){
                    fields[i].setReadOnly(false);
                }
            }
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
                var formField = form.getFormField();//根据节点id查询表单字段
                if(formField.length==0){
                    XD.msg('请检查模板设置信息是否正确');
                    return;
                }
                form.removeAll();//移除form表单所有表单控件
                form.initField(formField);//重新动态添加表单控件
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
		        }
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

    findView:function (btn) {
      return btn.findParentByType('destructionBillView');
    },
    findDesBillDetailView: function (btn) {//获取查看单据窗口
        return btn.findParentByType('destructionBillDetailView');
    },
    findDesBillGridView:function (btn) {
        return this.findView(btn).down('destructionBillGridView');
    },

    printHandler:function (btn) {
        var grid = this.findDesBillGridView(btn);
        var ids = [];
        var params = {};
        Ext.each(grid.getSelectionModel().getSelection(),function(){
            ids.push(this.get('billid').trim());
        });
        if(reportServer == 'UReport') {
            params['billid'] = ids.join(',');
            XD.UReportPrint(null, '销毁单据管理', params);
        }
        else if(reportServer == 'FReport') {
            XD.FRprint(null, '销毁单据管理', ids.length > 0 ? "'billid':'" + ids.join(",") + "'" : '');
        }
    }
});