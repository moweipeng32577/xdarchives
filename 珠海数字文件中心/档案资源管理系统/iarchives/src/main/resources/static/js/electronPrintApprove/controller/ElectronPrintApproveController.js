/**
 * Created by Administrator on 2019/5/23.
 */

Ext.define("ElectronPrintApprove.controller.ElectronPrintApproveController",{
    extend:"Ext.app.Controller",
    views:[
        'ElectronPrintApproveView','ElectronPrintApproveGridView','ElectronPrintApproveFormView',
        'ElectronicView','ApproveAddView','ApprovePrintSetEleView'
    ],
    stores:[
        'NextSpmanStore','NextNodeStore','ElectronPrintApproveGridStore','ApplyPrintEleGridStore',
        'ApproveOrganStore'
    ],
    models:[
        'ElectronPrintApproveGridModel','ApplyPrintEleGridModel'
    ],
    init:function (view) {
        var isAddPostil = false;//判断是否已经添加过批注
        this.control({
            'electronPrintApproveFormView':{
                render:function(view){
                    window.wform = view;
                    view.load({
                        url: '/electronApprove/getBorrowDocByTaskid',
                        params : {
                            taskid:taskid
                        },
                        success : function(form,action) {
                            window.borrowcode = Ext.decode(action.response.responseText).data.borrowcode;
                            window.wapprove = window.wform.getValues()['approve'];
                        },
                        failure: function() {
                            XD.msg('操作失败');
                        }
                    });
                    var nextNode = view.down('[itemId=nextNodeId]');
                    var nextSpman = view.down('[itemId=nextSpmanId]');
                    nextNode.on('change',function(val){
                        nextSpman.getStore().proxy.extraParams.nodeId = val.value;
                        nextSpman.getStore().load(function(){
                            if(this.getCount() > 0){
                                nextSpman.select(this.getAt(0));
                            }else{
                                nextSpman.setValue(null);
                            }
                        });
                    });
                }
            },
            "electronPrintApproveGridView":{
                afterrender:function (view) {
                    view.initGrid({taskid:taskid});
                }
            },

            'electronPrintApproveFormView button[itemId=electronId]': {
                click: function (view) {
                    window.leadIn = Ext.create("Ext.window.Window", {
                        width: '100%',
                        height: '100%',
                        title: '查看文件',
                        modal: true,
                        header: false,
                        draggable: false,//禁止拖动
                        resizable: false,//禁止缩放
                        closeToolText: '关闭',
                        layout: 'fit',
                        items: [{xtype: 'electronicPro'}]
                    });
                    window.wform = view.findParentByType('electronPrintApproveFormView');
                    window.leadIn.down('electronicPro').initData(window.borrowcode);
                    window.leadIn.show();
                }
            },

            'electronPrintApproveFormView button[itemId=approveAdd]':{
                click:function(){
                    Ext.create('ElectronPrintApprove.view.ApproveAddView').show();
                }
            },

            'approveAddView button[itemId=approveAddSubmit]': {
                click: function (view) {
                    var areaText = view.findParentByType('approveAddView').down('[itemId=approveId]').value;
                    if ('' == areaText) {
                        XD.msg("请输入批示");
                        return;
                    }

                    if(isAddPostil){
                        XD.msg('您已添加过批示');
                        return;
                    }

                    window.wareatext = areaText;
                    var curdate = getNowFormatDate();
                    var rname = window.parent.realname ? window.parent.realname : window.parent.parent.realname;
                    var text = '意见：'+areaText+'\n'+flowsText+'：' + rname +'\n'+curdate;
                    if (typeof(window.wapprove) != 'undefined' && window.wapprove != '') {
                        text = window.wapprove + '\n\n' + text;
                    }
                    window.wform.getComponent('approveId').setValue(text);
                    view.findParentByType('approveAddView').close();
                    isAddPostil = true;
                }
            },

            'approveAddView button[itemId=approveAddClose]': {
                click: function (view) {
                    view.findParentByType("approveAddView").close();
                }
            },

            'approveAddView': {
                render: function (field) {
                    field.down('[itemId=selectApproveId]').on('change', function (val) {
                        field.down('[itemId=approveId]').setValue(val.value);
                    });
                },
                afterrender: function (field) {
                    if (typeof window.wareatext != "undefined") {
                        field.down('[itemId=approveId]').setValue(window.wareatext);
                    }
                }
            },
            'electronPrintApproveFormView button[itemId=fileNotfound]':{ //查无此档
                click:function (view) {
                    XD.confirm('是否查无此档',function () {
                        var textArea=window.wform.getComponent('approveId').value;
                        var currenttime=getNowFormatDate();
                        var realname=window.parent.realname ? window.parent.realname : window.parent.parent.realname;
                        if(textArea==''){
                            if (textArea == '') {
                                textArea += '意见：查无此档\n'+flowsText+'：' + realname +'\n'+currenttime;
                            } else if (textArea.indexOf('查无此档') < 0) {
                                textArea += '\n\n意见：查无此档\n'+flowsText+'：' + realname +'\n'+currenttime;
                            }
                        }
                        Ext.Ajax.request({
                            method:'GET',
                            url:'/electronPrintApprove/filenotfound',
                            params:{
                                taskid:taskid,
                                nodeId:nodeId,
                                textarea:textArea
                            },
                            sync: true,
                            success:function () {
                                XD.msg('审批完成');
                                Ext.defer(function () {
                                    if(iflag=='1'){
                                        parent.wgridView.notResetInitGrid({state:'待处理',type:'电子打印'});
                                        parent.approve.close();
                                    }else{
                                        parent.closeObj.close(parent.layer.getFrameIndex(window.name));
                                    }
                                },1000);
                            }

                        })
                    })
                }
            },
            'electronPrintApproveFormView button[itemId=printApproveFormSubmit]':{
                click:function(view){
                    var textArea = window.wform.getComponent('approveId').getValue();
                    var nextNode = view.up('electronPrintApproveFormView').down('[itemId=nextNodeId]').getValue();
                    var nextSpman = view.up('electronPrintApproveFormView').down('[itemId=nextSpmanId]').getValue();
                    var sendMsg = view.up('electronPrintApproveFormView').down("[itemId=sendmsgId]").getValue();
                    if(nextNode==null){
                        XD.msg('下一环节不能为空');
                        return ;
                    }

                    if(view.up('electronPrintApproveFormView').down('[itemId=nextNodeId]').rawValue!='结束'
                        &&(view.up('electronPrintApproveFormView').down('[itemId=nextSpmanId]').rawValue=='')){
                        XD.msg('下一环节审批人不能为空');
                        return ;
                    }

                    var borrowtyts = window.wform.getComponent('borrowtytsId').getValue();
                    if(borrowtyts==''||borrowtyts==null||isNaN(borrowtyts)||parseInt(borrowtyts)<=0||borrowtyts.indexOf('.')>-1){
                        XD.msg('非法同意天数');
                        return ;
                    }
                    var grid = view.findParentByType('electronPrintApproveView').down('electronPrintApproveGridView');
                    var count =0;
                    var gridcount = grid.getStore().getCount();
                    for(var i =0;i<gridcount;i++){
                        if(grid.getStore().getAt(i).data.lyqx=='拒绝'){
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
                            borrowtyts:borrowtyts,
                            sendMsg:sendMsg
                        },
                        url: '/electronPrintApprove/approvePrintSubmit',
                        method: 'POST',
                        async: false,
                        success: function (resp) {
                            var respText = Ext.decode(resp.responseText);
                            XD.msg(respText.msg);
                            Ext.defer(function(){
                                if(iflag=='1'){
                                    parent.wgridView.notResetInitGrid({state:'待处理',type:'电子打印'});
                                    parent.approve.close();
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

            'electronPrintApproveFormView button[itemId=printApproveFormClose]':{
                click:function(view){
                    if(iflag=='1') {
                        parent.approve.close();
                    }else{
                        parent.closeObj.close(parent.layer.getFrameIndex(window.name));
                    }
                }
            },

            'electronPrintApproveFormView button[itemId=printApproveFormZz]':{
                click:function(view){
                    XD.confirm('是否确定退回',function () {
                        var textArea = window.wform.getComponent('approveId').value;
                        var curdate = getNowFormatDate();
                        var rname = window.parent.realname ? window.parent.realname : window.parent.parent.realname;
                        if (textArea == '') {
                            textArea += '意见：不通过\n'+flowsText+'：' + rname +'\n'+curdate;
                        } else if (textArea.indexOf('驳回') < 0) {
                            textArea += '\n\n意见：不通过\n'+flowsText+'：' + rname +'\n'+curdate;
                        }
                        Ext.Ajax.request({
                            params: {
                                taskid:taskid,
                                nodeId:nodeId,
                                textarea:textArea
                            },
                            url: '/electronPrintApprove/returnPrintApply',
                            method: 'POST',
                            sync: true,
                            success: function (resp) {
                                XD.msg('审批完成');
                                Ext.defer(function () {
                                    if(iflag=='1'){
                                        parent.wgridView.notResetInitGrid({state:'待处理',type:'电子打印'});
                                        parent.approve.close();
                                    }else{
                                        parent.closeObj.close(parent.layer.getFrameIndex(window.name));
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

            'electronPrintApproveGridView button[itemId=agree]':{
                click:function(view){
                    var electronPrintApproveGridView = view.findParentByType('electronPrintApproveGridView');
                    var select = electronPrintApproveGridView.getSelectionModel().getSelection();
                    if(select.length<1){
                        XD.msg('请至少选择一条数据');
                        return;
                    }
                    var entryids = [];
                    for(var i=0;i<select.length;i++){
                        entryids.push(select[i].get('entryid'));
                    }
                        Ext.Ajax.request({
                            params: {
                                taskid:taskid,
                                entryids:entryids,
                                type:"同意",
                                setType:"setEntry"
                            },
                            url: '/electronPrintApprove/setPrintApproveState',
                            method: 'POST',
                            sync: true,
                            success: function (resp) {
                                electronPrintApproveGridView.getStore().reload();
                                XD.msg('设置完成');
                            },
                            failure : function() {
                                XD.msg('操作失败');
                            }
                        });
                }
            },

            'electronPrintApproveGridView button[itemId=refuse]':{
                click:function(view){
                    var electronPrintApproveGridView = view.findParentByType('electronPrintApproveGridView');
                    var select = electronPrintApproveGridView.getSelectionModel().getSelection();
                    if(select.length<1){
                        XD.msg('请至少选择一条数据');
                        return;
                    }
                    var entryids = [];
                    for(var i=0;i<select.length;i++){
                        entryids.push(select[i].get('entryid'));
                    }
                    Ext.Ajax.request({
                        params: {
                            taskid:taskid,
                            entryids:entryids,
                            type:"拒绝",
                            setType:"setEntry"
                        },
                        url: '/electronPrintApprove/setPrintApproveState',
                        method: 'POST',
                        sync: true,
                        success: function (resp) {
                            electronPrintApproveGridView.getStore().reload();
                            XD.msg('设置完成');
                        },
                        failure : function() {
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            'electronPrintApproveGridView button[itemId=look]':{
                click:function(view){
                    var approve =  Ext.create("Ext.window.Window",{
                        width:'100%',
                        height:'100%',
                        plain: true,
                        header: false,
                        border: false,
                        closable: false,
                        frame:false,
                        draggable : false,//禁止拖动
                        resizable : false,//禁止缩放
                        modal:true,
                        closeToolText:'关闭',
                        layout:'fit',
                        items:[{
                            xtype: 'EntryFormView'
                        }]
                    });


                    var grid = view.findParentByType('electronPrintApproveGridView');
                    var record = grid.selModel.getSelection();

                    if(record.length == 0){
                        XD.msg('请至少选择一条需要查看的数据');
                        return;
                    }

                    var entryid = record[0].get("entryid");
                    var entryids = [];
                    var nodeids = [];
                    for(var i=0;i<record.length;i++){
                        entryids.push(record[i].get('entryid'));
                        nodeids.push(record[i].get('nodeid'));
                    }
                    var form = approve.down('dynamicform');
                    form.nodeid = record[0].get("nodeid");
                    form.operate = 'look';
                    form.entryids = entryids;
                    form.nodeids = nodeids;
                    form.entryid = entryids[0];
                    this.initFormField(form, 'hide', record[0].get('nodeid'));
                    this.initFormData('look',form, entryid);
                    approve.show();
                    window.approves = approve;
                }
            },

            'EntryFormView button[itemId=back]':{
                click:function(btn){
                   btn.findParentByType("window").close();
                }
            },
            'EntryFormView [itemId=preBtn]':{
                click:this.preHandler
            },
            'EntryFormView [itemId=nextBtn]':{
                click:this.nextHandler
            },

            'electronPrintApproveGridView button[itemId=setlyqx]':{
                click:function(view){
                    var electronPrintApproveGridView = view.findParentByType("electronPrintApproveGridView");
                    var select = electronPrintApproveGridView.getSelectionModel().getSelection();
                    if(select.length!=1){
                        XD.msg("只能选择一条数据");
                        return;
                    }
                    var entryid = select[0].get('entryid');
                    var approveView =  Ext.create("Ext.window.Window",{
                        width:'100%',
                        height:'100%',
                        plain: true,
                        header: false,
                        border: false,
                        closable: false,
                        frame:false,
                        modal:true,
                        closeToolText:'关闭',
                        layout:'fit',
                        items:[{
                            xtype: 'approvePrintSetEleView'
                        }]
                    });
                    var eleGrid = approveView.down("[itemId=eleGrid]");
                    eleGrid.getStore().proxy.extraParams.entryid = entryid;
                    eleGrid.getStore().proxy.extraParams.borrowcode=window.borrowcode;
                    eleGrid.getStore().proxy.extraParams.type="all";
                    eleGrid.getStore().reload();
                    approveView.show();
                }
            },

            'approvePrintSetEleView [itemId=agreeEle]':{
                click:function (btn) {
                    var approvePrintSetEleView = btn.findParentByType('approvePrintSetEleView');
                    var eleGrid = approvePrintSetEleView.down("[itemId=eleGrid]");
                    var select = eleGrid.getSelectionModel().getSelection();
                    if(select.length<1){
                        XD.msg('至少选择一条数据');
                        return;
                    }
                    var ids = [];
                    for(var i=0;i<select.length;i++){
                        if(select[i].get('state')==null||select[i].get('state')==""){
                            XD.msg('请勿选择未申请打印的电子文件');
                            return;
                        }
                        ids.push(select[i].get('id'));
                    }
                    Ext.Ajax.request({
                        params: {
                            taskid:taskid,
                            entryids:ids,
                            type:"同意",
                            setType:"setEle"
                        },
                        url: '/electronPrintApprove/setPrintApproveState',
                        method: 'POST',
                        sync: true,
                        success: function (resp) {
                            eleGrid.getStore().reload();
                            XD.msg('设置完成');
                        },
                        failure : function() {
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            'approvePrintSetEleView [itemId=refuseEle]':{
                click:function (btn) {
                    var approvePrintSetEleView = btn.findParentByType('approvePrintSetEleView');
                    var eleGrid = approvePrintSetEleView.down("[itemId=eleGrid]");
                    var select = eleGrid.getSelectionModel().getSelection();
                    if(select.length<1){
                        XD.msg('至少选择一条数据');
                        return;
                    }
                    var ids = [];
                    for(var i=0;i<select.length;i++){
                        if(select[i].get('state')==null||select[i].get('state')==""){
                            XD.msg('请勿选择未申请打印的电子文件');
                            return;
                        }
                        ids.push(select[i].get('id'));
                    }
                    Ext.Ajax.request({
                        params: {
                            taskid:taskid,
                            entryids:ids,
                            type:"拒绝",
                            setType:"setEle"
                        },
                        url: '/electronPrintApprove/setPrintApproveState',
                        method: 'POST',
                        sync: true,
                        success: function (resp) {
                            eleGrid.getStore().reload();
                            XD.msg('设置完成');
                        },
                        failure : function() {
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            'approvePrintSetEleView [itemId=backEle]':{
                click:function (btn) {
                    btn.findParentByType("window").close();
                }
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

    getCurrentElectronApproveform:function(btn) {
        return btn.up('EntryFormView');
    },

    //点击上一条
    preHandler:function(btn){
        var currentElectronApproveform = this.getCurrentElectronApproveform(btn);
        var form = currentElectronApproveform.down('dynamicform');
        this.refreshFormData(form, 'pre');
    },

//点击下一条
    nextHandler:function(btn){
        var currentElectronApproveform = this.getCurrentElectronApproveform(btn);
        var form = currentElectronApproveform.down('dynamicform');
        this.refreshFormData(form, 'next');
    },

    refreshFormData:function(form, type){
        var entryids = form.entryids;
        var nodeids = form.nodeids;
        var currentEntryid = form.entryid;
        var entryid;
        var nodeid;
        for(var i=0;i<entryids.length;i++){
            if(type == 'pre' && entryids[i] == currentEntryid){
                if(i==0){
                    i=entryids.length;
                }
                entryid = entryids[i-1];
                nodeid = nodeids[i-1];
                break;
            }else if(type == 'next' && entryids[i] == currentEntryid){
                if(i==entryids.length-1){
                    i=-1;
                }
                entryid = entryids[i+1];
                nodeid = nodeids[i+1];
                break;
            }
        }
        form.entryid = entryid;
        if(form.operate != 'undefined'){
            this.initFormField(form, 'hide', nodeid);//上下条时切换模板
            this.initFormData(form.operate, form, entryid);
            return;
        }
        this.initFormField(form, 'hide', nodeid);
        this.initFormData('look', form, entryid);
    },

    initFormData:function (operate, form, entryid,flag) {
        var formview = form.up('EntryFormView');
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
        }else{
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
        if(flag){
            this.activeForm(form);
        }
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
    // + " " + date.getHours() + seperator2 + date.getMinutes();
    // + seperator2 + date.getSeconds();
    return currentdate;
}
