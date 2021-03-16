/**
 * Created by Administrator on 2019/6/13.
 */
Ext.define('Accept.controller.AcceptController',{
    extend: 'Ext.app.Controller',
    views:[
        'AcceptView','AcceptdocView','AcceptdocGridView',
        'AcceptAddDocForm','AcceptdocTreeComboboxView',
        'AcceptdocBatchForm','SterilizingView',
        'FinishsterilizeView','FinishstoreView',
        'SterilizingGridView','FinishsterilizeGridView',
        'FinishstoreGridView'
    ],
    stores:[
        'AcceptdocFinishGridStore','AcceptdocBatchGridStore',
        'SterilizingGridStore','FinishsterilizeGridStore',
        'FinishstoreGridStore'
    ],
    models:[
        'AcceptdocFinishGridModel','AcceptdocBatchGridModel'
    ],

    init:function () {
        var AcceptdocGrid,southGrid,grid;
        this.control({
            'AcceptView':{
                render:function (view) {
                    AcceptdocGrid = view.down('[itemId=acceptdoc]').down('AcceptdocGridView');
                },
                tabchange:function(view) {//选项卡切换
                    if(view.activeTab.title == '接收单据'){
                        grid = view.down('AcceptdocView');
                    }
                    if(view.activeTab.title == '正在消毒'){
                        grid = view.down('SterilizingView');
                    }
                    if(view.activeTab.title == '已消毒'){
                        grid = view.down('FinishsterilizeView');
                    }if(view.activeTab.title == '已入库'){
                        grid = view.down('FinishstoreView');
                    }
                    var northStore = grid.down('[itemId=northgrid]').getStore();
                    var southStore = grid.down('[itemId=southgrid]').getStore();
                    northStore.reload();
                    southStore.removeAll();
                }
            },
            'AcceptdocGridView [itemId=add]':{//接收单据-新增单据
                click: this.addDoc
            },'AcceptdocGridView [itemId=update]':{//接收单据-修改单据
                click: this.updateDoc
            },'AcceptdocGridView [itemId=sampling]': {//接收单据-打印
                click: this.printDocHandler
            },
            'AcceptdocGridView [itemId=del]':{//接收单据-删除
                click:function (btn) {
                    this.delDoc(btn);
                }
            },
            'AcceptAddDocForm [itemId=docAddSubmit]':{//接收单据-新增单据-提交
                click:function (btn) {
                    this.submitDoc(btn,AcceptdocGrid);
                }
            },
            'AcceptdocView [itemId=add]':{//接收单据-新建批次
                click:function (btn) {
                    this.addBatch(btn,AcceptdocGrid);
                }
            },
            'AcceptdocBatchForm [itemId=batchAddSubmit]':{//接收单据-新建批次-提交
                click:function (btn) {
                    this.submitBatch(btn,AcceptdocGrid);
                }
            },
            'AcceptdocGridView,SterilizingGridView,FinishsterilizeGridView,FinishstoreGridView':{//单据列表切换
                itemclick: this.itemclickHandler
            },
            'AcceptdocView [itemId=steriliz]':{//接收单据-消毒
                click:function (btn) {
                    this.sterilize(btn);
                }
            },
            'SterilizingView [itemId=finish]':{//正在消毒-完成
                click:function (btn) {
                    this.finish(btn);
                }
            },
            'FinishsterilizeView [itemId=putStorage]':{//已消毒-入库
                click:function (btn) {
                    this.putStorage(btn,AcceptdocGrid);
                }
            }
        })
    },
    addDoc:function (btn) {
        var docAddView = Ext.create('Accept.view.AcceptAddDocForm',{
            operationType:'add',
            title:'新增单据'
        });
        docAddView.show();
    },
    updateDoc:function(btn){
        var record = btn.up('AcceptdocGridView').getSelectionModel().getSelection();
        if(record.length < 1){
            XD.msg('请选择需要操作的数据!');
            return;
        }else if (record.length > 1){
            XD.msg('不能同时操作多条的数据!');
            return;
        }
        var acceptdocid = record[0].get('acceptdocid');
        var submitter = record[0].get('submitter');
        var submitorgan = record[0].get('submitorgan');
        var submitdate = record[0].get('submitdate');
        var docremark = record[0].get('docremark');
        var archiveNum = record[0].get('archivenum');
        var updateDocView = Ext.create('Accept.view.AcceptAddDocForm',{
            operationType:'update',
            title:'修改单据'
        });
        updateDocView.down('[itemId=acceptItemId]').setValue(acceptdocid);
        updateDocView.down('[itemId=submitterId]').setValue(submitter);
        updateDocView.down('[itemId=submitorganId]').setValue(submitorgan);
        updateDocView.down('[itemId=submitdateId]').setValue(submitdate);
        updateDocView.down('[itemId=docremarkId]').setValue(docremark);
        updateDocView.down('[itemId=archiveNumId]').setValue(archiveNum);
        updateDocView.show();
    },
    delDoc:function (btn) {
        var grid = btn.findParentByType('AcceptdocGridView');
        var gridModel = grid.getSelectionModel();
        var record = gridModel.getSelection();
        if(record.length == 0){
            XD.msg('请选择需要操作的数据!');
            return;
        }
        var acceptdocid = [];
        for(var i = 0;i<record.length;i++){
            acceptdocid.push(record[i].get('acceptdocid'));
        }
        XD.confirm('确定删除这'+acceptdocid.length+'条单据吗？',function() {
            Ext.Ajax.request({
                params:{acceptdocid:acceptdocid},
                url:'/accept/delDoc',
                method:'POST',
                sync:true,
                success: function (resp) {
                    var respText = Ext.decode(resp.responseText);
                    if (respText.success == true) {
                        grid.getStore().reload();
                        var southStore = grid.findParentByType('AcceptdocView').down('[itemId=southgrid]').getStore();
                        southStore.removeAll();
                        gridModel.clearSelections();
                    }
                    XD.msg(respText.msg);
                },
                failure: function () {
                    gridModel.clearSelections();
                    XD.msg('操作失败');
                }
            })
        })

    },
    submitDoc:function (btn,AcceptdocGrid) {
        /*if(btn.text=='新增单据'){
            return false;
        }*/
        var formView = btn.findParentByType('AcceptAddDocForm');
        var form = formView.down('form');
        if(!form.isValid()){
            XD.msg('有必填项未填写');
            return;
        }
        var url;
        if (formView.operationType == "update"){
             url = '/accept/update';
        }else{
            url = '/accept/saveDoc';
        }
        form.submit({
            waitTitle: '提示',
            waitMsg: '正在处理请稍后...',
            url:url,
            method:'POST',
            success:function (form, action) {
                var respText = Ext.decode(action.response.responseText);
                if (respText.success == true){
                    XD.msg(respText.msg);
                    formView.close();
                    AcceptdocGrid.getStore().reload();
                    //AcceptdocGrid.getSelectionModel().clearSelections();
                    var southStore = AcceptdocGrid.findParentByType('AcceptdocView').down('[itemId=southgrid]').getStore();
                    southStore.removeAll();
                }else{
                    XD.msg(respText.msg);
                    formView.close();
                }
            },failure:function () {
                XD.msg('操作失败');
            }
        })
    },
    addBatch:function (btn,AcceptdocGrid) {
        if(btn.text=='新增单据'){
            return false;
        }

        //southGrid.getSelectionModel().clearSelections();
        var gridModel = AcceptdocGrid.getSelectionModel();
        var record = gridModel.getSelection();
        if(record.length < 1){
            XD.msg('请选择需要操作的数据!');
            return;
        }else if (record.length > 1){
            XD.msg('不能同时操作多条的数据!');
            return;
        }
        var acceptdocid = record[0].get('acceptdocid');
        var batchAddView = Ext.create('Accept.view.AcceptdocBatchForm');
        var batchAddForm = batchAddView.down('form');
        batchAddForm.load({
            url: '/accept/getBatchAddForm',
            params:{acceptdocid:acceptdocid},
            success: function (form, action) {
                var respText = Ext.decode(action.response.responseText);
                if(respText.msg !="失败"){
                    batchAddForm.down("[itemId=startScope]").setValue(respText.msg);
                    batchAddView.show();
                }else {
                    XD.msg('所有的档案都新建批次');
                }
            },
            failure: function () {
                XD.msg('获取批次号失败');
            }
        });
    },
    submitBatch:function (btn,AcceptdocGrid) {
        var formView = btn.findParentByType('AcceptdocBatchForm');
        var form = formView.down('form');
        var archiveScope = form.down('[itemId=startScope]').getValue()+'-'+form.down('[itemId=endScope]').getValue();
        if(!form.isValid()){
            XD.msg('有必填项未填写');
            return;
        }
        form.submit({
            waitTitle: '提示',
            waitMsg: '正在处理请稍后...',
            url:'/accept/saveBatch',
            method:'POST',
            params:{archivescope:archiveScope},
            success:function (form,action) {
                var respText = Ext.decode(action.response.responseText);
                if(respText.msg !="失败"){
                    XD.msg('操作成功');
                    formView.close();
                    southGrid.getStore().loadPage(1);
                    AcceptdocGrid.getStore().reload();
                    AcceptdocGrid.getSelectionModel().clearSelections();
                }else{
                    XD.msg('档案范围超过档案数量');
                }
            },failure:function () {
                XD.msg('操作失败');
            }
        })
    },
    itemclickHandler:function (view,record,item,index,e) {
        /*var acceptdocid = record.get('acceptdocid');
        southGrid = view.findParentByType('AcceptdocView').down('[itemId=southgrid]');
        var southGridStore = southGrid.getStore();
        southGrid.expand();
        southGridStore.proxy.extraParams.acceptdocid = acceptdocid;
        southGridStore.proxy.extraParams.state = '';
        southGridStore.loadPage(1);
        southGrid.getSelectionModel().clearSelections();*/
        var tabTitle = view.up('AcceptView').activeTab.title;
        if(tabTitle == '接收单据'){
            southGrid = view.findParentByType('AcceptdocView').down('[itemId=southgrid]');
            var southGridStore = southGrid.getStore();
            southGridStore.proxy.extraParams.state = '';
        } else if(tabTitle == '正在消毒'){
            southGrid = view.findParentByType('SterilizingView').down('[itemId=southgrid]');
            var southGridStore = southGrid.getStore();
        } else if(tabTitle == '已消毒'){
            southGrid = view.findParentByType('FinishsterilizeView').down('[itemId=southgrid]');
            var southGridStore = southGrid.getStore();
        } else {
            southGrid = view.findParentByType('FinishstoreView').down('[itemId=southgrid]');
            var southGridStore = southGrid.getStore();
        }
        var acceptdocid = record.get('acceptdocid');
        southGrid.expand();
        southGridStore.proxy.extraParams.acceptdocid = acceptdocid;
        southGridStore.loadPage(1);
        southGrid.getSelectionModel().clearSelections();
    },
    sterilize:function (btn) {
        var acceptdocGridView = btn.findParentByType('AcceptdocView').down('AcceptdocGridView');
        var gridModel = btn.findParentByType('entrygrid').getSelectionModel();
        var record = gridModel.getSelection();
        if(record.length == 0){
            XD.msg('请至少选择一条需要操作的数据!');
            return;
        }
        var batchid = [];
        var acceptdocid = record[0].get('acceptdocid');
        for(var i = 0;i<record.length;i++){
            if(record[i].get('state')!=''){
                XD.msg('选中的数据含有已处理数据,请重新选择！');
              //  southGrid.getSelectionModel().clearSelections();
                return;
            }
            batchid.push(record[i].get('batchid'));

        }
        XD.confirm('确定要消毒这'+batchid.length+'条批次吗？',function() {
            Ext.Ajax.request({
                params:{batchid:batchid,acceptdocid:acceptdocid},
                url:'/accept/sterilizeBatch',
                method:'POST',
                sync:true,
                success: function (resp) {
                    var respText = Ext.decode(resp.responseText);
                    if (respText.success == true) {
                        gridModel.clearSelections();//清除批次列表选中数据
                        var southGridStore = southGrid.getStore();
                        southGridStore.loadPage(1);//批次列表刷新
                        acceptdocGridView.getSelectionModel().clearSelections();//清除单据列表选中数据
                        acceptdocGridView.getStore().reload();
                    }
                    XD.msg(respText.msg);
                },
                failure: function () {
                    gridModel.clearSelections();
                    XD.msg('操作失败');
                }
            })
        })
    },
    finish:function (btn) {
        var grid = btn.findParentByType('SterilizingView');
        var northgrid = grid.down('SterilizingGridView');
        var southgrid = grid.down('entrygrid');
        northgrid.getSelectionModel().clearSelections();
        var gridModel = southgrid.getSelectionModel();
        var record = gridModel.getSelection();
        if(record.length == 0){
            XD.msg('请至少选择一条需要操作的数据!');
            return;
        }
        var batchid = [];
        for(var i = 0;i<record.length;i++){
            batchid.push(record[i].get('batchid'));

        }
        XD.confirm('确定要完成这'+batchid.length+'条批次吗？',function() {
            Ext.Ajax.request({
                params:{batchid:batchid},
                url:'/accept/finishsterilizeBatch',
                method:'POST',
                sync:true,
                success: function (resp) {
                    var respText = Ext.decode(resp.responseText);
                    if (respText.success == true) {
                        southgrid.getStore().reload();
                        northgrid.getStore().reload();
                    }
                    XD.msg(respText.msg);
                },
                failure: function () {
                    gridModel.clearSelections();
                    XD.msg('操作失败');
                }
            })
        })
    },
    putStorage:function (btn,AcceptdocGrid) {
        var grid = btn.findParentByType('FinishsterilizeView');
        var northgrid = grid.down('FinishsterilizeGridView');
        var southgrid = grid.down('entrygrid');
        northgrid.getSelectionModel().clearSelections();
        //AcceptdocGrid.getSelectionModel().clearSelections();
        //var grid = btn.findParentByType('FinishsterilizeView');
        var gridModel = southgrid.getSelectionModel();
        var record = gridModel.getSelection();
        if(record.length == 0){
            XD.msg('请至少选择一条需要操作的数据!');
            return;
        }
        var batchid = [];
        for(var i = 0;i<record.length;i++){
            batchid.push(record[i].get('batchid'));

        }
        XD.confirm('确定要入库这'+batchid.length+'条批次吗？',function() {
            Ext.Ajax.request({
                params:{batchid:batchid},
                url:'/accept/putStorageBatch',
                method:'POST',
                sync:true,
                success: function (resp) {
                    var respText = Ext.decode(resp.responseText);
                    if (respText.success == true) {
                        southgrid.getStore().reload();
                        northgrid.getStore().reload();
                    }
                    XD.msg(respText.msg);
                },
                failure: function () {
                    gridModel.clearSelections();
                    XD.msg('操作失败');
                }
            })
        })
    },

    printDocHandler:function (btn) {//打印单据
        var ids = [];
        var params= {};
        var acceptdocGrid = btn.findParentByType('AcceptdocGridView');
        var record = acceptdocGrid.getSelectionModel().getSelection();
        if(record.length < 1){
            XD.msg('请选择需要打印的接收单据');
            return;
        }
        Ext.each(record,function(){
            ids.push(this.get('acceptdocid'));
        });

        if(reportServer == 'UReport') {
            params['acceptdocid'] = ids.join(",");
            XD.UReportPrint(null, '库房接收单据', params);
        }
        else if(reportServer == 'FReport') {
            XD.FRprint(null, '库房接收单据', ids.length > 0 ? "'acceptdocid':'" + ids.join(",") + "'" : '');
        }
    },
});