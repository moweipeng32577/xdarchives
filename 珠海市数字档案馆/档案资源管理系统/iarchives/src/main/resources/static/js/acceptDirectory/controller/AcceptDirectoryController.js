/**
 * Created by Administrator on 2019/6/24.
 */


Ext.define('AcceptDirectory.controller.AcceptDirectoryController', {
    extend: 'Ext.app.Controller',

    views: [
        'AcceptDirectoryFormAndGridView','AcceptDirectoryFormView','AcceptDirectoryGridView',
        'DataNodeComboView','FormAndGridView','FormAndInnerGridView','FormView',
        'AcceptDirectoryExportSetView','AcceptDirectoryExportMsgView','AcceptDetailGridView','AcquisitionReportGridView'
    ],
    models: [
        'AcceptDirectoryModel','ImportGridModel','AcceptDirectoryExportSetModel',
        'AcceptDetailGridModel','ReportGridModel'
    ],
    stores: [
        'AcceptDirectoryStore','ImportGridStore','TemplateStore','AcceptDirectoryExportSetStore',
        'AcceptDetailGridStore','ReportGridStore'
    ],

    init: function () {
        var treeNode;
        this.control({
            'acceptDirectoryFormAndGridView [itemId=treepanelId]': {
                render: function (view) {
                    view.getRootNode().on('expand', function (node) {
                        for (var i = 0; i < node.childNodes.length; i++) {
                            if (node.childNodes[i].raw.text == '全宗卷管理') {//隐藏全宗卷管理
                                node.childNodes[i].raw.visible = false;
                            }
                            //*if (node.childNodes[i].raw.text == '已归管理') {//默认打开已归管理第一条节点
                            //     treeNode = node.childNodes[i].raw.id;
                            // }
                            // if (node.childNodes[i].raw.parentId == treeNode) {//找到已归管理下的所有节点
                            //     treeNode = node.childNodes[0].raw.id;
                            //     node.getOwnerTree().expandPath(node.childNodes[0].raw.id, "id");
                            //     node.getOwnerTree().getSelectionModel().select(node.childNodes[0]);
                            // }*/
                        }
                    })
                },
                select: function (treemodel, record) {
                    var gridcard = this.findView(treemodel.view).down('[itemId=gridcard]');
                    var onlygrid = gridcard.down('[itemId=onlygrid]');
                    var pairgrid = gridcard.down('[itemId=pairgrid]');
                    var grid;
                    var nodeType = record.data.nodeType;
                    var bgSelectOrgan = gridcard.down('[itemId=bgSelectOrgan]');
                    treepanelInfo = this.findView(treemodel.view).down('treepanel');
                    //树节点为分类，更改右边页面为“请选择机构节点”
                    if (nodeType == 2) {
                        gridcard.setActiveItem(bgSelectOrgan);
                    } else {
                        if (record.data.classlevel == 2) {
                            gridcard.setActiveItem(pairgrid);
                            var ajgrid = pairgrid.down('[itemId=northgrid]');
                            ajgrid.setTitle("当前位置：" + record.data.text);
                            var jngrid = pairgrid.down('[itemId=southgrid]');
                            jngrid.setTitle("查看卷内");
                            if (jngrid.expandOrcollapse == 'expand') {
                                jngrid.expand();
                            } else {
                                jngrid.collapse();
                            }
                            jngrid.dataUrl = '/acceptDirectory/entries/innerfile/' + '' + '/';
                            jngrid.initGrid({nodeid: record.get('fnid')});
                            grid = ajgrid;
                        } else {
                            gridcard.setActiveItem(onlygrid);
                            onlygrid.setTitle("当前位置：" + record.data.text);
                            grid = onlygrid;
                        }
                        AcFormAndGridView = gridcard.up('acceptDirectoryFormAndGridView');
                        var gridview = gridcard.up('acceptDirectoryFormAndGridView').down('formAndGrid').down('acceptDirectoryGridView');
                        gridview.setTitle("当前位置：" + record.data.text);//将表单与表格视图标题改成当前位置
                        // this.refreshToolbarBtn(record.data.classlevel,grid,gridview);
                        grid.nodeid = record.get('fnid');
                        grid.initGrid({nodeid: record.get('fnid')});
                        //---

                        // var demoStore = Ext.getStore('AcquisitionGroupSetStore');
                        // demoStore.proxy.extraParams.fieldNodeid = record.get('fnid');
                        //--
                        var fullname = record.get('text');
                        while (record.parentNode.get('text') != '数据接收') {
                            fullname = record.parentNode.get('text') + '_' + fullname;
                            record = record.parentNode;
                        }
                        grid.nodefullname = fullname;
                        grid.parentXtype = 'acceptDirectoryFormAndGridView';
                        grid.formXtype = 'acceptDirectoryFormView';
                        var btn = grid.down('[itemId=basicgridCloseBtn]');
                        btn.hide();
                    }
                }
            },
            'acceptDirectoryGridView ': {
                eleview: this.activeEleForm,
                itemdblclick: this.lookHandler
            },
            'acceptDirectoryFormView [itemId=preBtn]': {
                click: this.preHandler
            },
            'acceptDirectoryFormView [itemId=nextBtn]': {
                click: this.nextHandler
            },
            'acceptDirectoryFormView [itemId=back]': {//返回
                click: function (btn) {
                    var treepanel = treepanelInfo;
                    var nodeid = treepanel.selModel.getSelected().items[0].get('fnid');
                    var currentAcquisitionform = this.getCurrentAcquisitionform(btn);
                    var formview = currentAcquisitionform.down('dynamicform');
                    //切换到列表界面,同时刷新列表数据(判断树节点nodeid是否和表单指定的nodeid)
                    if (formview.nodeid != nodeid) {
                        this.activeGrid(btn, false);
                    } else {
                        this.activeGrid(btn, true);
                    }
                }
            },
            'acceptDirectoryGridView [itemId=save]': {//著录
                click: this.saveHandler
            },
            'acceptDirectoryGridView [itemId=modify]': {//修改
                click: this.modifyHandler
            },
            'acceptDirectoryGridView [itemId=del]': {//删除
                click: this.delHandler
            },
            'acceptDirectoryGridView [itemId=look]': {//查看
                click: this.lookHandler
            },
            'acceptDirectoryFormView [itemId=save]': {//保存
                click: this.submitForm
            },
            'acceptDirectoryFormView [itemId=continuesave]': {//连续录入
                click: this.continueSubmitForm
            },
            'acceptDirectoryGridView [itemId=print]':{//打印
                click:this.printHandler
            },
            'acceptDirectoryGridView [itemId=exportEx]': {//导出excel--
                click: this.chooseFieldExportExcel
            },
            'acceptDirectoryGridView [itemId=exportXml]': {//导出xml
                click: this.chooseFieldExportXml
            },
            // 'acquisitiongrid [itemId=ExcleAndElectronic]': {//导出excel和原文
            //     click: this.chooseFieldExportExcelAndFile
            // },
            // 'acquisitiongrid [itemId=XmlAndElectronic]': {//导出xml和原文
            //     click: this.chooseFieldExportXmlAndFile
            // },

            'acceptDirectoryGridView [itemId=exportFileCode]': {//导出字段模板
                click: this.downloadFieldTemp
            },
            'acceptDirectoryExportSetView button[itemId="save"]': {
                click: this.chooseSave
            },
            'acceptDirectoryExportSetView button[itemId="addAllOrNotAll"]': {
                click:function(view){
                    var itemSelector = view.findParentByType('acceptDirectoryExportSetView').down('itemselector');
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

            'acceptDirectoryExportSetView button[itemId="close"]':{
              click:function (view) {
                  view.findParentByType('acceptDirectoryExportSetView').close();
              }
            },

            'acceptDirectoryExportMsgView button[itemId="SaveExport"]': {//导出
                click: function (view) {
                    var AcquisitionMessageView = view.up('acceptDirectoryExportMsgView');
                    var fileName = AcquisitionMessageView.down('[itemId=userFileName]').getValue();
                    var zipPassword = AcquisitionMessageView.down('[itemId=zipPassword]').getValue();
                    var b = AcquisitionMessageView.down('[itemId=addZipKey]').checked;
                    var form = AcquisitionMessageView.down('[itemId=form]');
                    tempParams['fileName'] = fileName;
                    tempParams['zipPassword'] = zipPassword;
                    tempParams['userFieldCode'] = userFieldCode;
                    tempParams['exporttype'] = "accept";

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
                        if(tempParams.exportState=="Xml"&&tempParams.indexLength>10000){
                            Ext.Msg.alert("提示", "提示：导出xml文件只支持导出1万条以内！");
                            return;
                        }
                        Ext.MessageBox.wait('正在处理请稍后...', '提示');
                        Ext.Ajax.request({
                            method: 'post',
                            url:'/export/capturechooseFieldExport',
                            timeout:XD.timeout,
                            scope: this,
                            async:true,
                            params: tempParams,
                            success:function(res){
                                var obj = Ext.decode(res.responseText).data;
                                if(obj.fileSizeMsg=="NO"){
                                    XD.msg('原文总大小超出限制');
                                    Ext.MessageBox.hide();
                                    return;
                                }
                                if(obj.entrySizeMsg=="NO"){
                                    if(tempParams.exportState=="XmlAndFile"||tempParams.exportState=="ExcelAndFile"){
                                        XD.msg('条目数超出限制，一次只支持导出10万含原文的条目！');
                                    }
                                    if(tempParams.exportState=="Excel"||tempParams.exportState=="Xml"){
                                        XD.msg('条目数超出限制，一次只支持导出50w的条目！');
                                    }
                                    Ext.MessageBox.hide();
                                    return;
                                }
                                window.location.href="/export/downloadZipFile?fpath="+encodeURIComponent(obj.filePath);
                                Ext.MessageBox.hide();
                                XD.msg('文件生成成功，正在准备下载');
                                AcquisitionMessageView.close()
                            },
                            failure:function(){
                                Ext.MessageBox.hide();
                                XD.msg('文件生成失败');
                            }
                        });
                    } else {
                        XD.msg("文件名不能为空")
                    }
                }
            },

            'acceptDirectoryExportMsgView button[itemId="cancelExport"]': {
                click:function (view) {
                    view.findParentByType('acceptDirectoryExportMsgView').close();
                }
              },

            'acceptDirectoryGridView button[itemId=checkAccept]':{  //确认接收
                click:function (view) {
                    var grid = view.findParentByType('acceptDirectoryGridView');
                    var record = grid.getSelectionModel().getSelection();
                    var selectAll = grid.down('[itemId=selectAll]').checked;
                    if (record.length <1) {
                        XD.msg('请选择一条操作记录!');
                        return;
                    }
                    var isSelectAll = false;
                    if(selectAll){
                        record = grid.acrossDeSelections;
                        isSelectAll = true;
                    }
                    var entryids = [];
                    for(var i = 0; i < record.length; i++){
                        entryids.push(record[i].get('entryid'));
                    }
                    XD.confirm('是否确定接收这'+record.length+'条数据？',function(){
                        Ext.Msg.wait('正在进行接收操作，请耐心等待……', '正在操作');
                        Ext.Ajax.request({
                            url:'/acceptDirectory/move',
                            params:{
                                isSelectAll: isSelectAll,
                                nodeid: grid.nodeid,
                                entryids:entryids,
                                movetype:"accept"
                            },
                            method: 'post',
                            timeout:XD.timeout,
                            success:function(response){
                                Ext.MessageBox.hide();
                                var resp = Ext.decode(response.responseText);
                                if (resp.msg == '档号记录重复') {
                                    XD.msg(Ext.decode(response.responseText).data);
                                } else {
                                    XD.msg(Ext.decode(response.responseText).msg);
                                    grid.getStore().reload();
                                }
                            }
                        })
                    });
                }
            },

            'acceptDirectoryGridView button[itemId=lookAcceptDetail]': {  //查看接收明细
                click:function (view) {
                    var lookAcceptDetail = Ext.create("Ext.window.Window",{
                        height:"60%",
                        width:"50%",
                        draggable: false,//禁止拖动
                        resizable: false,//禁止缩放
                        modal:true,
                        layout:"fit",
                        title:"查看接收明细",
                        items:[{
                            xtype: "acceptDetailGridView"
                        }]
                    });
                    var acceptDetailGridView = lookAcceptDetail.down('acceptDetailGridView');
                    acceptDetailGridView.initGrid({imptype:"目录接收"});
                    // lookAcceptDetail.down('[itemId=basicgridCloseBtn]').setVisible(false);//隐藏关闭按钮
                    lookAcceptDetail.show();
                }
            },

            'acceptDirectoryReportGridView [itemId=print]':{//打印报表
                click:function (btn) {
                    var reportGrid = btn.findParentByType('acceptDirectoryReportGridView');
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

            'acceptDirectoryReportGridView [itemId=showAllReport]':{//显示所有报表
                click:function (btn) {
                    var reportGrid = btn.findParentByType('acceptDirectoryReportGridView');
                    if(reportGrid.down('[itemId=showAllReport]').text=='显示所有报表'){
                        reportGrid.down('[itemId=showAllReport]').setText('显示当前报表');
                        reportGrid.initGrid({nodeid:reportGrid.nodeid,flag:'all'});
                    }else if(reportGrid.down('[itemId=showAllReport]').text=='显示当前报表'){
                        reportGrid.down('[itemId=showAllReport]').setText('显示所有报表');
                        reportGrid.initGrid({nodeid:reportGrid.nodeid});
                    }
                }
            },

            'acceptDirectoryReportGridView [itemId=back]':{//报表列表返回至数据列表
                click:function (btn) {
                    btn.up('window').hide();
                }
            },'acceptDetailGridView [itemId=printAcceptDetail]':{
                click:function (btn) {
                    if(reportServer == 'UReport'){
                        Ext.Ajax.request({
                            method: 'GET',
                            url: '/report/ifFileExist/' + "''",
                            scope: this,
                            async: false,
                            params:{isNotReportTemplate:'true',fileName:'目录接收明细表'},
                            success: function (response) {
                                var responseText = Ext.decode(response.responseText);
                                if (responseText.success == true) {
                                    var params = {};
                                    params['imptype'] = '目录接收';
                                    XD.UReportPrint('目录接收明细表', '目录接收明细表', params);
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
                            url: '/report/ifFileExist/' + "",
                            scope: this,
                            async: false,
                            success: function (response) {
                                var responseText = Ext.decode(response.responseText);
                                if (responseText.success == true) {
                                    //在ajax里面异步打开新标签页，会被拦截(谷歌会拦截，360不会)
                                    var win = null;
                                    XD.FRprint(win, filename, '目录接收');
                                } else {
                                    XD.msg('打印失败！' + responseText.msg);
                                    return;
                                }
                            }
                        });
                    }
                }
            }
        });
    },
    //批量修改
    doBatchModify:function (btn) {
        var resultGrid = this.getGrid(btn);
        var records = resultGrid.getSelectionModel().getSelection();
        var selectCount = resultGrid.getSelectionModel().getSelectionLength();
        if(selectCount==0){
            XD.msg('请选择数据');
            return;
        }
        if(selectCount>5000){
            XD.msg('最多只能选择5000条数据操作');
            return;
        }
        var selectAll=resultGrid.down('[itemId=selectAll]').checked;
        var isSelectAll = false;
        if(selectAll){
            records = resultGrid.acrossDeSelections;
            isSelectAll = true;
        }
        var tmp = [];
        for(var i = 0; i < records.length; i++){
            tmp.push(records[i].get('entryid'));
        }
        var entryids = tmp.join(',');
        var fromOutside = false;
        var params = resultGrid.getStore().proxy.extraParams;
        if(typeof(params.content)!=='undefined'){
            fromOutside = true;
        }
        var batchModifyModifyWin = Ext.create('Ext.window.Window',{
            width:'100%',
            height:'100%',
            title:'批量修改',
            // draggable : true,//可拖动
            // resizable : false,//禁止缩放
            modal:true,
            closeToolText:'关闭',
            layout:'fit',
            items:[{
                xtype: 'batchModifyModifyFormView',
                entryids:entryids,
                resultgrid:resultGrid,
                isSelectAll:isSelectAll,
                fromOutside:fromOutside
            }]
        });
        var fieldModifyPreviewGrid = batchModifyModifyWin.down('grid');
        if(fieldModifyPreviewGrid.getStore().data.length>0){
            fieldModifyPreviewGrid.getStore().removeAll();
        }
        batchModifyModifyWin.show();
//        window.batchModifyModifyWins = batchModifyModifyWin;
//        Ext.on('resize',function(a,b){
//            window.batchModifyModifyWins.setPosition(0, 0);
//            window.batchModifyModifyWins.fitContainer();
//        });
    },
    loadModifyTemplatefieldCombo:function (view) {//加载批量修改form的下拉框
        var combostore = view.getStore();
        var batchModifyModifyFormView = view.up('batchModifyModifyFormView');
        combostore.proxy.extraParams.datanodeidAndFieldcodes = batchModifyModifyFormView.resultgrid.nodeid;
        combostore.load();
    },
    loadModifyTemplateEnumfieldCombo:function (view) {//加载批量修改form的下拉框(处理枚举值)
        var data = view.valueCollection.items[0].data;
        var batchModifyModifyFormView = view.up('batchModifyModifyFormView');
        var updateFieldvalue = batchModifyModifyFormView.down('[itemId=updateFieldvalue]');
        var enumfieldCombo = batchModifyModifyFormView.down('[itemId=enumfieldCombo]');
        var codeInfo = batchModifyModifyFormView.getForm().findField('code');
        Ext.Ajax.request({
            url: '/template/getInactiveformfield',
            params: {
                nodeid: data.nodeid,
                field: data.fieldcode
            },
            method: 'POST',
            success: function (resp) {
                var value = Ext.decode(resp.responseText);
                if (data.ftype == 'enum') {
                    codeInfo.setValue('');
                    var store = enumfieldCombo.getStore('BatchModifyTemplateEnumfieldStore');
                    store.proxy.extraParams = {configCode: data.fieldname};
                    store.load();

                    updateFieldvalue.hide();
                    enumfieldCombo.show();

                    if (value.msg == '隐藏') {
                        codeInfo.editable = false;
                        enumfieldCombo.editable = false;
                    } else {
                        codeInfo.editable = true;
                        enumfieldCombo.editable = true;
                    }
                } else {
                    updateFieldvalue.show();
                    enumfieldCombo.hide();
                }
            },
            failure: function (resp) {
                XD.msg('操作失败！');
            }
        });
    },
    loadReplaceTemplatefieldCombo:function (view) {//加载批量替换form的下拉框
        var combostore = view.getStore();
        var batchModifyReplaceFormView = view.up('batchModifyReplaceFormView');
        combostore.proxy.extraParams.datanodeidAndFieldcodes = batchModifyReplaceFormView.resultgrid.nodeid;
        combostore.load();
    },
    loadAddTemplatefieldCombo:function (view) {//加载批量增加form的下拉框
        var combostore = view.getStore();
        var batchModifyReplaceFormView = view.up('batchModifyAddFormView');
        combostore.proxy.extraParams.datanodeidAndFieldcodes = batchModifyReplaceFormView.resultgrid.nodeid;
        combostore.load();
    },
    findView: function (btn) {
        return btn.up('acceptDirectoryFormAndGridView');
    },

    findGridView: function (btn) {
        return this.findView(btn).getComponent('gridview');
    },

    getGrid: function (btn) {
        var grid;
        if (!btn.findParentByType('formAndGrid')) {
            grid = this.findActiveGrid(btn);
        } else {
            grid = this.findGridToView(btn);
        }
        return grid;
    },

    findActiveGrid: function (btn) {
        var active = this.findView(btn).down('[itemId=gridcard]').getLayout().getActiveItem();
        if (active.getXType() == "acceptDirectoryGridView") {
            return active;
        } else if (active.getXType() == "panel") {
            return active.down('[itemId=northgrid]');
        }
    },

    findGridToView: function (btn) {
        return this.findView(btn).down('formAndGrid').down('acceptDirectoryGridView');
    },

    findDfView: function (btn) {
        return this.findView(btn).down('formView').down('acceptDirectoryFormView').down('dynamicform');
    },

    //切换到单个表单界面视图
    activeToForm: function (form) {
        var view = this.findView(form);
        var formView = view.down('formView');
        var acquisitionform = formView.down('acceptDirectoryFormView');
        view.setActiveItem(formView);
        acquisitionform.items.get(0).enable();
        /*acquisitionform.setActiveTab(0);*/
        return formView;
    },





    activeEleForm: function (obj) {
        var view = this.findView(obj.grid);
        var formview = this.getCurrentAcquisitionform(obj.grid);
        view.setActiveItem(formview.findParentByType('panel'));
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

    getCurrentAcquisitionform: function (btn) {
        if (btn.up('formAndGrid')) {//如果是案卷表单
            return this.findFormView(btn);
        }
        if (btn.up('formAndInnerGrid')) {//如果是卷内表单
            return this.findFormInnerView(btn);
        }
        if (btn.up('formView') || btn.xtype == 'entrygrid' || btn.xtype == 'acceptDirectoryGridView') {
            return this.findFormToView(btn);
        }
    },

    //切换到列表界面视图
    activeGrid: function (btn, flag) {
        var view = this.findView(btn);
        // if(view.xtype=='acquisitionTransdocView'){
        //     this.application.getController('AcquisitionTransforController').activeDocEntryGrid(btn);
        //     return;
        // }
        view.setActiveItem(this.findGridView(btn));
        this.findFormView(btn).saveBtn = undefined;
        this.findFormView(btn).continueSaveBtn = undefined;
        this.findFormInnerView(btn).saveBtn = undefined;
        this.findFormInnerView(btn).continueSaveBtn = undefined;
        formvisible = false;
        var allMediaFrame = document.querySelectorAll('#mediaFrame');
        if (allMediaFrame) {
            for (var i = 0; i < allMediaFrame.length; i++) {
                allMediaFrame[i].setAttribute('src', '');
            }
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

    changeBtnStatus: function (form, operate) {
        var savebtn, continuesave, tbseparator;
        if (form.findParentByType('formAndGrid')) {
            savebtn = this.findFormView(form).down('[itemId=save]');
            continuesave = this.findFormView(form).down('[itemId=continuesave]');
            tbseparator = this.findFormView(form).getDockedItems('toolbar')[0].query('tbseparator');
        }
        if (form.findParentByType('formAndInnerGrid')) {
            savebtn = this.findFormInnerView(form).down('[itemId=save]');
            continuesave = this.findFormInnerView(form).down('[itemId=continuesave]');
            tbseparator = this.findFormInnerView(form).getDockedItems('toolbar')[0].query('tbseparator');
        }
        if (form.findParentByType('formView') || form.findParentByType('acquisitionTransdocView')) {
            savebtn = this.findFormToView(form).down('[itemId=save]');
            continuesave = this.findFormToView(form).down('[itemId=continuesave]');
            tbseparator = this.findFormToView(form).getDockedItems('toolbar')[0].query('tbseparator');
        }
        if (operate == 'look') {//查看时隐藏保存及连续录入按钮
            savebtn.setVisible(false);
            continuesave.setVisible(false);
            tbseparator[0].setVisible(false);
            tbseparator[1].setVisible(false);
        } else if (operate == 'modify' || operate == 'insertion') {//修改或插件时隐藏连续录入按钮
            savebtn.setVisible(true);
            continuesave.setVisible(false);
            tbseparator[0].setVisible(false);
            tbseparator[1].setVisible(true);
        } else {
            savebtn.setVisible(true);
            continuesave.setVisible(true);
            tbseparator[0].setVisible(true);
            tbseparator[1].setVisible(true);
        }
    },

    findFormView: function (btn) {
        return this.findView(btn).down('formAndGrid').down('acceptDirectoryFormView');
    },

    findFormInnerView: function (btn) {
        return this.findView(btn).down('formAndInnerGrid').down('acceptDirectoryFormView');
    },

    findFormToView: function (btn) {
        return this.findView(btn).down('formView').down('acceptDirectoryFormView');
    },

    initSouthGrid:function (form) {
        var formAndGridView = this.findView(form).down('formAndGrid');//保存表单与表格视图
        var gridview = formAndGridView.down('acceptDirectoryGridView');
        gridview.initGrid({nodeid:form.nodeid});
    },

    findTreeView : function (btn) {
        return btn.up('acceptDirectoryFormAndGridView').down('treepanel');
    },

    findInnerGrid:function(btn){
        return this.findView(btn).down('[itemId=southgrid]');
    },

    //切换到表单界面视图
    activeForm: function (form) {
        var view = this.findView(form);
        var formAndGridView = view.down('formAndGrid');//保存表单与表格视图
        var formview = formAndGridView.down('acceptDirectoryFormView');
        view.setActiveItem(formAndGridView);
        formview.items.get(0).enable();
        formview.setActiveTab(0);
        return formAndGridView;
    },

    lookHandler: function (btn) {
        var grid = this.getGrid(btn);
        var form = this.findDfView(btn);
        var records = grid.selModel.getSelection();
        var selectCount = records.length;
        var selectAll = grid.down('[itemId=selectAll]').checked;
        if (selectAll) {
            XD.msg('不支持选择所有页查看');
            return;
        }
        var tree = this.findGridView(btn).down('treepanel');
        var node = tree.selModel.getSelected().items[0];
        if (!node) {
            XD.msg('请选择节点');
            return;
        }
        if (selectCount == 0) {
            XD.msg('请至少选择一条需要查看的数据');
            return;
        }
        var entryids = [];
        for (var i = 0; i < records.length; i++) {
            entryids.push(records[i].get('entryid'));
        }
        var initFormFieldState = this.initFormField(form, 'hide', node.get('fnid'));
        if (!initFormFieldState) {//表单控件加载失败
            return;
        }
        form.operate = 'look';
        form.entryids = entryids;
        form.entryid = entryids[0];
        this.initFormData('look', form, entryids[0]);
        this.activeToForm(form);
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
        if (formField.length == 0) {
            XD.msg('请检查档号设置信息是否正确');
            return;
        }
        form.templates = formField;
        form.initField(formField, operate);//重新动态添加表单控件
//        }
        return '加载表单控件成功';
    },

    //点击上一条
    preHandler: function (btn) {
        var currentAcquisitionform = this.getCurrentAcquisitionform(btn);
        var form = currentAcquisitionform.down('dynamicform');
        this.preNextHandler(form, 'pre');
    },

    //点击下一条
    nextHandler: function (btn) {
        var currentAcquisitionform = this.getCurrentAcquisitionform(btn);
        var form = currentAcquisitionform.down('dynamicform');
        this.preNextHandler(form, 'next');
    },

    //条目切换，上一条下一条
    preNextHandler: function (form, type) {
        var dirty = !!form.getForm().getFields().findBy(function (f) {
            return f.wasDirty;
        });
        if (form.operate == 'modify' && dirty) {
            XD.confirm('数据已修改，确定保存吗？', function () {
                //保存数据
                var formview = this.form;
                var nodename = this.ref.getNodename(formview.nodeid);
                var params = {
                    nodeid: formview.nodeid,
                    type: formview.findParentByType('acceptDirectoryFormView').operateFlag,
                    eleid: formview.findParentByType('acceptDirectoryFormView').down('electronic').getEleids(),
                    operate: nodename
                };
                var fieldCode = formview.getRangeDateForCode();//字段编号，用于特殊的自定义字段(范围型日期)
                if (fieldCode != null) {
                    params[fieldCode] = formview.getDaterangeValue();
                }
                var archivecodeSetState = formview.setArchivecodeValueWithNode(nodename);
                if (!archivecodeSetState) {//若档号设置失败，则停止后续的表单提交
                    return;
                }
                Ext.MessageBox.wait('正在保存请稍后...', '提示');
                var formValues = formview.getValues();
                for (var name in formValues) {//遍历表单中的所有值
                    if (name == 'kccount' || name == 'fscount') {
                        if (formValues[name] == '' || formValues[name] == null) {
                            formValues[name] = "0";
                        }
                    }
                }
                formview.submit({
                    method: 'POST',
                    url: '/acceptDirectory/entries',
                    params: params,
                    scope: this,
                    success: function (form, action) {
                        Ext.MessageBox.hide();
                        this.ref.refreshFormData(this.form, this.type);
                    },
                    failure: function (form, action) {
                        Ext.MessageBox.hide();
                        XD.msg(action.result.msg);
                    }
                });
            }, {
                ref: this,
                form: form,
                type: type
            }, function () {
                this.ref.refreshFormData(this.form, this.type)
            });
        } else {
            this.refreshFormData(form, type);
        }
    },

    refreshFormData: function (form, type) {
        var entryids = form.entryids;
        var currentEntryid = form.entryid;
        var entryid;
        for (var i = 0; i < entryids.length; i++) {
            if (type == 'pre' && entryids[i] == currentEntryid) {
                if (i == 0) {
                    i = entryids.length;
                }
                entryid = entryids[i - 1];
                break;
            } else if (type == 'next' && entryids[i] == currentEntryid) {
                if (i == entryids.length - 1) {
                    i = -1;
                }
                entryid = entryids[i + 1];
                break;
            }
        }
        form.entryid = entryid;
        if (form.operate != 'undefined') {
            this.initFormData(form.operate, form, entryid);
            return;
        }
        this.initFormData('look', form, entryid);
    },

    initFormData: function (operate, form, entryid, state) {
        var nullvalue = new Ext.data.Model();
        var acquisitionform = form.up('acceptDirectoryFormView');
        var fields = form.getForm().getFields().items;
        var prebtn = form.down('[itemId=preBtn]');
        var nextbtn = form.down('[itemId=nextBtn]');
        var savebtn = acquisitionform.down('[itemId=save]');
        var continuesavebtn = acquisitionform.down('[itemId=continuesave]');
        if (operate == 'modify' || operate == 'look') {
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
        for (var i = 0; i < fields.length; i++) {
            if (fields[i].value && typeof(fields[i].value) == 'string' && fields[i].value.indexOf('label') > -1) {
                continue;
            }
            if (fields[i].xtype == 'combobox') {
                fields[i].originalValue = null;
            }
            nullvalue.set(fields[i].name, null);
        }
        form.loadRecord(nullvalue);
        /*var etips = form.up('acceptDirectoryFormView').down('[itemId=etips]');
        etips.show();*/
        if (operate != 'look' && operate != 'lookfile') {
            var settingState = this.ifSettingCorrect(form.nodeid, form.templates);
            if (!settingState) {
                return;
            }
            Ext.each(fields, function (item) {
                if (!item.freadOnly) {
                    item.setReadOnly(false);
                }
            });
        } else {
            Ext.each(fields, function (item) {
                item.setReadOnly(true);
            });
        }
        /*var eleview = this.getCurrentAcquisitionform(form).down('electronic');*/
        /*var solidview = this.getCurrentAcquisitionform(form).down('solid');*/
        // var longview = this.getCurrentAcquisitionform(form).down('long');
        if (state == '案卷著录' || state == '卷内著录') {
            //通过节点查询当前模板的默认值
            Ext.Ajax.request({
                method: 'POST',
                params: {
                    nodeid: form.nodeid,
                    entryid: entryid,
                    type: state
                },
                url: '/acceptDirectory/getDefaultInfo',//通过节点的id获取模板中所有配置值默认数据
                success: function (response) {
                    var info = Ext.decode(response.responseText);
                    form.loadRecord({
                        getData: function () {
                            return info.data;
                        }
                    });
                }
            });
            /*eleview.initData();*/
            /*solidview.initData();*/
        } else {
            Ext.Ajax.request({
                method: 'GET',
                scope: this,
                url: '/acceptDirectory/entries/' + entryid,
                success: function (response) {
                    var entry = Ext.decode(response.responseText);
                    if (operate == 'insertion') {
                        prebtn.setVisible(false);
                        nextbtn.setVisible(false);
                        this.entryID = entryid;
                        delete entry.entryid;
                    }
                    if (operate == 'lookfile') {
                        prebtn.setVisible(false);
                        nextbtn.setVisible(false);
                        savebtn.setVisible(false);
                        continuesavebtn.setVisible(false);
                    }
                    var data = Ext.decode(response.responseText);
                    if (data.organ) {
                        entry.organ = data.organ;//机构
                    }
                    if (operate == 'add') {
                        delete entry.entryid;
                        entry.filingyear = new Date().getFullYear();
                        entry.descriptiondate = Ext.util.Format.date(new Date(), 'Y-m-d H:i:s');
                        if (data.keyword && entry.keyword) {
                            entry.keyword = data.keyword;//主题词
                        }
                        Ext.Ajax.request({
                            async: false,
                            url: '/user/getUserRealname',
                            success: function (response) {
                                entry.descriptionuser = Ext.decode(response.responseText).data;
                            }
                        });
                    }
                    if (operate == 'add' || operate == 'modify') {
                        if (!data.organ) {
                            Ext.Ajax.request({
                                async: false,
                                url: '/nodesetting/findByNodeid/' + form.nodeid,
                                success: function (response) {
                                    entry.organ = Ext.decode(response.responseText).data.nodename;
                                }
                            });
                        }
                    }
                    var fieldCode = form.getRangeDateForCode();//字段编号，用于特殊的自定义字段(范围型日期)
                    if (fieldCode != null) {
                        //动态解析数据库日期范围数据并加载至两个datefield中
                        form.initDaterangeContent(entry);
                    }
                    form.loadRecord({
                        getData: function () {
                            return entry;
                        }
                    });
                    if (operate == 'add') {
                        var formValues = form.getValues();
                        var formParams = {};
                        for (var name in formValues) {//遍历表单中的所有值
                            formParams[name] = formValues[name];
                        }
                        formParams.nodeid = form.nodeid;
                        formParams.nodename = this.getNodename(form.nodeid);
                        var archive = '';
                        var calFieldName = '';
                        var calValue = '';
                        Ext.Ajax.request({//计算项的数值获取并设置
                            url: form.calurl,//动态URL
                            async: true,
                            params: formParams,
                            success: function (response) {
                                var result = Ext.decode(response.responseText).data;
                                if (result) {
                                    calFieldName = result.calFieldName;
                                    calValue = result.calValueStr;
                                    archive = result.archive;
                                }
                                var calField = form.getForm().findField(calFieldName);
                                if (calField == null) {
                                    return;
                                }
                                calField.setValue(calValue);//设置档号最后一个构成字段的值，填充至文本框中
                                var archiveCode = form.getForm().findField('archivecode');
                                if (archiveCode == null) {
                                    return;
                                }
                                archiveCode.setValue(archive);
                            }
                        });
                    }
/*                    eleview.initData(entry.entryid);
                    solidview.initData(entry.entryid);*/
                    // longview.initData(entry.entryid);
                }
            });
        }
//        form.formStateChange(operate);
     /*   form.fileLabelStateChange(eleview, operate);
        form.fileLabelStateChange(solidview, operate);*/
        // form.fileLabelStateChange(longview,operate);
        this.changeBtnStatus(form, operate);
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

    ifCodesettingCorrect: function (nodeid) {
        var codesetting = [];
        Ext.Ajax.request({
            url: '/codesetting/getCodeSettingFields',
            async: false,
            params: {
                nodeid: nodeid
            },
            success: function (response, opts) {
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

    //数据著录
    saveHandler: function (btn) {
        formvisible = true;
        formlayout = 'formgrid';
        var acquisitionform = this.findFormView(btn);
        acquisitionform.down('electronic').operateFlag='add';
        acquisitionform.operateFlag = 'add';
        var grid = this.getGrid(btn);
        var form = acquisitionform.down('dynamicform');
        var tree = this.findGridView(btn).down('treepanel');
        var selectCount = grid.selModel.getSelection().length;
        var selectAll = grid.down('[itemId=selectAll]').checked;
        if (selectAll) {
            selectCount = grid.selModel.selected.length;//当前页选中
        }
        var node = tree.selModel.getSelected().items[0];
        if (!node) {//若点击著录时左侧未选中任何节点，则提示选择节点
            XD.msg('请选择节点');
            return;
        }
        var initFormFieldState = this.initFormField(form, 'show', node.get('fnid'));
        form.down('[itemId=preNextPanel]').setVisible(false);
        var codesetting = this.getCodesetting(node.get('fnid'));
        var nodename = this.getNodename(node.get('fnid'));
        var info = false;
        if(codesetting){//如果设置了档号字段
            info = true;
        }else{
            if (nodename == '未归管理' || nodename == '文件管理' || nodename == '资料管理') {
                info = true;
            }
        }
        if(info){
            if (typeof(initFormFieldState) != 'undefined') {
                if (selectCount == 0) {
                    this.initFormData('add',form,'','案卷著录');//非卷内文件著录
                    this.activeForm(form);
                    this.initSouthGrid(form);
                }else if(selectCount!=1){
                    XD.msg('只能选择一条数据');
                } else {
                    var entryid;
                    if (selectAll) {
                        entryid = grid.selModel.selected.items[0].get("entryid");
                    } else {
                        entryid = grid.selModel.getSelection()[0].get("entryid");
                    }
                    //选择数据著录，则加载当前数据到表单界面
                    this.initFormData('add',form, entryid,'案卷数据著录');//数据著录
                    this.activeForm(form);
                    this.initSouthGrid(form);
                }
                if(form.operateType){
                    form.operateType = undefined;
                }
            }
        }else{
            XD.msg('请检查档号模板信息是否正确');
        }
    },

    modifyHandler: function (btn) {
        formvisible = true;
        formlayout = 'formview';
        var acquisitionform = this.findFormToView(btn);
        acquisitionform.down('electronic').operateFlag='modify';
        acquisitionform.operateFlag = 'modify';
        var grid = this.getGrid(btn);
        var form = acquisitionform.down('dynamicform');
        var records = grid.selModel.getSelection();
        var selectCount = records.length;
        var selectAll = grid.down('[itemId=selectAll]').checked;
        if(selectAll){
            XD.msg('不支持选择所有页修改');
            return;
        }
        var tree = this.findGridView(btn).down('treepanel');
        var node = tree.selModel.getSelected().items[0];
        if (!node) {//若点击修改时左侧未选中任何节点，则提示选择节点
            XD.msg('请选择节点');
            return;
        }
        if (selectCount == 0) {
            XD.msg('请至少选择一条需要修改的数据');
            return;
        }
        var initFormFieldState = this.initFormField(form, 'show', node.get('fnid'));
        var codesetting = this.getCodesetting(node.get('fnid'));
        var nodename = this.getNodename(node.get('fnid'));
        var info = false;
        if(codesetting){//如果设置了档号字段
            info = true;
        }else{
            if (nodename == '未归管理' || nodename == '文件管理' || nodename == '资料管理') {
                info = true;
            }
        }
        if(info){
            if (typeof(initFormFieldState) != 'undefined') {
                var entryids = [];
                for(var i=0;i<records.length;i++){
                    entryids.push(records[i].get('entryid'));
                }
                form.operate = 'modify';
                form.entryids = entryids;
                form.entryid = entryids[0];
                this.initFormData('modify', form, entryids[0]);
                this.activeToForm(form);
                if(form.operateType){
                    form.operateType = undefined;
                }
            }
        }else{
            XD.msg('请检查档号模板信息是否正确');
        }
    },


    //打开报表显示列表
    printHandler:function(btn){
        var grid = this.getGrid(btn);
        var selectAll=grid.down('[itemId=selectAll]').checked;
        var ids = [];
        if(selectAll){
            var tempParams = grid.getStore().proxy.extraParams;
            Ext.Ajax.request({
                async:false,
                url: '/acquisition/getSelectAllEntryid',
                params:tempParams,
                success:function (response) {
                    var records = Ext.decode(response.responseText);
                    if (grid.acrossDeSelections.length > 0) {
                        //获取取消选择的条目
                        var cancles=[];
                        for(var i = 0; i < grid.acrossDeSelections.length; i++){
                            cancles.push(grid.acrossDeSelections[i].get('entryid'))
                        }
                        if(cancles.length>0){
                            var strCancles =cancles.join(',');
                            //遍历总条目，获取取消选择的条目中不包含遍历的条目
                            for(var i = 0; i < records.length; i++){
                                if(strCancles.indexOf(records[i])==-1){
                                    ids.push(records[i]);
                                }
                            }
                        }else{
                            ids = records;
                        }
                    }else{
                        ids = records;
                    }
                }
            });
        }else{
            Ext.each(grid.getSelectionModel().getSelection(),function(){
                ids.push(this.get('entryid'));
            });
        }
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
                xtype: 'acceptDirectoryReportGridView',
                entryids:ids,
                nodeid:grid.nodeid
            }]
        });
        var reportGrid = reportGridWin.down('acceptDirectoryReportGridView');
        reportGrid.initGrid({nodeid:reportGrid.nodeid});
        reportGridWin.show();
    },



    delHandler: function (btn) {
        var grid = this.getGrid(btn);
        var selectAll=grid.down('[itemId=selectAll]').checked;
        var tree = this.findGridView(btn).down('treepanel');
        var node = tree.selModel.getSelected().items[0];
        if (!node) {
            XD.msg('请选择节点');
            return;
        }
        if(grid.selModel.getSelectionLength() == 0){
            XD.msg('请至少选择一条需要删除的数据');
            return;
        }
        XD.confirm('确定要删除这' + grid.selModel.getSelectionLength() + '条数据吗',function(){
            Ext.MessageBox.wait('正在删除数据...','提示');
            var record = grid.selModel.getSelection();
            var isSelectAll = false;
            if(selectAll){
                record = grid.acrossDeSelections;
                isSelectAll = true;
            }
            var tmp = [];
            for (var i = 0; i < record.length; i++) {
                tmp.push(record[i].get('entryid'));
            }
            var entryids = tmp.join(",");
            var tempParams = grid.getStore().proxy.extraParams;
            tempParams['entryids'] = entryids;
            tempParams['isSelectAll'] = isSelectAll;
            grid.getStore().proxy.url='/acceptDirectory/entriesPost';
            Ext.Msg.wait('正在删除数据，请耐心等待……', '正在操作');
            Ext.Ajax.request({
                method: 'post',
                scope: this,
                url: '/acceptDirectory/delete',
                params:tempParams,
                timeout:XD.timeout,
                success: function (response, opts) {
                    XD.msg(Ext.decode(response.responseText).msg);
                    grid.getStore().proxy.extraParams.entryids='';
                    grid.delReload(grid.selModel.getSelectionLength());
                    // grid.initGrid({nodeid: node.data.fnid});//刷新整个数据管理列表以及下面的数据显示
                    this.findInnerGrid(btn).getStore().removeAll();
                    this.findInnerGrid(btn).setTitle('查看卷内');
                    Ext.MessageBox.hide();
                },
                failure : function() {
                    Ext.MessageBox.hide();
                    XD.msg('操作失败');
                }
            })
        },this);
    },

    lookHandler: function (btn) {
        var grid = this.getGrid(btn);
        var form = this.findDfView(btn);
        var records = grid.selModel.getSelection();
        var selectCount = records.length;
        var selectAll = grid.down('[itemId=selectAll]').checked;
        if(selectAll){
            XD.msg('不支持选择所有页查看');
            return;
        }
        var tree = this.findGridView(btn).down('treepanel');
        var node = tree.selModel.getSelected().items[0];
        if (!node) {
            XD.msg('请选择节点');
            return;
        }
        if(selectCount == 0){
            XD.msg('请至少选择一条需要查看的数据');
            return;
        }
        var entryids = [];
        for(var i=0;i<records.length;i++){
            entryids.push(records[i].get('entryid'));
        }
        var initFormFieldState = this.initFormField(form, 'hide', node.get('fnid'));
        if(!initFormFieldState){//表单控件加载失败
            return;
        }
        form.operate = 'look';
        form.entryids = entryids;
        form.entryid = entryids[0];
        this.initFormData('look',form, entryids[0]);
        this.activeToForm(form);
    },

    //保存表单数据，返回列表界面视图
    submitForm: function (btn) {
        var currentAcquisitionform = this.getCurrentAcquisitionform(btn);
        var eleids = currentAcquisitionform.down('electronic').getEleids();
        var formview = currentAcquisitionform.down('dynamicform');
        var fieldCode = formview.getRangeDateForCode();//字段编号，用于特殊的自定义字段(范围型日期)
        var nodename = this.getNodename(formview.nodeid);
        var params = {
            nodeid: formview.nodeid,
            eleid: eleids,
            type: currentAcquisitionform.operateFlag,
            operate: nodename
        };
        if (fieldCode != null) {
            params[fieldCode]=formview.getDaterangeValue();
        }
        var archivecodeSetState = formview.setArchivecodeValueWithNode(nodename);
        if(!archivecodeSetState){//若档号设置失败，则停止后续的表单提交
            return;
        }
        var operateType = formview.operateType;
        var submitType = formview.submitType;
        Ext.MessageBox.wait('正在保存请稍后...','提示');
        formview.submit({
            method: 'POST',
            url: '/acceptDirectory/entries',
            params: params,
            scope: this,
            success: function (form, action) {
                Ext.MessageBox.hide();
                var treepanel = this.findTreeView(btn);
                var nodeid = treepanel.selModel.getSelected().items[0].get('fnid');
                if(action.result.success==true){
                    if(operateType=='insertion'){
                        var pages = action.result.data.pages;
                        var state = this.updateSubsequentData(this.entryID,submitType,pages);
                        //切换到列表界面,同时刷新列表数据(判断树节点nodeid是否和表单指定的nodeid)
                        if(formview.nodeid != nodeid){
                            this.activeGrid(btn, false);
                            this.findInnerGrid(btn).getStore().reload();
                        }else{
                            this.activeGrid(btn,true);
                            this.findInnerGrid(btn).getStore().removeAll();
                        }
                        if(!state){
                            return;//保存条目成功，但插件后更新后续数据计算项及档号失败
                        }
                        //XD.msg(action.result.msg);//避免两个提示同时出现
                    }
                    //多条时切换到下一条。单条时或最后一条时切换到列表界面,同时刷新列表数据
                    if(formview.entryids && formview.entryids.length > 1 && formview.entryid != formview.entryids[formview.entryids.length-1]){
                        var allMediaFrame = document.querySelectorAll('#mediaFrame');
                        if(allMediaFrame){
                            for (var i = 0; i < allMediaFrame.length; i++) {
                                allMediaFrame[i].setAttribute('src','');
                            }
                        }
                        this.refreshFormData(formview, 'next');
                    }else {
                        if (formview.nodeid != nodeid) {
                            this.activeGrid(btn, false);
                            this.findInnerGrid(btn).getStore().reload();
                        } else {
                            this.activeGrid(btn, true);
                            this.findInnerGrid(btn).getStore().removeAll();
                        }
                    }
                    XD.msg(action.result.msg);
                }
            },
            failure: function (form, action) {
                Ext.MessageBox.hide();
                XD.msg(action.result.msg);
            }
        });
        if(formview.operateType){
            formview.operateType = undefined;
        }
    },

    //连续保存
    continueSubmitForm: function (btn) {
        var currentAcquisitionform = this.getCurrentAcquisitionform(btn);
        var solidview = currentAcquisitionform.down('solid');
        var eleids = currentAcquisitionform.down('electronic').getEleids();
        var formview = currentAcquisitionform.down('dynamicform');
        var fieldCode = formview.getRangeDateForCode();//字段编号，用于特殊的自定义字段(范围型日期)
        var nodename = this.getNodename(formview.nodeid);
        var params={
            nodeid: formview.nodeid,
            eleid:eleids,
            type: currentAcquisitionform.operateFlag,
            operate: nodename
        };
        if (fieldCode != null) {
            params[fieldCode]=formview.getDaterangeValue();
        }
        var archivecodeSetState = formview.setArchivecodeValueWithNode(nodename);
        if(!archivecodeSetState){//若档号设置失败，则停止后续的表单提交
            return;
        }
        Ext.MessageBox.wait('正在保存请稍后...','提示');
        formview.submit({
            method: 'POST',
            url: '/acceptDirectory/entries',
            params: params,
            scope: this,
            success: function (form, action) {
                Ext.MessageBox.hide();
                //每次点击连续著录时刷新表单下面列表
                var grid = formview.findParentByType('panel').findParentByType('panel').down('grid');
                grid.getStore().reload();
                this.findFormView(btn).down('electronic').initData();
                this.findFormView(btn).down('solid').initData();
                // this.findFormView(btn).down('long').initData();
                this.addCalValue(formview);
                if(archivecodeSetState!='无档号节点'){
                    formview.setArchivecodeValueWithNode(nodename);
                }

                XD.msg(action.result.msg);
                //点击连续录入后，遍历表单中所有控件，将光标移动至第一个非隐藏且非只读的控件
                // var fields = form.getFields().items;
                // for(var i=0;i<fields.length;i++){
                //     if(fields[i].xtype!='hidden' && fields[i].xtype!='displayfield' && fields[i].readOnly==false){
                //         fields[i].focus(true);
                //         if(fields[i].getValue()!=null){
                //             Ext.defer(function () {
                //                 fields[i].selectText(0,fields[i].getValue().length);
                //             },1);
                //         }
                //         break;
                //     }
                // }
                // var allMediaFrame = document.querySelectorAll('#mediaFrame');
                // if(allMediaFrame){
                //     for (var i = 0; i < allMediaFrame.length; i++) {
                //         allMediaFrame[i].setAttribute('src','');
                //     }
                // }
            },
            failure: function (form, action) {
                Ext.MessageBox.hide();
                XD.msg(action.result.msg);
            }
        });
    },

    getCodesetting: function (nodeid){
        var isExist = false;//档号构成字段的集合
        Ext.Ajax.request({//获得档号构成字段的集合
            url:'/codesetting/getCodeSettingFields',
            async:false,
            params:{
                nodeid:nodeid
            },
            success:function(response){
                var res = Ext.decode(response.responseText).success;
                if (res) {
                    isExist = true;
                }
            }
        });
        return isExist;
    },

    getNodename: function (nodeid) {
        var nodename;
        Ext.Ajax.request({
            async:false,
            url: '/nodesetting/getFirstLevelNode/' + nodeid,
            success:function (response) {
                nodename = Ext.decode(response.responseText);
            }
        });
        return nodename;
    },

    //从页面获取计算项数据并+1
    addCalValue:function (form) {
        var codeSettingFieldList;//档号构成字段的集合
        Ext.Ajax.request({//获得档号构成字段的集合
            url:'/codesetting/getCodeSettingFields',
            async:false,
            params:{
                nodeid:form.nodeid
            },
            success:function(response){
                codeSettingFieldList = Ext.decode(response.responseText).data;
            }
        });
        if(!codeSettingFieldList && this.getNodename(form.nodeid)!='未归管理'){
            XD.msg('请检查档号设置信息是否正确');
            return;
        }
        var calFieldName = codeSettingFieldList[codeSettingFieldList.length-1];
        var calValue = form.getForm().findField(calFieldName).getValue();
        var calFieldLength = calValue.length;
        var addedCalValue = Number(calValue)+1;
        form.getForm().findField(calFieldName).setValue(Ext.String.leftPad(addedCalValue,calFieldLength,'0'));
    },

    //--------自选字段导出--s----//
    exportFunction:function(view, state){
        var userGridView = view.findParentByType('acceptDirectoryGridView');
        var selectAll = userGridView.down('[itemId=selectAll]').checked;
        var record = userGridView.getSelection();
        var isSelectAll = false;
        if(selectAll){
            record = userGridView.acrossDeSelections;
            isSelectAll = true;
        }
        var tmp = [];
        for(var i = 0; i < record.length; i++){
            tmp.push(record[i].get('entryid'));
        }
        var entryids = tmp.join(',');
        tempParams = userGridView.getStore().proxy.extraParams;
        tempParams['entryids'] = entryids;
        tempParams['isSelectAll'] = isSelectAll;
        tempParams['exportState'] = state;
        var gridStore=userGridView.getStore();
        tempParams['indexLength'] = gridStore.totalCount;
        if(selectAll == false && entryids.length == 0){
            XD.msg('请至少选择一条需要导出的数据');
            return;
        }
        var selectItem = Ext.create("AcceptDirectory.view.AcceptDirectoryExportSetView");
        var selectStore = selectItem.items.get(0).getStore();
        selectStore.proxy.extraParams.fieldNodeid = userGridView.nodeid;
        selectStore.reload();
        selectItem.show();
    },

    chooseFieldExportExcel: function (view) {
        this.exportFunction(view, "Excel");
    },
    chooseFieldExportXml: function (view) {
        this.exportFunction(view, "Xml");
    },
    chooseFieldExportXmlAndFile: function (view) {
        this.exportFunction(view, "XmlAndFile");
    },
    chooseFieldExportExcelAndFile: function (view) {
        this.exportFunction(view, "ExcelAndFile");
    },

    //--下载节点字段模板
    downloadFieldTemp:function(btn){
        var tree = this.findGridView(btn).down('treepanel');
        var nodeid = tree.selModel.getSelected().items[0].get('fnid');
        var reqUrl="/export/downloadFieldTemp?nodeid="+nodeid;
        window.location.href=reqUrl;
    },

    chooseSave: function (view) {
        var filenames = "";
        var isbtn = "";
        var pattern = new RegExp("[/:*?\"<>|]");
        var selectView = view.findParentByType('acceptDirectoryExportSetView');
        var FieldCode = selectView.items.get(0).getValue();
        userFieldCode = FieldCode;
        var exporUrl = "";
        if (FieldCode.length>0) {
            var win = Ext.create("AcceptDirectory.view.AcceptDirectoryExportMsgView", {});
            win.show();
        }else {
            XD.msg("请选择需要导出的字段")
        }
    }
});
