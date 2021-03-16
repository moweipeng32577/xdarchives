/**
 * Created by yl on 2017/10/25.
 */
var exportState = "";
var entryids = "";
var NodeIdf = "";
var appraisalfieldcod="";
Ext.define('Appraisal.controller.AppraisalController', {
    extend: 'Ext.app.Controller',

    views: [
        'AppraisalView', 'AppraisalGridView',
        'AppraisalWindow', 'AppraisalDestroyView',
        'AppraisalBillView','AppraisalShowBillGridView',
        'AppraisalBillEntryGridView','DealDetailsGridView','AppraisalReportGridView',
        'AppraisalGroupSetView','AppraisalMessageView'
    ],//加载view
    stores: [
        'AppraisalGridStore', 'AppraisalTreeStore',
        'ApproveManStore','BillGridStore',
        'BillEntryGridStore','DealDetailsGridStore','ReportGridStore',
        'AppraisalGroupSetStore','ApproveOrganStore','ApproveNodeStore'
    ],//加载store
    models: [
        'AppraisalGridModel', 'AppraisalTreeModel','ReportGridModel',
        'BillGridModel','BillEntryGridModel','DealDetailsGridModel',
        'AppraisalGroupSetModel'
    ],//加载model
    init: function () {
        var treeFnid;
        var count = 0;
        this.control({
            'appraisalView [itemId=treepanelId]': {
                select: function (treemodel, record) {
                    var gridcard = this.findView(treemodel.view).down('[itemId=gridcard]');
                    var onlygrid = gridcard.down('[itemId=onlygrid]');
                    var pairgrid = gridcard.down('[itemId=pairgrid]');
                    var grid;
                    var nodeType = record.data.nodeType;
                    var bgSelectOrgan = gridcard.down('[itemId=bgSelectOrgan]');
                    if (nodeType == 2) {
                        gridcard.setActiveItem(bgSelectOrgan);
                    } else {
                        //if (Ext.String.endsWith(record.data.text, '卷', true)) {
                        if (record.data.classlevel == 2) {
                            gridcard.setActiveItem(pairgrid);
                            var ajgrid = pairgrid.down('[itemId=northgrid]');
                            ajgrid.setTitle("当前位置：" + record.data.text);
                            var jngrid = pairgrid.down('[itemId=southgrid]');
                            jngrid.setTitle("卷内文件");
                            jngrid.collapse();
                            grid = ajgrid;
                        } else {
                            gridcard.setActiveItem(onlygrid);
                            onlygrid.setTitle("当前位置：" + record.data.text);

                            grid = onlygrid;
                        }
                        treeFnid = record.data.fnid;
                        grid.nodeid = record.data.fnid;//给列表的nodeid参数（模板表查询）重新赋值，刷新列的内容
                        //---
                        NodeIdf = record.get('fnid');
                        var demoStore = Ext.getStore('AppraisalGroupSetStore');
                        demoStore.proxy.extraParams.fieldNodeid = NodeIdf;
                        //--
                        grid.initGrid({nodeid: record.data.fnid});
                        window.grid = grid;
                        grid.parentXtype = 'appraisalView';
                        grid.formXtype = 'EntryFormView';
                    }
                }
            },
            'appraisalView [itemId=northgrid]': {
                itemclick: this.itemclickHandler
            },
            'appraisalView [itemId=southgrid]': {
                eleview: this.activeEleForm
            },
            'appraisalView [itemId=ilook]': {
                click: this.ilookHandler
            },

            'appraisalGridView ': {
                eleview: this.activeEleForm
            },
            'appraisalGridView [itemId=look]': {//查看
                click: this.lookHandler
            },
            'appraisalGridView [itemId=submitAppraisalID]': {//新增销毁单据
                click: this.destroyHandler
            },'appraisalGroupSetView button[itemId="addAllOrNotAll"]': {
                    click:function(view){
                        var itemSelector = view.findParentByType('appraisalGroupSetView').down('itemselector');
                        if(view.getText()=='全选'){
                            var fromList = itemSelector.fromField.boundList,
                                allRec = fromList.getStore().getRange();
                            fromList.getStore().remove(allRec);
                            itemSelector.toField.boundList.getStore().add(allRec);
                            itemSelector.syncValue();//
                            view.setText('取消全选');
                        }else{
                            var toList = itemSelector.toField.boundList,
                                allRec = toList.getStore().getRange();
                            toList.getStore().remove(allRec);
                            itemSelector.fromField.boundList.getStore().add(allRec);
                            itemSelector.syncValue();
                            view.setText('全选');
                        }

                    }
                },
            'appraisalGridView [itemId=Excel]': {//导出--st
                click: this.chooseFieldExportExcel
            },
            'appraisalGridView [itemId=Xml]': {//导出
                click: this.chooseFieldExportXml
            },
            'appraisalGridView [itemId=ExcleAndElectronic]': {//导出
                click: this.chooseFieldExportExcelAndFile
            },
            'appraisalGridView [itemId=XmlAndElectronic]': {//导出--en
                click: this.chooseFieldExportXmlAndFile
            },
            'AppraisalMessage button[itemId="SaveExport"]': {//导出
                click: function (view) {
                    var AppraisalMessageView = view.up('AppraisalMessage');
                    var fileName = AppraisalMessageView.down('[itemId=userFileName]').getValue();
                    var zipPassword = AppraisalMessageView.down('[itemId=zipPassword]').getValue();
                    var b = AppraisalMessageView.down('[itemId=addZipKey]').checked
                    var form = AppraisalMessageView.down('[itemId=form]')

                    if (fileName!=null&&fileName!="请输入..."&&fileName!="") {
                        var pattern = new RegExp("[/:*?\"<>|]");
                        if (pattern.test(fileName) || fileName.indexOf('\\') > -1) {
                            XD.msg("文件名称不能包含下列任何字符：\\/:*?\"<>|");
                            return;
                        }
                        if(zipPassword==""&&b){
                            XD.msg("zip压缩密码不能为空");
                            return;
                        }

                        Ext.MessageBox.wait('正在处理请稍后...')
                        Ext.Ajax.request({
                            method: 'post',
                            url:'/export/chooseFieldExport',
                            timeout:XD.timeout,
                            scope: this,
                            async:true,
                            params:{
                                nodeid: NodeIdf,
                                fileName: fileName,
                                exportState: exportState,
                                zipPassword: zipPassword,
                                entryids: entryids,
                                userFieldCode: appraisalfieldcod
                            },
                            success:function(res){
                                var obj = Ext.decode(res.responseText).data;
                                if(obj.fileSizeMsg=="NO"){
                                    XD.msg('原文总大小超出限制');
                                    Ext.MessageBox.hide()
                                    return;
                                }
                                if(obj.entrySizeMsg=="NO"){
                                    if(exportState=="XmlAndFile"||exportState=="ExcelAndFile"){
                                        XD.msg('条目数超出限制，一次支持支导出10万含原文的条目！');
                                    }
                                    if(exportState=="Excel"||exportState=="Xml"){
                                        XD.msg('条目数超出限制，一次只支持导出50w的条目！');
                                    }
                                    Ext.MessageBox.hide()
                                    return;
                                }
                                window.location.href="/export/downloadZipFile?fpath="+encodeURIComponent(obj.filePath)
                                Ext.MessageBox.hide()
                                XD.msg('文件生成成功，正在准备下载');
                                AppraisalMessageView.close()
                            },
                            failure:function(){
                                Ext.MessageBox.hide()
                                XD.msg('文件生成失败');
                            }
                        });
                    } else {
                        XD.msg("文件名不能为空")
                    }
                }
            },
            'AppraisalMessage button[itemId="cancelExport"]': {
                click: function (view) {
                    view.findParentByType('AppraisalMessage').close();
                }
            },
            'appraisalGridView [itemId=appraisalBtnID]': {//鉴定
                click: this.appraisalHandler
            },
            'appraisalGridView [itemId=printAppraisalID]': {//打印
                click: this.printHandler
            },
            'appraisalGridView [itemId=showBillID]': {//查看销毁单据
                click: this.showBillHandler
            },
            'appraisalGridView [itemId=updateTree]': {//更新节点树
                click: function (btn) {
                    XD.confirm('确定更新树节点吗?', function () {
                        Ext.MessageBox.wait('正在更新树节点...', '提示');
                        Ext.Ajax.request({
                            url: '/appraisal/updateAppraisal',
                            method: 'POST',
                            timout:XD.timeout,
                            success: function (response) {
                                Ext.Msg.hide();
                                var respText = Ext.decode(response.responseText);
                                if (respText.success == true) {
                                    XD.msg(respText.msg);
                                }
                            },
                            failure: function () {
                                Ext.Msg.hide();
                                XD.msg('操作失败');
                            }
                        });
                    })
                }
            },
            'appraisalShowBillGridView button[itemId=xhDealDetailsId]': {//办理详情
                click: this.xhDealDetailsHandler
            },

            'appraisalShowBillGridView [itemId=showEntryDetail]':{//查看销毁单据列表 查看详细条目
                click:this.showEntryDetailHandler
            },
            'appraisalShowBillGridView [itemId=print]':{//查看销毁单据列表 打印
                click:this.printBillHandler
            },
            'appraisalShowBillGridView [itemId=back]':{//查看销毁单据列表 返回
                click:function (btn) {
                    btn.up('window').hide();
                }
            },
            'appraisalBillEntryGridView [itemId=back]':{//查看销毁单据条目详情列表 返回
                click:function (btn) {
                    this.activeBillGrid(btn);
                }
            },
            'EntryFormView [itemId=preBtn]':{
                click:this.preHandler
            },
            'EntryFormView [itemId=nextBtn]':{
                click:this.nextHandler
            },
            'EntryFormView [itemId=back]': {
                click: function (btn) {
                    this.activeGrid(btn, true);
                }
            },
            'appraisalDestroyView button[itemId=approval]': {
                click: function (view, e, eOpts) {
                    var appraisalDestroyView = this.findDestroyView(view);
                    var firstCardForm=this.findFirstCard(view);
                    firstCardForm.getForm().findField('nodeid').setValue(treeFnid);
                    if (firstCardForm.isValid()) {
                        Ext.MessageBox.wait('正在提交数据请稍后...','提示');
                        firstCardForm.submit({
                            clientValidation: true,
                            url: '/destructionBill/checkBill',
                            method: 'POST',
                            scope:this,
                            params: {
                                entryids: window.entryids
                            },
                            success: function (form, action) {
                                var respText = Ext.decode(action.response.responseText);
                                if (respText.success == true) {
                                    Ext.Msg.hide();
                                    appraisalDestroyView.setTitle('送审');
                                    var secondCard = this.findSecondCard(view);
                                    this.hideSecondCardBtn(appraisalDestroyView);
                                    secondCard.down("[itemId=nextnode]").getStore().load();
                                    var spmanOrgan = secondCard.down("[itemId=approveOrgan]");
                                    spmanOrgan.getStore().proxy.extraParams.type = "submit"; //申请时获取审批单位
                                    spmanOrgan.getStore().proxy.extraParams.taskid = null;
                                    spmanOrgan.getStore().proxy.extraParams.nodeid = respText.data.id;
                                    spmanOrgan.getStore().proxy.extraParams.worktext = null;
                                    spmanOrgan.getStore().reload(); //刷新审批单位
                                    appraisalDestroyView.down('panel').setActiveItem(secondCard);
                                } else {
                                    Ext.Msg.hide();
                                    XD.msg(respText.msg);
                                }
                            },
                            failure: function (form, action) {
                                var respText = Ext.decode(action.response.responseText);
                                Ext.Msg.hide();
                                XD.msg(respText.msg);
                            }
                        });
                    }

                }
            },
            'appraisalGroupSetView button[itemId="close"]': {
                click: function (view) {
                    view.findParentByType('appraisalGroupSetView').close();
                }
            },
            'appraisalGroupSetView button[itemId="save"]': {
                click: this.chooseSave
            },
            'appraisalDestroyView button[itemId=back]': {
                click: function (view) {
                    var appraisalDestroyView = this.findDestroyView(view);
                    appraisalDestroyView.setTitle('新增销毁单据');
                    var firstCard = this.findFirstCard(view);
                    this.hideFirstCardBtn(appraisalDestroyView);
                    appraisalDestroyView.down('panel').setActiveItem(firstCard);
                }
            },
            'appraisalDestroyView button[itemId=close]': {
                click: function (view) {
                    var window = view.up('appraisalDestroyView');
                    window.close();
                }
            },
            'appraisalDestroyView button[itemId=confirm]': {
                click: function (view, e, eOpts) {
                    var firstCardForm = this.findFirstCard(view);
                    var secondCardForm = this.findSecondCard(view);
                    firstCardForm.getForm().findField('nodeid').setValue(treeFnid);
                    var appraisalDestroyView = firstCardForm.up('appraisalDestroyView');
                    if(secondCardForm.getComponent('spmanId').getRawValue()==''){
                        XD.msg('审批人不能为空');
                        return;
                    }
                    if (firstCardForm.isValid()) {
                        Ext.MessageBox.wait('正在送审请稍后...','提示');
                        firstCardForm.submit({
                            clientValidation: true,
                            url: '/destructionBill/approvalBill',
                            method: 'POST',
                            params: {
                                entryids: window.entryids,
                                userid: secondCardForm.getComponent('spmanId').getValue(),
                                username: secondCardForm.getComponent('spmanId').getRawValue(),
                                text: secondCardForm.getComponent('nextnode').getValue(),
                                sendMsg:secondCardForm.down('[itemId=sendmsgId]').getValue()
                            },
                            success: function (form, action) {
                                Ext.MessageBox.hide();
                                var respText = Ext.decode(action.response.responseText);
                                if (respText.success == true) {
                                    Ext.MessageBox.alert("提示", respText.msg, callBack);
                                    function callBack() {
                                        appraisalDestroyView.close();
                                    }
                                } else {
                                    XD.msg(respText.msg);
                                }
                            },
                            failure: function (form, action) {
                                Ext.MessageBox.hide();
                                var respText = Ext.decode(action.response.responseText);
                                XD.msg(respText.msg);
                            }
                        });
                    }
                }
            },
            'appraisalDestroyView button[itemId=save]': {
                click: function (view, e, eOpts) {
                    var form = this.findFirstCard(view);
                    form.getForm().findField('nodeid').setValue(treeFnid);
                    var appraisalDestroyView = form.up('appraisalDestroyView');
                    if (form.isValid()) {
                        Ext.MessageBox.wait('正在保存数据请稍后...','提示');
                        form.submit({
                            url: '/destructionBill/saveBill',
                            method: 'POST',
                            params: {
                                entryids: window.entryids
                            },
                            success: function (form, action) {
                                Ext.MessageBox.hide();
                                var respText = Ext.decode(action.response.responseText);
                                if (respText.success == true) {
                                    Ext.MessageBox.alert("提示", respText.msg, callBack);
                                    function callBack() {
                                        appraisalDestroyView.close();
                                    }
                                } else {
                                    XD.msg(respText.msg);
                                }
                            },
                            failure: function (form, action) {
                                Ext.MessageBox.hide();
                                var respText = Ext.decode(action.response.responseText);
                                XD.msg(respText.msg);
                            }
                        });
                    }
                }
            },
            // 'appraisalDestroyView': {
            //     afterRender: function (view) {
            //         view.down('form').getForm().findField('total').setValue(window.entryids.length);
            //     }
            // },
            'appraisalWindow button[itemId=appraisalWinSaveBtnID]': {
                click: function (view) {
                    var appraisalWindow = view.up('appraisalWindow');
                    var approvaldate = appraisalWindow.down('[itemId=approvaldateID]');

                    if (approvaldate.getValue() == null) {
                        XD.msg('请选择保管期限');
                    } else {
                        Ext.Ajax.request({
                            params: {
                                entryid: window.entryids[0],
                                approvaldate: approvaldate.getRawValue()
                            },
                            url: '/appraisal/againAppraisal',
                            method: 'POST',
                            sync: true,
                            success: function (response) {
                                var respText = Ext.decode(response.responseText);
                                if (respText.success == true) {
                                    XD.msg(respText.msg);
                                    appraisalWindow.close();
                                    window.grid.getStore().loadPage(window.grid.getStore().currentPage);
                                } else {
                                    XD.msg(respText.msg);
                                }
                            },
                            failure: function () {
                                XD.msg('操作失败');
                            }
                        });
                    }
                }
            },
            'appraisalWindow': {
                beforerender: function (view) {
                    view.getComponent('filedate').setValue(window.grid.selModel.getSelection()[0].get('filedate'));
                    view.getComponent('yretention').setValue(window.grid.selModel.getSelection()[0].get('entryretention'));
                    var combobox = view.down('combobox');
                    combobox.getStore().proxy.extraParams.value = window.fenums;
                    combobox.getStore().reload();
                }
            },
            'appraisalWindow button[itemId=appraisalWinCloseBtnID]': {
                click: function (view) {
                    var window = view.up('appraisalWindow');
                    window.close();
                }
            },
            'appraisalWindow combobox[itemId=approvaldateID]': {
                render: function (combo) {
                    var record = window.grid.getSelectionModel().getLastSelected();
                    combo.setValue(record.data.entryretention);
                }
            },
            'appraisalReportGridView [itemId=print]':{//打印报表
                click:function (btn) {
                    var reportGrid = btn.findParentByType('appraisalReportGridView');
                    var records = reportGrid.getSelectionModel().getSelection();
                    if(records.length==0){
                        XD.msg('请选择需要打印的报表');
                        return;
                    }
                    var record = records[0];
                    var filename = record.get('filename');
                    if(!filename){
                        XD.msg('无报表样式文件，请在报表管理中上传');
                        return;
                    }
                    if(reportServer == 'UReport'){
                        Ext.Ajax.request({
                            method: 'GET',
                            url: '/report/ifFileExist/' + record.get('reportid'),
                            scope: this,
                            async: false,
                            success: function (response) {
                                var responseText = Ext.decode(response.responseText);
                                if (responseText.success == true) {
                                    var params = {};
                                    params['entryid'] = reportGrid.entryids.join(",");
                                    XD.UReportPrint(null, filename, params);
                                } else {
                                    XD.msg('打印失败！' + responseText.msg);
                                    return;
                                }
                            }
                        });
                    }
                    else if(reportServer == 'FReport') {
                        Ext.Ajax.request({
                            method: 'GET',
                            url: '/report/ifFileExist/' + record.get('reportid'),
                            scope: this,
                            async: false,
                            success: function (response) {
                                var responseText = Ext.decode(response.responseText);
                                if (responseText.success == true) {
                                    //在ajax里面异步打开新标签页，会被拦截(谷歌会拦截，360不会)
                                    var win = null;
                                    XD.FRprint(win, filename, reportGrid.entryids.length > 0 ? "'entryid':'" + reportGrid.entryids.join(",") + "','nodeid':'" + reportGrid.nodeid + "'" : '');
                                } else {
                                    XD.msg('打印失败！' + responseText.msg);
                                    return;
                                }
                            }
                        });
                    }
                }
            },
            'appraisalReportGridView [itemId=showAllReport]':{//显示所有报表
                click:function (btn) {
                    var reportGrid = btn.findParentByType('appraisalReportGridView');
                    if(reportGrid.down('[itemId=showAllReport]').text=='显示所有报表'){
                        reportGrid.down('[itemId=showAllReport]').setText('显示当前报表');
                        reportGrid.initGrid({nodeid:reportGrid.nodeid,flag:'all'});
                    }else if(reportGrid.down('[itemId=showAllReport]').text=='显示当前报表'){
                        reportGrid.down('[itemId=showAllReport]').setText('显示所有报表');
                        reportGrid.initGrid({nodeid:reportGrid.nodeid + ',公有报表'});
                    }
                }
            },
            'appraisalReportGridView [itemId=back]':{//报表列表返回至数据列表
                click:function (btn) {
                    btn.up('window').hide();
                }
            }
        });
    },
    //获取到期鉴定管理应用视图
    findView: function (btn) {
        return btn.findParentByType('appraisalView');
    },

    //获取表单界面视图
    findFormView: function (btn) {
        return this.findView(btn).down('EntryFormView');
    },

    //获取列表界面视图
    findGridView: function (btn) {
        return this.findView(btn).getComponent('gridview');
    },
    findActiveGrid:function(btn){
        var active = this.findView(btn).down('[itemId=gridcard]').getLayout().getActiveItem();
        if(active.getXType() == "appraisalGridView"){
            return active;
        }else if(active.getXType() == "panel"){
            return active.down('[itemId=northgrid]');
        }
    },
    findDestroyView: function (btn) {
        return btn.findParentByType('appraisalDestroyView');
    },
    findFirstCard: function (btn) {
        return this.findDestroyView(btn).down('[itemId=firstCard]');
    },
    findSecondCard: function (btn) {
        return this.findDestroyView(btn).down('[itemId=secondCard]');
    },
    //切换到列表界面视图
    activeGrid: function (btn, flag) {
        var view = this.findView(btn);
        view.setActiveItem(this.findGridView(btn));
        if (document.getElementById('mediaFrame')) {
            document.getElementById('mediaFrame').setAttribute('src', '');
        }
        if (document.getElementById('solidFrame')) {
            document.getElementById('solidFrame').setAttribute('src', '');
        }
        // if (document.getElementById('longFrame')) {
        //     document.getElementById('longFrame').setAttribute('src', '');
        // }
        if (flag) {//根据参数确定是否需要刷新数据
            var grid = this.findActiveGrid(btn);
            grid.notResetInitGrid();
        }
    },

    //切换到表单界面视图
    activeForm: function (btn) {
        var view = this.findView(btn);
        var formview = this.findFormView(btn);
        view.setActiveItem(formview);
        formview.items.get(0).enable();
        formview.setActiveTab(0);
        return formview;
    },
    activeEleForm: function (obj) {
        var view = this.findView(obj.grid);
        var formview = this.findFormView(obj.grid);
        view.setActiveItem(formview);
        formview.items.get(0).disable();
        var eleview = formview.down('electronic');
        var solidview = formview.down('solid');
        eleview.operateFlag = "look"; //电子文件查看标识符
        solidview.operateFlag = "look";//利用文件查看标识符
        eleview.initData(obj.entryid);
        solidview.initData(obj.entryid);
        var from =formview.down('dynamicform');
        //电子文件按钮权限
        var elebtns = eleview.down('toolbar').query('button');
        from.getELetopBtn(elebtns,eleview.operateFlag );
        var soildbtns = solidview.down('toolbar').query('button');
        from.getELetopBtn(soildbtns,solidview.operateFlag);
        formview.setActiveTab(1);
        return formview;
    },

    //获取到期鉴定管理查看销毁单据视图
    findBillView: function (btn) {
        return btn.findParentByType('appraisalBillView');
    },

    //获取查看销毁单据界面的单据列表视图
    findBillGridView:function (btn) {
        return this.findBillView(btn).down('appraisalShowBillGridView');
    },

    //获取查看销毁单据界面的条目详情列表视图
    findBillEntryGridView:function (btn) {
        return this.findBillView(btn).down('appraisalBillEntryGridView');
    },

    //切换到查看销毁单据界面的单据列表视图
    activeBillGrid: function (btn) {
        var view = this.findBillView(btn);
        var billview = this.findBillGridView(btn);
        view.setActiveItem(billview);
        return billview;
    },

    //切换到查看销毁单据界面的条目详情列表视图
    activeBillEntryGrid: function (btn) {
        var view = this.findBillView(btn);
        var billentryview = this.findBillEntryGridView(btn);
        view.setActiveItem(billentryview);
        return billentryview;
    },

    getNodeid:function (parentid) {
        var nodeid;
        var params = {};
        if(typeof(parentid) != 'undefined' && parentid != ''){
            params.parentid = parentid;
        }
        Ext.Ajax.request({
            url:'/publicUtil/getNodeid',
            async:false,
            params:params,
            success:function(response){
                nodeid = Ext.decode(response.responseText).data;
            }
        });
        return nodeid;
    },
    itemclickHandler: function (view, record, item, index, e) {
        var southgrid = this.findInnerGrid(view);
        southgrid.dataUrl = '/management/entries/innerfile/' + record.get('archivecode') + '/';
        southgrid.on('reconfigure', function (grid, store) {
            if(store){
                store.on('beforeload',function(){
                    southgrid.expand();
                });
                store.on('load',function(){
                    if(store.getCount() == 0){
                        southgrid.collapse();
                    }
                })
            }
        });
        southgrid.initGrid({nodeid: this.getNodeid(record.get('nodeid'))});
        southgrid.parentXtype = 'appraisalView';
        southgrid.formXtype = 'EntryFormView';
    },
    findInnerGrid: function (btn) {
        return this.findView(btn).down('[itemId=southgrid]');
    },
    //查看
    lookHandler: function (btn) {
        var record = window.grid.selModel.getSelection();
        var tree = this.findGridView(btn).down('treepanel');
        var node = tree.selModel.getSelected().items[0];
        if (!node) {
            XD.msg('请选择节点');
            return;
        }
        if (record.length == 0) {
            XD.msg('请至少选择一条需要查看的数据');
            return;
        }
        var entryids = [];
        var nodeids = [];
        for(var i=0;i<record.length;i++){
            entryids.push(record[i].get('entryid'));
            nodeids.push(record[i].get('nodeid'));
        }
        var entryid = record[0].get('entryid');
        var form = this.findFormView(btn).down('dynamicform');
        form.operate = 'look';
        form.entryids = entryids;
        form.nodeids = nodeids;
        form.entryid = entryids[0];
        var initFormFieldState = this.initFormField(form, 'hide', node.get('fnid'));
        if (!initFormFieldState) {//表单控件加载失败
            return;
        }
        this.initFormData('look', form, entryid);
    },
    ilookHandler: function (btn) {
        var grid = this.findInnerGrid(btn);
        var records = grid.selModel.getSelection();
        var nodeid = grid.dataParams.nodeid;
        if (records.length != 1) {
            XD.msg('查看只能选中一条数据');
            return;
        }
        var entryid = records[0].get('entryid');
        var form = this.findView(btn).down('dynamicform');
        var initFormFieldState = this.initFormField(form, 'hide', nodeid);
        if (!initFormFieldState) {//表单控件加载失败
            return;
        }
        this.initFormData('look', form, entryid);
    },
    //提交销毁
    destroyHandler: function (btn) {
        var select = window.grid.getSelectionModel();
        if (!select.hasSelection()) {
            XD.msg('请至少选择一条需要提交的数据');
        } else {
            window.entryids = new Array();
            var entrys = select.getSelection();
            for (i = 0; i < entrys.length; i++) {
                window.entryids[i] = entrys[i].get("entryid");
            }
            var win = Ext.create('Appraisal.view.AppraisalDestroyView');
            win.down('form').getForm().findField('total').setValue(window.entryids.length);
            this.hideFirstCardBtn(win);
            win.show();
        }
    },
    //鉴定
    appraisalHandler: function (btn) {
        var select = window.grid.getSelectionModel();
        var tree = this.findGridView(btn).down('treepanel');
        var node = tree.selModel.getSelected().items[0];
        if (!select.hasSelection()) {
            XD.msg('请选择一条需要鉴定的数据');
        } else if (select.getSelection().length != 1) {
            XD.msg('只能选中一条数据');
        } else {
            window.entryids = new Array();
            var entrys = select.getSelection();
            for (i = 0; i < entrys.length; i++) {
                window.entryids[i] = entrys[i].get("entryid");
            }
            Ext.Ajax.request({
                url: '/template/form',
                async: false,
                params: {
                    nodeid: node.data.fnid
                },
                scope: this,
                success: function (response, opts) {
                    var obj = Ext.decode(response.responseText);
                    for (var i = 0; i < obj.length; i++) {
                        switch (obj[i].fieldcode) {
                            case 'entryretention' :
                                window.fenums = obj[i].fenums;
                                break;
                        }
                    }
                    if (window.fenums != null) {
                        var appraisalWindow = Ext.create('Appraisal.view.AppraisalWindow');
                        appraisalWindow.show();
                    }
                }
            });

        }
    },
    //打印报表
    printHandler:function(btn){
        // var grid = this.findActiveGrid(btn);
        // var tree = this.findGridView(btn).down('treepanel');
        // var node = tree.selModel.getSelected().items[0];
        // var ids = [];
        // Ext.each(grid.getSelectionModel().getSelection(),function(){
        //     ids.push(this.get('entryid'));
        // });
        // XD.FRprint(null, 'recordcatalog_index', ids.length > 0 ? "'entryid':'" + ids.join(",") + "','nodeid':'" + node.get('fnid') + "'" : '');
        var grid = this.findActiveGrid(btn);
        var ids = [];
        Ext.each(grid.getSelectionModel().getSelection(),function(){
            ids.push(this.get('entryid'));
        });
        if(ids.length==0){
            XD.msg('请至少选择一条需要打印的数据');
            return;
        }
        var reportGridWin = Ext.create('Ext.window.Window',{
            width:'100%',
            height:'100%',
            header:false,
            draggable : false,//禁止拖动
            resizable : false,//禁止缩放
            modal:true,
            closeToolText:'关闭',
            layout:'fit',
            items:[{
                xtype: 'appraisalReportGridView',
                entryids:ids,
                nodeid:grid.nodeid
            }]
        });
        var reportGrid = reportGridWin.down('appraisalReportGridView');
        reportGrid.initGrid({nodeid:reportGrid.nodeid + ',公有报表'});
        reportGridWin.show();
    },

    showBillHandler:function (btn) {//查看销毁单据
        var grid = this.findActiveGrid(btn);
        var tree = this.findGridView(btn).down('treepanel');
        var node = tree.selModel.getSelected().items[0];
        if(!node){
            XD.msg('请选择节点');
            return;
        }
        var showBillWin = Ext.create('Ext.window.Window',{
            modal:true,
            width:900,
            height:530,
            title:'查看销毁单据',
            layout:'fit',
            closeToolText:'关闭',
            closeAction:'hide',
            items:[{
                xtype: 'appraisalBillView',
                nodeid:grid.nodeid
            }]
        });
        var showBillView = showBillWin.down('appraisalBillView');
        var billGrid = showBillView.down('appraisalShowBillGridView');
        billGrid.getStore().setPageSize(XD.pageSize);
        billGrid.initGrid({nodeid:showBillView.nodeid});
        showBillWin.show();
    },

    xhDealDetailsHandler:function(btn){
        var billGrid = this.findBillGridView(btn);
        var record = billGrid.getSelectionModel().getSelection();
        if(record.length!=1){
            XD.msg('请选择一条单据');
            return;
        }
        var billid = record[0].get('billid');
        this.showDealDetailsWin(billid);
    },

    showEntryDetailHandler:function (btn) {//查看与单据相关联的条目详细内容
        var showBillView = this.findBillView(btn);
        var billGrid = this.findBillGridView(btn);
        var entryGrid = this.findBillEntryGridView(btn);
        var record = billGrid.getSelectionModel().getSelection();
        if(record.length!=1){
            XD.msg('请选择一条需要查看的单据');
            return;
        }
        var billid = record[0].get('billid');
        // var billState = record[0].get('state');
        var params = {
            billid:billid
            // ,billState:billState,
            // nodeid:showBillView.nodeid//请求模板数据，显示列表(此处列表数据不通过模板请求)
        };
        entryGrid.initGrid(params);
        this.activeBillEntryGrid(btn);
    },

    printBillHandler:function (btn) {//打印销毁单据
        var ids = [];
        var params = {};
        var billGrid = this.findBillGridView(btn);
        var record = billGrid.getSelectionModel().getSelection();
        if(record.length<1){
            XD.msg('请选择需要打印的销毁单据');
            return;
        }
        Ext.each(record,function(){
            ids.push(this.get('billid'));
        });
        if(reportServer == 'UReport') {
            params['billid'] = ids.join(",");
            XD.UReportPrint(null, '到期鉴定单据管理', params);
        }
        else if(reportServer == 'FReport') {
            XD.FRprint(null, '到期鉴定单据管理', ids.length > 0 ? "'billid':'" + ids.join(",") + "'" : '')  ;
        }
    },

    ifCodesettingCorrect: function (nodeid) {
        var codesetting = [];
        Ext.Ajax.request({
            url: '/codesetting/getCodeSettingFields',
            async: false,
            params: {
                nodeid: nodeid
            },
            success: function (response) {
                if (Ext.decode(response.responseText).success == true) {
                    codesetting = Ext.decode(response.responseText).data;
                }
            }
        });
        if (codesetting.length == 0) {
            return;
        }
        return '档号设置信息正确';
    },

    ifSettingCorrect: function (nodeid, templates) {
        var hasArchivecode = false;//表单字段是否包含档号（archivecode）
        Ext.each(templates, function (item) {
            if (item.fieldcode == 'archivecode') {
                hasArchivecode = true;
            }
        });
        if (hasArchivecode) {//若表单字段包含档号，则判断档号设置是否正确
            var codesettingState = this.ifCodesettingCorrect(nodeid);
            if (!codesettingState) {
                XD.msg('请检查档号设置信息是否正确');
                return;
            }
        }
        return '档号设置正确';
    },
    initFormField: function (form, operate, nodeid) {
//        if (form.nodeid != nodeid) {//切换节点后，form和tree的节点id不相等
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

    getCurrentAppraisalform:function (btn) {
        return btn.up('EntryFormView');
    },

    //点击上一条
    preHandler:function(btn){
        var currentAppraisalform = this.getCurrentAppraisalform(btn);
        var form = currentAppraisalform.down('dynamicform');
        this.refreshFormData(form, 'pre');
    },

    //点击下一条
    nextHandler:function(btn){
        var currentAppraisalform = this.getCurrentAppraisalform(btn);
        var form = currentAppraisalform.down('dynamicform');
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

    initFormData: function (operate, form, entryid) {
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
            if(fields[i].value&&typeof(fields[i].value)=='string'&&fields[i].value.indexOf('label')>-1){
                continue;
            }
            if(fields[i].xtype == 'combobox'){
                fields[i].originalValue = null;
            }
            nullvalue.set(fields[i].name, null);
        }
        form.loadRecord(nullvalue);
        var etips = formview.down('[itemId=etips]');
        etips.show();
        if (operate != 'look') {
            var settingState = ifSettingCorrect(form.nodeid, form.templates);
            if (!settingState) {
                return;
            }
        }
        this.activeForm(form);
        var eleview = this.findFormView(form).down('electronic');
        var solidview = this.findFormView(form).down('solid');
        // var longview = this.findFormView(form).down('long');
        if (typeof(entryid) != 'undefined') {
            Ext.Ajax.request({
                method: 'GET',
                scope: this,
                url: '/management/entries/' + entryid,
                success: function (response, opts) {
                    var entry = Ext.decode(response.responseText);
                    if (operate == 'add') {
                        delete entry.entryid;
                    }
                    form.loadRecord({
                        getData: function () {
                            return entry;
                        }
                    });
                    //字段编号，用于特殊的自定义字段(范围型日期)
                    var fieldCode = form.getRangeDateForCode();
                    if (fieldCode != null) {
                        //动态解析数据库日期范围数据并加载至两个datefield中
                        form.initDaterangeContent(entry);
                    }
                    //初始化原文数据
                    eleview.initData(entry.entryid);
                    solidview.initData(entry.entryid);
                    // longview.initData(entry.entryid);
                }
            });
        } else {
            eleview.initData();
            solidview.initData();
            // longview.initData();
        }
//        form.formStateChange(operate);
        form.fileLabelStateChange(eleview,operate);
        form.fileLabelStateChange(solidview,operate);
        // form.fileLabelStateChange(longview,operate);
    },
    hideFirstCardBtn: function (view) {
        view.down('[itemId=approval]').setVisible(true);
        view.down('[itemId=save]').setVisible(true);
        view.down('[itemId=confirm]').setVisible(false);
        view.down('[itemId=back]').setVisible(false);
        view.down('[itemId=close]').setVisible(true);
    },
    hideSecondCardBtn: function (view) {
        view.down('[itemId=approval]').setVisible(false);
        view.down('[itemId=save]').setVisible(false);
        view.down('[itemId=confirm]').setVisible(true);
        view.down('[itemId=back]').setVisible(true);
        view.down('[itemId=close]').setVisible(false);
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
    //导出xml
    exportXml:function(btn) {
        var filenames="";
        var isbtn="";
        var tree = this.findGridView(btn).down('treepanel');
        var nodeid = tree.selModel.getSelected().items[0].get('fnid');
        var grid = btn.up('appraisalGridView');
        var ids = [];

        var pattern = new RegExp("[/:*?\"<>|]");
        Ext.each(grid.getSelectionModel().getSelection(), function () {
            ids.push(this.get('entryid'));
        });

        if (ids.length == 0) {
            XD.msg('请至少选择一条需要导出的数据');
            return;
        }

        Ext.MessageBox.prompt("输入导出后的文件名","文件名",function (btn2,text) {
            filenames=text;
            isbtn=btn2
            if(isbtn=="ok"){
                if(filenames){
					if(pattern.test(filenames) || filenames.indexOf('\\') > -1) {
						XD.msg("文件名称不能包含下列任何字符：\\/:*?\"<>|");
						return;
					}
					
                    Ext.Ajax.request({
                        async: false,
                        method: 'POST',
                        params: {
                            ids: ids,
                            nodeId: nodeid,
                            fileName: filenames
                        },
                        url: '/export/exportParameter',
                        success: function () {
                        }
                    });
                    var nw=window.open("/export/exportXml");
                    nw.document.title = '正在努力下载文件中.....';
                }else {
                    XD.msg('文件名不能为空');
                    return;
                }
            }
        });
    },

    //导出excel和原文
    exportExcelAndElectronic:function (btn) {
        var filenames="";
        var isbtn="";
        var tree = this.findGridView(btn).down('treepanel');
        var nodeid = tree.selModel.getSelected().items[0].get('fnid');
        var grid = btn.up('appraisalGridView');
        var ids = [];
        var pattern = new RegExp("[/:*?\"<>|]");
        Ext.each(grid.getSelectionModel().getSelection(), function () {
            ids.push(this.get('entryid'));
        });
        if (ids.length == 0) {
            XD.msg('请至少选择一条需要导出的数据');
            return;
        }
        Ext.MessageBox.prompt("输入导出后的文件名","文件名",function (btn2,text) {
            filenames=text;
            isbtn=btn2
            if(isbtn=="ok"){
                if(filenames){
					if(pattern.test(filenames) || filenames.indexOf('\\') > -1) {
						XD.msg("文件名称不能包含下列任何字符：\\/:*?\"<>|");
						return;
					}
					
                    Ext.Ajax.request({
                        async :  false,//同步
                        method: 'POST',
                        params:{
                            ids:ids,
                            nodeId:nodeid,
                            fileName:filenames
                        },
                        url: '/export/exportParameter',
                        success: function () {
                        }
                    });
                    var nw=window.open("/export/exporteExcelAndElectronic");
                    nw.document.title = '正在努力下载文件中.....';
                }else {
                    XD.msg('文件名不能为空');
                    return;
                }
            }
        });
    },
    //导出Xml和原文
    exportXmlAndElectronic:function (btn) {
        var filenames="";
        var isbtn="";
        var tree = this.findGridView(btn).down('treepanel');
        var nodeid = tree.selModel.getSelected().items[0].get('fnid');
        var grid = btn.up('appraisalGridView');
        var ids = [];

        var pattern = new RegExp("[/:*?\"<>|]");
        Ext.each(grid.getSelectionModel().getSelection(), function () {
            ids.push(this.get('entryid'));
        });

        if (ids.length == 0) {
            XD.msg('请至少选择一条需要导出的数据');
            return;
        }
        //var selectAll = grid.down('[itemId=selectAll]').checked;
        Ext.MessageBox.prompt("输入导出后的文件名","文件名",function (btn2,text) {
            filenames=text;
            isbtn=btn2
            if(isbtn=="ok"){
                if(filenames){
					if(pattern.test(filenames) || filenames.indexOf('\\') > -1) {
						XD.msg("文件名称不能包含下列任何字符：\\/:*?\"<>|");
						return;
					}
					
                    Ext.Msg.wait('正在进行导出，请耐心等候......','正在操作');
                    Ext.Ajax.request({
                        async :  true,
                        method: 'POST',
                        params:{
                            ids:ids,
                            nodeId:nodeid,
                            fileName:filenames
                        },
                        url: '/export/exportParameter',
                        success: function (data) {
                            var nw=window.open('/export/exporteXmlAndElectronic');
                            nw.document.title = '正在努力下载文件中.....';
                            Ext.MessageBox.hide();
                        }
                    });
                }else {
                    XD.msg('文件名不能为空');
                    return;
                }
            }
        });
    },
    //导出excel
    exportExcel:function (btn) {
        var filenames="";
        var isbtn="";
        var tree = this.findGridView(btn).down('treepanel');
        var nodeid = tree.selModel.getSelected().items[0].get('fnid');
        var grid = btn.up('appraisalGridView');
        var ids = [];

        var pattern = new RegExp("[/:*?\"<>|]");
        Ext.each(grid.getSelectionModel().getSelection(), function () {
            ids.push(this.get('entryid'));
        });
        if (ids.length == 0) {
            XD.msg('请至少选择一条需要导出的数据');
            return;
        }
        //var selectAll = grid.down('[itemId=selectAll]').checked;
        Ext.MessageBox.prompt("输入导出后的文件名","文件名",function (btn2,text) {
            filenames=text;
            isbtn=btn2
            if(isbtn=="ok"){
                if(filenames){
					if(pattern.test(filenames) || filenames.indexOf('\\') > -1) {
						XD.msg("文件名称不能包含下列任何字符：\\/:*?\"<>|");
						return;
					}
					
                    Ext.Ajax.request({
                        async :  false,
                        method: 'POST',
                        params:{
                            ids:ids,
                            nodeId:nodeid,
                            fileName:filenames
                        },
                        url: '/export/exportParameter',
                        success: function () {
                        }
                    });
                    var nw=window.open("/export/exportExcle");
                    nw.document.title = '正在努力下载文件中.....';
                }else {
                    XD.msg('文件名不能为空');
                    return;
                }
            }
        });
    },

    //--------自选字段导出--s----//
    chooseFieldExportExcel: function (view) {
        exportState = "Excel";
        var userGridView = view.findParentByType('appraisalGridView');
        var select = userGridView.getSelection();
        var tree = this.findGridView(view).down('treepanel');
        var ids = [];
        //var selectAll = userGridView.down('[itemId=selectAll]').checked;
        var pattern = new RegExp("[`*':'.<>/?*|‘”“'\\\\？\"\"]");
        Ext.each(userGridView.getSelectionModel().getSelection(), function () {
            ids.push(this.get('entryid'));
        });
        entryids = ids;
        if (ids.length == 0) {
            XD.msg('请至少选择一条需要导出的数据');
            return;
        } else {
            var selectItem = Ext.create("Appraisal.view.AppraisalGroupSetView", {});
            selectItem.items.get(0).getStore().load({});
            selectItem.show();
        }
    },
    chooseFieldExportXml: function (view) {
        exportState = "Xml";
        var userGridView = view.findParentByType('appraisalGridView');
        var select = userGridView.getSelection();
        var tree = this.findGridView(view).down('treepanel');
        var ids = [];
        //var selectAll = userGridView.down('[itemId=selectAll]').checked;
        var pattern = new RegExp("[`*':'.<>/?*|‘”“'\\\\？\"\"]");
        Ext.each(userGridView.getSelectionModel().getSelection(), function () {
            ids.push(this.get('entryid'));
        });
        entryids = ids;
        if (ids.length == 0) {
            XD.msg('请至少选择一条需要导出的数据');
            return;
        } else {
            var selectItem = Ext.create("Appraisal.view.AppraisalGroupSetView", {});
            selectItem.items.get(0).getStore().load({});
            selectItem.show();
        }
    },
    chooseFieldExportXmlAndFile: function (view) {
        exportState = "XmlAndFile";
        var userGridView = view.findParentByType('appraisalGridView');
        var select = userGridView.getSelection();
        var tree = this.findGridView(view).down('treepanel');
        var ids = [];
        //var selectAll = userGridView.down('[itemId=selectAll]').checked;
        var pattern = new RegExp("[`*':'.<>/?*|‘”“'\\\\？\"\"]");
        Ext.each(userGridView.getSelectionModel().getSelection(), function () {
            ids.push(this.get('entryid'));
        });
        entryids = ids;
        if (ids.length == 0) {
            XD.msg('请至少选择一条需要导出的数据');
            return;
        } else {
            var selectItem = Ext.create("Appraisal.view.AppraisalGroupSetView", {});
            selectItem.items.get(0).getStore().load({});
            selectItem.show();
        }
    },
    chooseFieldExportExcelAndFile: function (view) {
        exportState = "ExcelAndFile";
        var userGridView = view.findParentByType('appraisalGridView');
        var select = userGridView.getSelection();
        var tree = this.findGridView(view).down('treepanel');
        var ids = [];
        //var selectAll = userGridView.down('[itemId=selectAll]').checked;
        var pattern = new RegExp("[`*':'.<>/?*|‘”“'\\\\？\"\"]");
        Ext.each(userGridView.getSelectionModel().getSelection(), function () {
            ids.push(this.get('entryid'));
        });
        entryids = ids;
        if (ids.length == 0) {
            XD.msg('请至少选择一条需要导出的数据');
            return;
        } else {
            var selectItem = Ext.create("Appraisal.view.AppraisalGroupSetView", {});
            selectItem.items.get(0).getStore().load({});
            selectItem.show();
        }
    },
    chooseSave: function (view) {
        var filenames = "";
        var isbtn = "";
        var pattern = new RegExp("[/:*?\"<>|]");
        var selectView = view.findParentByType('appraisalGroupSetView');
        var FieldCode = selectView.items.get(0).getValue()
        appraisalfieldcod = FieldCode;
        var exporUrl = "";
        if (FieldCode.length>0) {
            var win = Ext.create("Appraisal.view.AppraisalMessageView", {});
            win.show();
        }else {
            XD.msg("请选择需要导出的字段")
        }
    }
    //--------自选字段导出----e----//

});