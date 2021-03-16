/**
 * Created by xd on 2017/10/21.
 */
var updateBorrowCode="";
var formAndGridView;
Ext.define('Restitution.controller.RestitutionAdminsController', {
     extend: 'Ext.app.Controller',

    views: [
        'RestitutionAdminsView',
        'RestitutionWghView',
        'RestitutionYghView',
        'RestitutionYzcView',
        'RestitutionRegisterView',
        'XjDescAddView',
        'RestitutionSqView',
        'RestitutionFormItemView',
        'RestitutionFormGridView',
        'RestitutionGridView',
        'RestitutionSearchView',
        'RestitutionLookItemView',
        'RestitutionLookGridView',
        'RestitutionLookSqView',
        'RestitutionUpdateItemView',
        'RestitutionUpdateGridView',
        'RestitutionUpdateSqView',
        'RestitutionUpdateView',
        'RestitutionUpdateSearchView',
        'RestitutionFormView',
        'FormAndGridView',
        'FormView',
        'ReturnMessageView'
    ],//加载view
    stores: [
        'RestitutionWghStore',
        'RestitutionYghStore',
        'RestitutionYzcStore',
        'RestitutionRegisterStore',
        'RestitutionFormGridStore',
        'RestitutionGridStore',
        'RestitutionFormUpdateGridStore'
    ],//加载store
    models: [
        'RestitutionWghModel',
        'RestitutionYghModel',
        'RestitutionYzcModel',
        'RestitutionRegisterModel',
        'RestitutionFormGridModel',
        'RestitutionGridModel'
    ],//加载model
    init: function (view) {
        var attr = {flag:'未归还'};
        var lendGrid,lendWined,importWined,isFormSubmit;
        this.control({
            // 'RestitutionSearchView':{
            //     render:function (view) {
            //         var closeBtn = view.down('[itemId=topCloseBtn]');
            //         // var advandedSearchBtn = view.down('[itemId=advancedSearchBtn]');
            //         var sGrid = view.down('restitutionGridView');
            //         var buttons = sGrid.down("toolbar");
            //         var tbseparator = sGrid.down("toolbar").query('tbseparator');
            //         var formview = view.down('EntryFormView');
            //         var soildview = formview.down('solid');
            //
            //         closeBtn.show();
            //
            //         sGrid.initGrid();
            //     }
            // },
            'restitutionRegisterView':{
                afterrender:function (view) {
                    if(taskid){
                        var tag = view.up('restitutionAdminsView');
                        tag.setActiveItem(tag.down('[itemId=dzJyId]'));
                    }
                }
            },
            'restitutionAdminsView':{
                tabchange:function(view){
                    if(view.activeTab.title == '未归还'){
                        var grid = view.down('restitutionWghView');
                        grid.initGrid({flag:'未归还'});
                    }else if(view.activeTab.title == '已归还'){
                        var grid = view.down('restitutionYghView');
                        grid.initGrid({flag:'已归还'});
                    }else if(view.activeTab.title == '已转出'){
                        var grid = view.down('restitutionYzcView');
                        grid.initGrid({flag:'已转出'});
                    }
                    else if(view.activeTab.title == '登记'){
                        var grid = view.down('restitutionRegisterView');
                        grid.getStore().reload();
                    }
                }
            },

            'restitutionWghView':{
                afterrender:function (grid) {
                    if(borrowmsgid){
                        attr = {flag:'未归还',condition:'msgid',operator:'equal',content:borrowmsgid};
                        grid.down('fieldcontainer').hide();
                    }
                    grid.initGrid(attr);
                    // grid.store.on('load',function(store,records,success){
                    //     for (var i = 0; i < records.length; i++) {
                    //         var record = records[i];
                    //         if(record.get('backdate')==getDateStr(0)){//到期
                    //             grid.getView().getRow(record).style.backgroundColor = '#FFFF00';
                    //         }else if(record.get('backdate')==getDateStr(1)){//即将到期
                    //             grid.getView().getRow(record).style.backgroundColor = '#00FF00';
                    //         }
                    //     }
                    // });
                    grid.on('itemmouseenter', function (view, record, item, index, e, eOpts) {
                        if (view.tip == null) {
                            view.tip = Ext.create('Ext.tip.ToolTip', {
                                target: view.el,
                                delegate: view.itemSelector,
                                renderTo: Ext.getBody()
                            });
                        };
                        view.el.clean();
                        if(record.get('backdate')==getDateStr(0)){//到期
                            view.tip.update('该查档到期');
                        }else if(record.get('backdate')==getDateStr(1)){//即将到期
                            view.tip.update('该查档即将到期');
                        }else if(record.get('backdate')>getDateStr(0)){
                            view.tip.update('该查档'+record.get('backdate')+'到期');
                        }else{
                            view.tip.update('该查档已过期');
                        }
                    });
                }
            },
            'restitutionYghView':{
                afterrender:function (grid) {
                    grid.initGrid({flag:'已归还'});
                }
            },
            'restitutionYzcView':{
                afterrender:function (grid) {
                    grid.initGrid({flag:'已转出'});
                }
            },
            'restitutionRegisterView':{
                afterrender:function (grid) {
                    grid.initGrid();
                }
            },
            'restitutionWghView button[itemId=restitution]':{//归还
                click:function(btn){
                    var grid = btn.findParentByType('restitutionWghView');
                    var select = grid.getSelectionModel();
                    var borrowmsgs = select.selected.items;
                    var selectCount = borrowmsgs.length;
                    if(selectCount==0){
                        XD.msg('至少选择一条数据');
                        return;
                    }else{
                        XD.confirm('确定要归还这' + selectCount + '条数据吗',function () {
                            var win = Ext.create("Restitution.view.ReturnMessageView", {});
                            Ext.Ajax.request({
                                url: '/user/getCurrentusr',
                                method: 'get',
                                sync: true,
                                success: function (response) {
                                    var resp = Ext.decode(response.responseText);
                                    win.down('[itemId=returnMan]').setValue(resp.data);
                                }
                            });
                            win.grid=grid;
                            win.borrowmsgs=borrowmsgs;
                            win.show();
                        })
                    }
                }
            },
            'ReturnMessage button[itemId=saveGh]': {//归还信息输入框-确认归还
                click: function (btn) {
                    var ids = new Array();
                    var messageView=btn.up('ReturnMessage');
                    var returnMan=messageView.down('[itemId=returnMan]').getValue();
                    var remarkValue=messageView.down('[itemId=remarkId]').getValue();
                    for(var i=0;i<messageView.borrowmsgs.length;i++){
                        ids.push(messageView.borrowmsgs[i].get('id'));
                    }
                    Ext.Ajax.request({
                        params: {ids: ids,returnMan:returnMan,remarkValue:remarkValue},
                        url: '/jyAdmins/restitution',
                        method: 'POST',
                        sync: true,
                        success: function (resp) {
                            var respText = Ext.decode(resp.responseText);
                            if (respText.success == true) {
                                XD.msg(respText.msg);
                                messageView.close();
                                messageView.grid.notResetInitGrid(attr);
                            } else {
                                XD.msg(respText.msg);
                            }
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },
            'ReturnMessage button[itemId=cancelGh]': {//归还信息输入框-关闭
                click: function (btn) {
                    var returnMessage = btn.up('ReturnMessage');
                    returnMessage.close();
                }
            },
            'restitutionUpdateGridView button[itemId=UpdateRemoveId]':{//修改--移除条目
                click:function(btn){
                    var entryGrid = btn.findParentByType('restitutionUpdateGridView');
                    var gridModel = entryGrid.getSelectionModel();
                    var record = gridModel.getSelection();
                    if(record.length==0){
                        XD.msg('请选择需要操作的数据!');
                        return;
                    }
                    var dataids = [];
                    for (var i = 0; i < record.length; i++) {
                        dataids.push(record[i].get('entryid'));
                    }
                    XD.confirm('是否确定移除？',function() {
                        Ext.Ajax.request({
                            params: {
                                borrowcode: updateBorrowCode,
                                dataids: dataids
                            },
                            url: '/jyAdmins/removeImport',
                            method: 'POST',
                            sync: true,
                            success: function (resp) {
                                var respText = Ext.decode(resp.responseText);
                                if (respText.success == true) {
                                    entryGrid.getStore().proxy.extraParams.borrowcode = updateBorrowCode;
                                    entryGrid.getSelectionModel().clearSelections();
                                    entryGrid.getStore().reload();
                                }
                                XD.msg(respText.msg);
                            },
                            failure: function () {
                                XD.msg('操作失败');
                            }
                        });
                    },this);
                    gridModel.clearSelections();
                }
            }
            ,
            'restitutionWghView button[itemId=renewal]':{//续借
                click:function(btn){
                    var grid = btn.findParentByType('restitutionWghView');
                    var select = grid.getSelectionModel();
                    var entrys = select.selected.items;
                    var selectCount = entrys.length;
                    if(selectCount==0){
                        XD.msg('请选择数据');
                        return;
                    }

                    var ids = [];
                    for(var i=0;i<entrys.length;i++){
                        ids.push(entrys[i].get('id'));
                    }
                    var xjDescAddView = Ext.create('Restitution.view.XjDescAddView',{
                        wghGrid:grid,
                        ids:ids
                    });
                    xjDescAddView.show();
                }
            },

            'restitutionWghView button[itemId=reason]':{//查看续借理由
                click:function(btn){
                    var grid = btn.findParentByType('restitutionWghView');
                    var select = grid.getSelectionModel();
                    var entrys = select.selected.items;
                    var selectCount = entrys.length;
                    if(selectCount==0){
                        XD.msg('请选择数据');
                        return;
                    }else if(selectCount > 1){
                        XD.msg('请选择一条数据');
                        return;
                    }
                    var win=Ext.create("Restitution.view.XjDescAddView");
                    win.down('[itemId = xjAddSubmit]').setHidden(true);
                    win.down('form').down('textfield').setEditable(false);
                    win.down('form').down('textarea').setEditable(false);
                    win.down('form').loadRecord(entrys[0]);
                    win.show();
                }
            },

            'xjDescAddView button[itemId=xjAddSubmit]':{//续借表单提交
                click:function(btn){
                    var xjDescAddWin = btn.findParentByType('xjDescAddView');
                    var ts = xjDescAddWin.down('form').down('textfield').value;
                    var desci = xjDescAddWin.down('form').down('textarea').value;
                    if(isNaN(ts)||parseInt(ts)<1){
                        XD.msg('请输入正确格式');
                        return;
                    }

                    Ext.Ajax.request({
                        params: {
                            ids:xjDescAddWin.ids,
                            ts:ts,
                            xjapprove:desci+'\n'+'续借天数：'+ts+'\t'+'续借日期：'+getNowFormatDate(),
                            flag:false
                        },
                        url: '/electron/stXjAddFormBill',
                        method: 'POST',
                        sync: true,
                        success: function () {
                            XD.msg('续借成功');
                            xjDescAddWin.close();
                            xjDescAddWin.wghGrid.notResetInitGrid(attr);
                            //parent.parent.closeObj.close();//刷新通知栏
                        },
                        failure : function() {
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            'xjDescAddView button[itemId=xjAddClose]':{
                click:function(btn){
                    btn.findParentByType('xjDescAddView').close();
                }
            },

            'restitutionWghView button[itemId=print]':{//未归还　打印清册
                click:function(btn){
                    var grid = btn.findParentByType('restitutionWghView');
                    var ids = [];
                    var params = {};
                    var records = grid.getSelectionModel().getSelection();
                    if(records.length==0){
                        XD.msg('请选择需要打印的查档信息');
                        return;
                    }
                    Ext.each(records,function(){
                        ids.push(this.get('id').trim());
                    });
                    if(reportServer == 'UReport') {
                        params['msgid'] = ids.join(",");
                        XD.UReportPrint(null, '借阅管理', params);
                    }
                    else if(reportServer == 'FReport') {
                        XD.FRprint(null, '借阅管理', ids.length > 0 ? "'id':'" + ids.join(",") + "'" : '');
                    }
                }
            },
            'restitutionWghView button[itemId=refresh]':{//未归还　刷新
                click:function(btn){
                    var grid = btn.findParentByType('restitutionWghView');
                    grid.notResetInitGrid(attr);
                }
            },
            'restitutionWghView button[itemId=askToReturn]':{//未归还  催还
                click:function (btn) {
                    var grid = btn.findParentByType('restitutionWghView');
                    var select = grid.getSelectionModel();
                    var entrys = select.selected.items;
                    var selectCount = entrys.length;
                    if(selectCount==0){
                        XD.msg('请选择数据');
                        return;
                    }
                    var ids = [];
                    for(var i=0;i<entrys.length;i++){
                        ids.push(entrys[i].get('id'));
                    }
                    Ext.Ajax.request({
                        url: '/jyAdmins/askToReturn',
                        async:false,
                        params:{
                            ids:ids
                        },
                        success: function (response) {
                        }
                    });
                }
            },
            'restitutionYghView button[itemId=print]':{//已归还　打印清册
                click:function(btn){
                    var grid = btn.findParentByType('restitutionYghView');
                    var ids = [];
                    var records = grid.getSelectionModel().getSelection();
                    if(records.length==0){
                        XD.msg('请选择需要打印的查档信息');
                        return;
                    }
                    Ext.each(records,function(){
                        ids.push(this.get('id').trim());
                    });
                    var params ={};
                    if(reportServer == 'UReport') {
                        params['msgid'] = ids.join(",");
                        XD.UReportPrint(null, '借阅管理', params);
                    }
                    else if(reportServer == 'FReport') {
                        XD.FRprint(null, '借阅管理', ids.length > 0 ? "'id':'" + ids.join(",") + "'" : '');
                    }
                }
            },
            'restitutionYghView button[itemId=refresh]':{//已归还　刷新
                click:function(btn){
                    var grid = btn.findParentByType('restitutionYghView');
                    grid.notResetInitGrid({flag:'已归还'});
                }
            },

            'restitutionRegisterView button[itemId=lendRegister]': {//查档登记
                click:function(btn){
                    isFormSubmit = null;
                    var lendWin = Ext.create('Restitution.view.RestitutionSqView', {});
                    var formGridViewStore =  lendWin.down('restitutionFormGridView').getStore();
                    formGridViewStore.proxy.extraParams.borrowcode = '';
                    formGridViewStore.reload();
                    lendWin.show();
                    lendWined = lendWin;
                    lendGrid = btn.findParentByType('restitutionRegisterView');
                }
            },
            'restitutionRegisterView button[itemId=checkEntries]': {//查看
                click:function(btn){
                    var borrowGrid = btn.up('restitutionRegisterView');
                    var gridModel = borrowGrid.getSelectionModel();
                    var record = gridModel.getSelection();
                    if(record.length==0){
                        XD.msg('请选择需要查看的数据!');
                        return;
                    }else if(record.length>1){
                        XD.msg('不支持多条数据同时查看!');
                        return;
                    }
                    var borrowCodes = [];
                    var borrowId=[];
                    for (var i = 0; i < record.length; i++) {
                        borrowCodes.push(record[i].get('borrowcode'));
                        borrowId.push(record[i].get('id'));
                    }
                    //对应的档案条目
                    var lendWin = Ext.create('Restitution.view.RestitutionLookSqView', {});
                    var formGridViewStore =  lendWin.down('restitutionLookGridView').getStore();
                    formGridViewStore.proxy.extraParams.borrowcode = borrowCodes;//对应的查档条目
                    //查档条目数据回显
                    var d = record[0].data;
                    var Form=lendWin.down('restitutionLookItemView')
                    Form.loadRecord({getData: function () {return d;}});

                    formGridViewStore.reload();
                    lendWin.show();
                    lendWined = lendWin;
                    lendGrid = btn.findParentByType('restitutionRegisterView');
                }
            },

            'restitutionRegisterView button[itemId=updataEntries]': {//修改
                click:function(btn){
                    var borrowGrid = btn.up('restitutionRegisterView');
                    var gridModel = borrowGrid.getSelectionModel();
                    var record = gridModel.getSelection();
                    if(record.length==0){
                        XD.msg('请选择需要修改的的数据!');
                        return;
                    }else if(record.length>1){
                        XD.msg('不支持多条数据修改!');
                        return;
                    }
                    var borrowCodes = [];
                    var borrowId=[];
                    for (var i = 0; i < record.length; i++) {
                        borrowCodes.push(record[i].get('borrowcode'));
                        borrowId.push(record[i].get('id'));
                    }
                    updateBorrowCode=borrowCodes
                    isFormSubmit = borrowCodes;
                    //初始化对应条目
                    var lendWin = Ext.create('Restitution.view.RestitutionUpdateSqView', {});
                    var formGridViewStore =  lendWin.down('restitutionUpdateGridView').getStore();
                    formGridViewStore.proxy.extraParams.borrowcode = borrowCodes;//对应的查档条目
                    var d = record[0].data;
                    var Form=lendWin.down('restitutionUpdateItemView')
                    //数据回显
                    Form.loadRecord({getData: function () {return d;}});

                    formGridViewStore.reload();
                    lendWin.show();

                    lendWined = lendWin;
                    lendGrid = btn.findParentByType('restitutionRegisterView');
                }
            },
            'restitutionRegisterView button[itemId=delete]': {//删除
                click:function(btn){
                   var borrowGrid = btn.up('restitutionRegisterView');
                    var gridModel = borrowGrid.getSelectionModel();
                    var record = gridModel.getSelection();
                    if(record.length==0){
                        XD.msg('请选择需要操作的数据!');
                        return;
                    }
                    var borrowCodes = [];
                    for (var i = 0; i < record.length; i++) {
                        borrowCodes.push(record[i].get('borrowcode'));
                    }

                    XD.confirm('确定删除这'+borrowCodes.length+'条单据吗？',function() {
                        Ext.Ajax.request({
                            params: {
                                borrowCodes:borrowCodes
                            },
                            url: '/jyAdmins/deteteImport',
                            method: 'POST',
                            sync: true,
                            success: function (resp) {
                                var respText = Ext.decode(resp.responseText);
                                if (respText.success == true) {
                                    gridModel.clearSelections();
                                    borrowGrid.getStore().reload();
                                }
                                XD.msg(respText.msg);
                            },
                            failure: function () {
                                gridModel.clearSelections();
                                XD.msg('操作失败');
                            }
                        });
                    },this);
                }
            },

            'restitutionRegisterView button[itemId=print]':{//登记　打印清册
                click:function(btn){
                    var grid = btn.findParentByType('restitutionRegisterView');
                    var ids = [];
                    var params = {};
                    var records = grid.getSelectionModel().getSelection();
                    if(records.length==0){
                        XD.msg('请选择需要打印的查档信息');
                        return;
                    }
                    Ext.each(records,function(){
                        ids.push(this.get('id').trim());
                    });
                    if(reportServer == 'UReport') {
                        params['docid'] = ids.join(",");
                        XD.UReportPrint(null, '借阅登记管理', params);
                    }
                    else if(reportServer == 'FReport'){
                        XD.FRprint(null, '借阅登记管理', ids.length > 0 ? "'id':'" + ids.join(",")+"'": '');
                    }
                }
            },

            'restitutionRegisterView button[itemId=moveEntries]': {//转出
                click:function(btn){
                    var grid = btn.findParentByType('restitutionRegisterView');
                    var select = grid.getSelectionModel();
                    var borrowmsgs = select.selected.items;
                    var selectCount = borrowmsgs.length;
                    if(selectCount==0){
                        XD.msg('至少选择一条数据');
                        return;
                    }else{
                        var state = false;
                        var ids = [];
                        for (var i = 0; i < selectCount; i++) {
                            ids.push(borrowmsgs[i].get('id'));
                            if(borrowmsgs[i].get('returnstate')!='已调档'){
                                state = true;
                            }
                        }
                        if(state){
                            XD.msg('请选择“已调档”的记录进行转出');
                            return;
                        }
                        XD.confirm('确定要转出这' + selectCount + '条数据吗',function () {
                            Ext.Ajax.request({
                                params: {ids: ids},
                                url: '/jyAdmins/moveout',
                                method: 'POST',
                                sync: true,
                                success: function (resp) {
                                    var respText = Ext.decode(resp.responseText);
                                    if (respText.success == true) {
                                        select.clearSelections();
                                        grid.getStore().reload();
                                    }
                                    XD.msg(respText.msg);
                                },
                                failure: function () {
                                    select.clearSelections();
                                    XD.msg('操作失败');
                                }
                            });
                        })
                    }
                }
            },

            'restitutionFormGridView button[itemId=importId]': {//查档导入条目
                click:function(btn){
                    if(!isFormSubmit){
                        XD.msg('请先提交表单');return;
                    }
                    importWined = this.importWin(isFormSubmit);
                }
            }
            ,

            'restitutionUpdateGridView button[itemId=UpdateAddId]': {//修改--添加条目
                click:function(btn){

                    var entryGrid = btn.findParentByType('restitutionUpdateGridView');
                    var gridModel = entryGrid.getSelectionModel();
                    gridModel.clearSelections();
                   var record=gridModel.getSelection()
                    console.log(record)
                    importWined = this.UpdateImportWin(isFormSubmit);
                }
            }
            ,

            'restitutionFormGridView button[itemId=removeId]': {//查档移除条目
                click:function(btn){
                    var entryGrid = btn.findParentByType('restitutionFormGridView');
                    var gridModel = entryGrid.getSelectionModel();
                    var record = gridModel.getSelection();
                    if(record.length==0){
                        XD.msg('请选择需要操作的数据!');
                        return;
                    }
                    var dataids = [];
                    for (var i = 0; i < record.length; i++) {
                        dataids.push(record[i].get('entryid'));
                    }

                    XD.confirm('是否确定移除？',function() {
                        Ext.Ajax.request({
                            params: {
                                borrowcode: importWined.borrowcode,
                                dataids: dataids
                            },
                            url: '/jyAdmins/removeImport',
                            method: 'POST',
                            sync: true,
                            success: function (resp) {
                                var respText = Ext.decode(resp.responseText);
                                if (respText.success == true) {
                                    entryGrid.getStore().proxy.extraParams.borrowcode = importWined.borrowcode;
                                    entryGrid.getSelectionModel().clearSelections();
                                    entryGrid.getStore().reload();
                                }
                                XD.msg(respText.msg);
                            },
                            failure: function () {
                                XD.msg('操作失败');
                            }
                        });
                    },this);
                }
            },

            'restitutionUpdateItemView button[itemId=UpdateSave]': {//修改页面提交保存表单
                click:function(btn){
                    var form = btn.findParentByType('restitutionUpdateItemView'),
                        borrowts = form.getComponent('borrowtsId').getValue(),
                        borrowmantel = form.getComponent('borrowmantelId').getValue();
                    if(!borrowmantel||!(/^((0\d{2,3})-)(\d{7,8})(-(\d{3,}))?$/.test(borrowmantel)||/^0?1[3|4|5|8][0-9]\d{8}$/.test(borrowmantel))){
                        XD.msg('电话号码不合法');return;
                    }
                    if(form.isValid()){
                        /*if(isFormSubmit){
                            XD.msg('不能重复提交表单');return;
                        }*/


                        if(!borrowts||isNaN(borrowts)||/\.|-/.test(borrowts+'')){
                            XD.msg('查档天数不合法');return;
                        }

                        var that = this;
                        form.submit({
                            url : '/jyAdmins/borrowUpdateSubmit',
                            method : 'POST',
                            params : {},// 此处可以添加额外参数
                            success : function(form, action) {
                                var respText = Ext.decode(action.response.responseText);
                                if (respText.success == true) {
                                    isFormSubmit = respText.data;//设置提交标识  borrowcode值
                                    //console.log(isFormSubmit)
                                    //importWined = that.importWin(isFormSubmit);
                                    XD.msg(respText.msg);
                                } else {
                                    XD.msg(respText.msg);
                                }
                            },
                            failure : function() {
                                XD.msg('操作失败');
                            }
                        });
                    }
                }
            },
            'restitutionFormItemView button[itemId=formSubmit]': {//查档提交表单
                click:function(btn){
                    var form = btn.findParentByType('restitutionFormItemView'),
                         borrowts = form.getComponent('borrowtsId').getValue(),
                        borrowmantel = form.getComponent('borrowmantelId').getValue();
                    // if(!borrowmantel||!(/^((0\d{2,3})-)(\d{7,8})(-(\d{3,}))?$/.test(borrowmantel)||/^0?1[3|4|5|8][0-9]\d{8}$/.test(borrowmantel))){
                    //     XD.msg('电话号码不合法');return;
                    // }
                    if(form.isValid()){
                        if(isFormSubmit){
                            XD.msg('不能重复提交表单');return;
                        }


                        if(!borrowts||isNaN(borrowts)||/\.|-/.test(borrowts+'')){
                            XD.msg('查档天数不合法');return;
                        }

                        var that = this;
                        form.submit({
                            url : '/jyAdmins/borrowFormSubmit',
                            method : 'POST',
                            params : {},// 此处可以添加额外参数
                            success : function(form, action) {
                                var respText = Ext.decode(action.response.responseText);
                                if (respText.success == true) {
                                    isFormSubmit = respText.data;//设置提交标识
                                    importWined = that.importWin(isFormSubmit);
                                    XD.msg(respText.msg);
                                } else {
                                    XD.msg(respText.msg);
                                }
                            },
                            failure : function() {
                                XD.msg('操作失败');
                            }
                        });
                    }
                }
            },

            'restitutionFormItemView button[itemId=formClose]': {//查档关闭提交页面
                click:function(btn){
                    lendGrid.getStore().reload();
                    lendWined.close();
                }
            },

            'restitutionLookItemView button[itemId=goBack]': {//查看页面-返回登记页面
                click:function(btn){
                    lendGrid.getStore().reload();
                    lendWined.close();
                }
            },

            'restitutionUpdateItemView button[itemId=goBack]': {//修改页面-返回登记页面
                click:function(btn){
                    lendWined.close();
                    var gridModel = lendGrid.getSelectionModel();
                    var record = gridModel.getSelection();
                    gridModel.clearSelections();

                    lendGrid.getStore().reload();

                }
            },
            'restitutionUpdateSearchView [itemId=simpleSearchSearchfieldId]':{
                search:function(searchfield){
                    this.UpdateSearchInfo(searchfield);
                }
            },
            'restitutionSearchView [itemId=simpleSearchSearchfieldId]':{
                search:function(searchfield){
                    this.searchInfo(searchfield);
                }
            },
            'restitutionUpdateView  button[itemId=entryImportId]': {//修改--导入条目
                click:function(view){
                    //importWined.borrowcode;
                    var sGrid = view.findParentByType('restitutionUpdateView');
                    var entryGrid = lendWined.down('restitutionUpdateGridView');//获取单据对应条目表格控件

                    var gridModel = sGrid.getSelectionModel();
                    var record = gridModel.getSelection();
                    if(record.length==0){
                        XD.msg('请选择需要操作的数据!');
                        return;
                    }
                    var dataids = [];
                    for (var i = 0; i < record.length; i++) {
                        dataids.push(record[i].get('entryid'));
                    }
                    console.log(dataids)
                    Ext.Ajax.request({
                        params: {
                            borrowcode:importWined.borrowcode,
                            dataids: dataids
                        },
                        url: '/jyAdmins/entryImport',
                        method: 'POST',
                        sync: true,
                        success: function (resp) {
                            var respText = Ext.decode(resp.responseText);
                            if (respText.success == true) {
                                entryGrid.getStore().proxy.extraParams.borrowcode = importWined.borrowcode;
                                entryGrid.getStore().reload();
                                sGrid.getStore().reload();
                            }
                            XD.msg(respText.msg);
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },
            'restitutionGridView  button[itemId=entryImportId]': {//导入条目
                click:function(view){
                    //importWined.borrowcode;
                    var sGrid = view.findParentByType('restitutionGridView');
                    var entryGrid = lendWined.down('restitutionFormGridView');//获取单据对应条目表格控件
                    var gridModel = sGrid.getSelectionModel();
                    var record = gridModel.getSelection();
                    if(record.length==0){
                        XD.msg('请选择需要操作的数据!');
                        return;
                    }
                    var dataids = [];
                    for (var i = 0; i < record.length; i++) {
                        dataids.push(record[i].get('entryid'));
                    }

                    Ext.Ajax.request({
                        params: {
                            borrowcode:importWined.borrowcode,
                            dataids: dataids
                        },
                        url: '/jyAdmins/entryImport',
                        method: 'POST',
                        sync: true,
                        success: function (resp) {
                            var respText = Ext.decode(resp.responseText);
                            if (respText.success == true) {
                                entryGrid.getStore().proxy.extraParams.borrowcode = importWined.borrowcode;
                                entryGrid.getStore().reload();
                                sGrid.getStore().reload();
                            }
                            XD.msg(respText.msg);
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            'restitutionUpdateView  button[itemId=entryImportBackId]': {//修改--导入条目返回
                click:function(btn){
                    var restitutionFormGridView = lendWined.down('restitutionUpdateGridView');
                    restitutionFormGridView.getStore().reload();
                    restitutionFormGridView.getSelectionModel().clearSelections();
                    importWined.close();
                }
            },
            'restitutionGridView  button[itemId=entryImportBackId]': {//导入条目返回
                click:function(btn){
                    var restitutionFormGridView = lendWined.down('restitutionFormGridView');
                    restitutionFormGridView.getStore().reload();
                    restitutionFormGridView.getSelectionModel().clearSelections();
                    importWined.close();
                }
            },
            'restitutionFormView [itemId=preBtn]':{//点击上一条
                click:function (btn) {
                    var form = btn.up('dynamicform');
                    this.preNextHandler(form, 'pre');
                }
            },
            'restitutionFormView [itemId=nextBtn]':{//点击下一条
                click:function (btn) {
                    var form = btn.up('dynamicform');
                    this.preNextHandler(form, 'next');
                }
            },
            'restitutionUpdateView button[itemId=lookImportId]':{
                click:function (btn) {
                    var restitutionUpdateView = btn.up('restitutionUpdateView');
                    // 获取到当前表单中的已选择数据
                    var record = restitutionUpdateView.getSelectionModel().selected;
                    var selectCount = record.length;
                    if(selectCount == 0){
                        XD.msg('请至少选择一条需要查看的数据');
                        return;
                    }
                    // 显示条目的表单信息
                    var formView = Ext.create('Restitution.view.FormView');
                    var dynamicform=formView.down('dynamicform');
                    var entryids = [];
                    var nodeid;
                    var nodeids = [];
                    for(var i=0;i<record.length;i++){
                        entryids.push(record.items[i].get('entryid'));
                        nodeid=record.items[i].get('nodeid');
                        nodeids.push(record.items[i].get('nodeid'));
                    }
                    var initFormFieldState = this.initFormField(dynamicform, 'hide',nodeid);

                    if(!initFormFieldState){//表单控件加载失败
                        return;
                    }
                    dynamicform.operate = 'look';
                    dynamicform.entryids = entryids;
                    dynamicform.entryid = entryids[0];
                    dynamicform.nodeids = nodeids;
                    this.initFormData('look',dynamicform, entryids[0]);

                    formView.show();
                }
            },
            'restitutionFormView button[itemId=back]':{//导入条目查看后关闭
                click:function (btn) {
                   var restitutionFormView = btn.up('restitutionFormView');
                   var formView = restitutionFormView.up('formView');
                   formView.close();
                }
            },
            'restitutionGridView button[itemId=lookImportId]':{//导入条目查看
                click:function (btn) {

                    var restitutionGrid = btn.up('restitutionGridView');
                    // 获取到当前表单中的已选择数据
                    var record = restitutionGrid.getSelectionModel().selected;
                    var selectCount = record.length;

                    // 显示条目的表单信息
                    var formView = Ext.create('Restitution.view.FormView');
                    var dynamicform=formView.down('dynamicform');

                    if(selectCount == 0){
                        XD.msg('请至少选择一条需要查看的数据');
                        return;
                    }
                    var entryids = [];
                    var nodeid;
                    var nodeids = [];
                    for(var i=0;i<record.length;i++){
                        entryids.push(record.items[i].get('entryid'));
                        nodeid=record.items[i].get('nodeid');
                        nodeids.push(record.items[i].get('nodeid'));
                    }
                    var initFormFieldState = this.initFormField(dynamicform, 'hide',nodeid);

                    if(!initFormFieldState){//表单控件加载失败
                        return;
                    }

                    dynamicform.operate = 'look';
                    dynamicform.entryids = entryids;
                    dynamicform.entryid = entryids[0];
                    dynamicform.nodeids = nodeids;
                    this.initFormData('look',dynamicform, entryids[0]);

                    formView.show();

                }
            },

            'restitutionWghView button[itemId=lookEntryId]':{//未归还-查看
                click:function (btn) {
                    var restitutionWghView = btn.up('restitutionWghView');
                    // 获取到当前表单中的已选择数据
                    var record = restitutionWghView.getSelectionModel().getSelection();
                    var selectCount = record.length;

                    // 显示条目的表单信息
                    var formView = Ext.create('Restitution.view.FormView');
                    var dynamicform = formView.down('dynamicform');

                    if(selectCount == 0){
                        XD.msg('请至少选择一条需要查看的数据');
                        return;
                    }
                    var entryids = [];
                    var nodeids = [];
                    var msgids = [];
                    for(var i=0;i<record.length;i++){
                        entryids.push(record[i].get('entryid'));
                        nodeids.push(record[i].get('nodeid'));
                        msgids.push(record[i].get('id'));
                    }
                    var nodeid = nodeids[0];
                    var initFormFieldState = this.initFormField(dynamicform, 'hide',nodeid);
                    if(!initFormFieldState){//表单控件加载失败
                        return;
                    }
                    dynamicform.operate = 'look';
                    dynamicform.entryids = entryids;
                    dynamicform.entryid = entryids[0];
                    dynamicform.nodeids = nodeids;
                    dynamicform.msgids = msgids;
                    dynamicform.msgid = msgids[0];
                    this.initFormData('look',dynamicform, entryids[0],'hsMsgids');
                    formView.show();
                }
            },

            'restitutionYghView button[itemId=lookEntryId]':{//已归还-查看
                click:function (btn) {
                    var restitutionYghView = btn.up('restitutionYghView');
                    // 获取到当前表单中的已选择数据
                    var record = restitutionYghView.getSelectionModel().getSelection();
                    var selectCount = record.length;

                    // 显示条目的表单信息
                    var formView = Ext.create('Restitution.view.FormView');
                    var dynamicform = formView.down('dynamicform');

                    if(selectCount == 0){
                        XD.msg('请至少选择一条需要查看的数据');
                        return;
                    }
                    var entryids = [];
                    var nodeids = [];
                    var msgids = [];
                    for(var i=0;i<record.length;i++){
                        entryids.push(record[i].get('entryid'));
                        nodeids.push(record[i].get('nodeid'));
                        msgids.push(record[i].get('id'));
                    }
                    var nodeid = nodeids[0];
                    var initFormFieldState = this.initFormField(dynamicform, 'hide',nodeid);
                    if(!initFormFieldState){//表单控件加载失败
                        return;
                    }
                    dynamicform.operate = 'look';
                    dynamicform.entryids = entryids;
                    dynamicform.entryid = entryids[0];
                    dynamicform.nodeids = nodeids;
                    dynamicform.msgids = msgids;
                    dynamicform.msgid = msgids[0];
                    this.initFormData('look',dynamicform, entryids[0],'hsMsgids');
                    formView.show();
                }
            },

            'restitutionYzcView button[itemId=lookEntryId]':{//已转出-查看
                click:function (btn) {
                    var restitutionYzcView = btn.up('restitutionYzcView');
                    // 获取到当前表单中的已选择数据
                    var record = restitutionYzcView.getSelectionModel().getSelection();
                    var selectCount = record.length;

                    // 显示条目的表单信息
                    var formView = Ext.create('Restitution.view.FormView');
                    var dynamicform = formView.down('dynamicform');

                    if(selectCount == 0){
                        XD.msg('请至少选择一条需要查看的数据');
                        return;
                    }
                    var entryids = [];
                    var nodeids = [];
                    var msgids = [];
                    for(var i=0;i<record.length;i++){
                        entryids.push(record[i].get('entryid'));
                        nodeids.push(record[i].get('nodeid'));
                        msgids.push(record[i].get('id'));
                    }
                    var nodeid = nodeids[0];
                    var initFormFieldState = this.initFormField(dynamicform, 'hide',nodeid);
                    if(!initFormFieldState){//表单控件加载失败
                        return;
                    }
                    dynamicform.operate = 'look';
                    dynamicform.entryids = entryids;
                    dynamicform.entryid = entryids[0];
                    dynamicform.nodeids = nodeids;
                    dynamicform.msgids = msgids;
                    dynamicform.msgid = msgids[0];
                    this.initFormData('look',dynamicform, entryids[0],'hsMsgids');
                    formView.show();
                }
            }
        });
    },

    //条目切换，上一条下一条
    preNextHandler:function(form,type){
            this.refreshFormData(form, type);
    },

    refreshFormData:function(form, type){
        var entryids = form.entryids;
        var nodeids = form.nodeids;
        var msgids = form.msgids;
        var currentEntryid = form.entryid;
        var entryid;
        var nodeid;
        var msgid;
        var currentMsgid = form.msgid;
        if(msgids){
            for(var i=0;i<msgids.length;i++){
                if(type == 'pre' && msgids[i] == currentMsgid){
                    if(i==0){
                        i = msgids.length;
                    }
                    entryid = entryids[i-1];
                    nodeid = nodeids[i-1];
                    if(msgids){
                        msgid = msgids[i-1];
                    }
                    break;
                }else if(type == 'next' && msgids[i] == currentMsgid){
                    if(i==msgids.length-1){
                        i=-1;
                    }
                    entryid = entryids[i+1];
                    nodeid = nodeids[i+1];
                    if(msgids){
                        msgid = msgids[i+1];
                    }
                    break;
                }
            }
        }else{
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
        }
        form.entryid = entryid;
        form.msgid = msgid;
        var state = 'noMsgids';
        if(msgids){
            state = 'hsMsgids';
        }
        if(form.operate != 'undefined'){
            this.initFormField(form, 'hide', nodeid);//上下条时切换模板
            this.initFormData(form.operate, form, entryid,state);
            return;
        }
        this.initFormField(form, 'hide', nodeid);
        this.initFormData('look', form, entryid,state);
    },

    initFormData:function(operate, form, entryid, state){
        var nullvalue = new Ext.data.Model();
        var restitutionFormView = form.up('restitutionFormView');
        var fields = form.getForm().getFields().items;
        var count = 0;
        if(operate == 'look') {
            if(state=='hsMsgids'){
                for (var i = 0; i < form.msgids.length; i++) {
                    if (form.msgids[i] == form.msgid) {
                        count = i + 1;
                        break;
                    }
                }
            }else{
                for (var i = 0; i < form.entryids.length; i++) {
                    if (form.entryids[i] == entryid) {
                        count = i + 1;
                        break;
                    }
                }
            }
            var total = form.entryids.length;
            var totaltext = form.down('[itemId=totalText]');
            totaltext.setText('当前共有  ' + total + '  条，');
            var nowtext = form.down('[itemId=nowText]');
            nowtext.setText('当前记录是第  ' + count + '  条');
        }
        for(var i = 0; i < fields.length; i++){
            if(fields[i].value&&typeof(fields[i].value)=='string'&&fields[i].value.indexOf('label')>-1){
                continue;
            }
            if(fields[i].xtype == 'combobox'){
                fields[i].originalValue = null;
            }
            nullvalue.set(fields[i].name, null);
        }
        form.loadRecord(nullvalue);
        var etips = form.up('restitutionFormView').down('[itemId=etips]');
        etips.show();
        if(operate!='look'&&operate!='lookfile'){
            var settingState = this.ifSettingCorrect(form.nodeid,form.templates);
            if(!settingState){
                return;
            }
            Ext.each(fields,function (item) {
                if(!item.freadOnly){
                    item.setReadOnly(false);
                }
            });
        }else{
            Ext.each(fields,function (item) {
                item.setReadOnly(true);
            });
        }

        var eleview = (form).up('restitutionFormView').down('electronic');
        var solidview = (form).up('restitutionFormView').down('solid');

            Ext.Ajax.request({
                method: 'GET',
                scope: this,
                url: '/management/entries/' + entryid,
                success: function (response) {
                    var entry = Ext.decode(response.responseText);

                    var data = Ext.decode(response.responseText);
                    if (data.organ) {
                        entry.organ = data.organ;//机构
                    }

                    form.loadRecord({getData: function () {return entry;}});

                    var fieldCode = form.getRangeDateForCode();//字段编号，用于特殊的自定义字段(范围型日期)
                    if (fieldCode != null) {
                        //动态解析数据库日期范围数据并加载至两个datefield中
                        form.initDaterangeContent(entry);
                    }
                    eleview.initData(entry.entryid);
                    solidview.initData(entry.entryid);
                }
            });

        form.fileLabelStateChange(eleview,operate);
        form.fileLabelStateChange(solidview,operate);

        var elebtns = eleview.down('toolbar').query('button');
        for(var i=0;i<elebtns.length;i++){
            var btnText = elebtns[i].text;
            //查看移交条目需要隐藏所有按钮
            var btn=  elebtns[i];
            if (btnText == '下载' || btnText == '全部下载' || btnText == '打印') {
                btn.show();
            }
            // from.getELetopBtn(btn);
        }
        var soildbtns = solidview.down('toolbar').query('button');
        for(var i=0;i<soildbtns.length;i++){
            var btnText = soildbtns[i].text;
            //查看移交条目需要隐藏所有按钮
            var btn=  soildbtns[i];
            if (btnText == '下载' || btnText == '全部下载' || btnText == '打印') {
                btn.show();
            }
            // from.getELetopBtn(btn);
        }
    },

    initFormField:function(form, operate, nodeid){
        form.nodeid = nodeid;//用左侧树节点的id初始化form的nodeid参数
        form.removeAll();//移除form中的所有表单控件
        var field = {
            xtype: 'hidden',
            name: 'entryid'
        };
        form.add(field);
        var formField = form.getFormField();//根据节点id查询表单字段
        if(formField.length==0){
            XD.msg('请检查档号设置信息是否正确');
            return;
        }
        form.templates = formField;
        form.initField(formField,operate);//重新动态添加表单控件
        return '加载表单控件成功';
    },

    //获取表单字段
    getFormField:function (nodeid) {
        var formField;
        Ext.Ajax.request({
            url: '/template/form',
            async:false,
            params:{
                nodeid:nodeid
            },
            success: function (response) {
                formField = Ext.decode(response.responseText);
                console.log(formField);
            }
        });
        return formField;
    },

    //弹出导入界面
    importWin:function(isFormSubmit){
        var importWin = Ext.create('Ext.window.Window', {
            width: '100%',
            height: '100%',
            header: false,
            modal: true,
            closeToolText: '关闭',
            layout: 'fit',
            xtype: 'categoryWin',
            borrowcode:isFormSubmit,
            items: [{
                xtype: 'restitutionSearchView'
            }]
        });

        importWin.show();
        return importWin;
    },

    //弹出导入界面
    UpdateImportWin:function(isFormSubmit){
        var importWin = Ext.create('Ext.window.Window', {
            width: '100%',
            height: '100%',
            header: false,
            modal: true,
            closeToolText: '关闭',
            layout: 'fit',
            xtype: 'categoryWin',
            borrowcode:isFormSubmit,
            items: [{
                xtype: 'restitutionUpdateSearchView'
            }]
        });
        var dview = importWin.down('restitutionUpdateView')
        var girModel=dview.getSelectionModel()
        girModel.clearSelections()//---清除修改页面---勾选条目后点击添加条目
        importWin.show();
        return importWin;
    },
    //检索表单信息
    searchInfo:function (searchfield) {
        //获取检索框的值
        var simpleSearchSearchView = searchfield.findParentByType('panel');
        var condition = simpleSearchSearchView.down('[itemId=simpleSearchSearchComboId]').getValue(); //字段
        var operator = 'like';//操作符
        var content = searchfield.getValue(); //内容
        var isCollection;
        var sGrid = searchfield.findParentByType('restitutionSearchView').down('restitutionGridView');
        if (sGrid.title == '当前位置：个人收藏') {//如果是收藏界面
            isCollection = '收藏';
        }
        //检索数据
        //如果有勾选在结果中检索，则添加检索条件，如果没有勾选，则重置检索条件
        var grid = simpleSearchSearchView.findParentByType('panel').down('restitutionGridView');
        var gridstore = grid.getStore();
        //加载列表数据
        var searchcondition = condition;
        var searchoperator = operator;
        var searchcontent = content;
        //如果选择在结果中查询,将两种条件用','号隔开,参数一起传递到后台
        var inresult = simpleSearchSearchView.down('[itemId=inresult]').getValue();
        if(inresult){
            var params = gridstore.getProxy().extraParams;
            if(typeof(params.condition) != 'undefined'){
                searchcondition = [params.condition,condition].join(XD.splitChar);
                searchoperator = [params.operator,operator].join(XD.splitChar);
                searchcontent = [params.content,content].join(XD.splitChar);
            }
        }
        grid.dataParams={
            isCollection: isCollection,
            condition: searchcondition,
            operator: searchoperator,
            content: searchcontent
        };

        //检索数据前,修改column的renderer，将检索的内容进行标红
        Ext.Array.each(grid.getColumns(), function(){
            var column = this;
            if(!inresult && column.xtype == 'gridcolumn'){
                column.renderer = function(value){
                    return value;
                }
            }
            if(column.dataIndex == condition){
                var searchstrs = [];
                var conditions = searchcondition.split(XD.splitChar);
                var contents = searchcontent.split(XD.splitChar);
                for(var i =0;i<conditions.length;i++){
                    if(conditions[i] == condition){
                        searchstrs.push(contents[i]);
                    }
                }
                column.renderer = function(value){
                    var contentData = searchstrs.join('|').split(' ');//切割以空格分隔的多个关键词
                    var reg = new RegExp(contentData.join('|'),'g');
                    return value.replace(reg,function (match) {
                        return '<span style="color:red">'+match+'</span>';
                    });
                }
            }
        });
        if(grid.bookmarkStatus==false){
            var simpleSearchStore = Ext.create('Restitution.store.RestitutionGridStore');
            grid.reconfigure(simpleSearchStore);
        }
        grid.initGrid();
        grid.parentXtype = 'simpleSearchView';
        grid.formXtype = 'EntryFormView';
    },

    //修改--检索表单信息
    UpdateSearchInfo:function (searchfield) {
        //获取检索框的值
        var simpleSearchSearchView = searchfield.findParentByType('panel');
        var condition = simpleSearchSearchView.down('[itemId=simpleSearchSearchComboId]').getValue(); //字段
        var operator = 'like';//操作符
        var content = searchfield.getValue(); //内容
        var isCollection;
        var sGrid = searchfield.findParentByType('restitutionUpdateSearchView').down('restitutionUpdateView');
        if (sGrid.title == '当前位置：个人收藏') {//如果是收藏界面
            isCollection = '收藏';
        }
        //检索数据
        //如果有勾选在结果中检索，则添加检索条件，如果没有勾选，则重置检索条件
        var grid = simpleSearchSearchView.findParentByType('panel').down('restitutionUpdateView');
        var gridstore = grid.getStore();
        //加载列表数据
        var searchcondition = condition;
        var searchoperator = operator;
        var searchcontent = content;
        //如果选择在结果中查询,将两种条件用','号隔开,参数一起传递到后台
        var inresult = simpleSearchSearchView.down('[itemId=inresult]').getValue();
        if(inresult){
            var params = gridstore.getProxy().extraParams;
            if(typeof(params.condition) != 'undefined'){
                searchcondition = [params.condition,condition].join(XD.splitChar);
                searchoperator = [params.operator,operator].join(XD.splitChar);
                searchcontent = [params.content,content].join(XD.splitChar);
            }
        }
        grid.dataParams={
            isCollection: isCollection,
            condition: searchcondition,
            operator: searchoperator,
            content: searchcontent
        };

        //检索数据前,修改column的renderer，将检索的内容进行标红
        Ext.Array.each(grid.getColumns(), function(){
            var column = this;
            if(!inresult && column.xtype == 'gridcolumn'){
                column.renderer = function(value){
                    return value;
                }
            }
            if(column.dataIndex == condition){
                var searchstrs = [];
                var conditions = searchcondition.split(XD.splitChar);
                var contents = searchcontent.split(XD.splitChar);
                for(var i =0;i<conditions.length;i++){
                    if(conditions[i] == condition){
                        searchstrs.push(contents[i]);
                    }
                }
                column.renderer = function(value){
                    var contentData = searchstrs.join('|').split(' ');//切割以空格分隔的多个关键词
                    var reg = new RegExp(contentData.join('|'),'g');
                    return value.replace(reg,function (match) {
                        return '<span style="color:red">'+match+'</span>';
                    });
                }
            }
        });
        if(grid.bookmarkStatus==false){
            var simpleSearchStore = Ext.create('Restitution.store.RestitutionGridStore');
            grid.reconfigure(simpleSearchStore);
        }
        grid.initGrid();
        grid.parentXtype = 'simpleSearchView';
        grid.formXtype = 'EntryFormView';
    },
});
function getNowFormatDate() {
    var date = new Date();
    var seperator1 = "";
    var seperator2 = ":";
    var month = date.getMonth() + 1;
    var strDate = date.getDate();
    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
    }
    var currentdate = date.getFullYear() + '年' + month + '月' + strDate + '日'
    // + " " + date.getHours() + seperator2 + date.getMinutes();
    // + seperator2 + date.getSeconds();
    return currentdate;
}

function getDateStr(AddDayCount) {
    var dd = new Date();
    dd.setDate(dd.getDate()+AddDayCount);//获取AddDayCount天后的日期
    var y = dd.getFullYear();
    var m = dd.getMonth()+1;//获取当前月份的日期
    var d = dd.getDate();
    if (m >= 1 && m <= 9) {
        m = "0" + m;
    }
    if (d >= 0 && d <= 9) {
        d = "0" + d;
    }
    return y+""+m+""+d;
}