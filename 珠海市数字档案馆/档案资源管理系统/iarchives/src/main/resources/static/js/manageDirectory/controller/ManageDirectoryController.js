/**
 * Created by Administrator on 2019/6/25.
 */



Ext.define('ManageDirectory.controller.ManageDirectoryController', {
    extend: 'Ext.app.Controller',

    views: [
        'ManageDirectoryFormAndGridView','ManageDirectoryFormView','ManageDirectoryGridView',
        'FormAndGridView','FormAndInnerGridView','FormView',
        'ManageDirectoryExportSetView','ManageDirectoryExportMsgView'
    ],
    models: [
        'ManageDirectoryModel','ManageDirectoryExportSetModel','FieldModifyPreviewGridModel'
    ],
    stores: [
        'ManageDirectoryStore','ManageDirectoryExportSetStore','BatchModifyTemplatefieldStore',
        'BatchModifyTemplateEnumfieldStore','FieldModifyPreviewGridStore'
    ],

    init: function () {
        var treeNode;
        this.control({
            'manageDirectoryFormAndGridView [itemId=treepanelId]': {
                render: function (view) {
                    view.getRootNode().on('expand', function (node) {
                        for (var i = 0; i < node.childNodes.length; i++) {
                            if (node.childNodes[i].raw.text == '全宗卷管理') {//隐藏全宗卷管理
                                node.childNodes[i].raw.visible = false;
                            }
                            // if (node.childNodes[i].raw.text == '已归管理') {//默认打开已归管理第一条节点
                            //     treeNode = node.childNodes[i].raw.id;
                            // }
                            // if (node.childNodes[i].raw.parentId == treeNode) {//找到已归管理下的所有节点
                            //     treeNode = node.childNodes[0].raw.id;
                            //     node.getOwnerTree().expandPath(node.childNodes[0].raw.id, "id");
                            //     node.getOwnerTree().getSelectionModel().select(node.childNodes[0]);
                            // }
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
                            jngrid.dataUrl = '/manageDirectory/entries/innerfile/' + '' + '/';
                            jngrid.initGrid({nodeid: record.get('fnid')});
                            grid = ajgrid;
                        } else {
                            gridcard.setActiveItem(onlygrid);
                            onlygrid.setTitle("当前位置：" + record.data.text);

                            grid = onlygrid;
                        }
                        formAndGridView = gridcard.up('manageDirectoryFormAndGridView');
                        var gridview = gridcard.up('manageDirectoryFormAndGridView').down('formAndGrid').down('manageDirectoryGridView');
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
                        grid.parentXtype = 'manageDirectoryFormAndGridView';
                        grid.formXtype = 'manageDirectoryFormView';
                        var btn = grid.down('[itemId=basicgridCloseBtn]');
                        btn.hide();
                    }
                }
            },
            'manageDirectoryGridView ': {
                eleview: this.activeEleForm,
                itemdblclick: this.lookHandler
            },
            'manageDirectoryFormView':{//添加键盘监控
                afterrender:this.addKeyAction
            },
            'manageDirectoryFormView [itemId=preBtn]': {
                click: this.preHandler
            },
            'manageDirectoryFormView [itemId=nextBtn]': {
                click: this.nextHandler
            },
            'manageDirectoryFormView [itemId=back]': {//返回
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
            'manageDirectoryGridView [itemId=save]': {//著录
                click: this.saveHandler
            },
            'manageDirectoryGridView [itemId=modify]': {//修改
                click: this.modifyHandler
            },
            'manageDirectoryGridView [itemId=del]': {//删除
                click: this.delHandler
            },
            'manageDirectoryGridView [itemId=look]': {//查看
                click: this.lookHandler
            },
            'manageDirectoryFormView [itemId=save]': {//保存
                click: this.submitForm
            },
            'manageDirectoryFormView [itemId=continuesave]': {//连续录入
                click: this.continueSubmitForm
            },
            'manageDirectoryGridView [itemId=exportEx]': {//导出excel--
                click: this.chooseFieldExportExcel
            },
            'manageDirectoryGridView [itemId=exportXml]': {//导出xml
                click: this.chooseFieldExportXml
            },

            // 'acquisitiongrid [itemId=ExcleAndElectronic]': {//导出excel和原文
            //     click: this.chooseFieldExportExcelAndFile
            // },
            // 'acquisitiongrid [itemId=XmlAndElectronic]': {//导出xml和原文
            //     click: this.chooseFieldExportXmlAndFile
            // },

            'manageDirectoryGridView [itemId=exportFileCode]': {//导出字段模板
                click: this.downloadFieldTemp
            },
            'manageDirectoryExportSetView button[itemId="save"]': {
                click: this.chooseSave
            },
            'manageDirectoryExportSetView button[itemId="addAllOrNotAll"]': {
                click:function(view){
                    var itemSelector = view.findParentByType('manageDirectoryExportSetView').down('itemselector');
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

            'manageDirectoryExportSetView button[itemId="close"]':{
                click:function (view) {
                    view.findParentByType('manageDirectoryExportSetView').close();
                }
            },

            'manageDirectoryExportMsgView button[itemId="SaveExport"]': {//导出
                click: function (view) {
                    var AcquisitionMessageView = view.up('manageDirectoryExportMsgView');
                    var fileName = AcquisitionMessageView.down('[itemId=userFileName]').getValue();
                    var zipPassword = AcquisitionMessageView.down('[itemId=zipPassword]').getValue();
                    var b = AcquisitionMessageView.down('[itemId=addZipKey]').checked;
                    var form = AcquisitionMessageView.down('[itemId=form]');
                    tempParams['fileName'] = fileName;
                    tempParams['zipPassword'] = zipPassword;
                    tempParams['userFieldCode'] = userFieldCode;
                    tempParams['exporttype'] = "manage";

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

            'manageDirectoryExportMsgView button[itemId="cancelExport"]': {
                click:function (view) {
                    view.findParentByType('manageDirectoryExportMsgView').close();
                }
            },

            ///////////批量操作－－－－－－－－－－－－－－－start////////////////////////////
            'manageDirectoryGridView [itemId=batchModify]':{//批量修改
                click:this.doBatchModify
            },
            'batchModifyModifyFormView [itemId=templatefieldCombo]':{
                render:this.loadModifyTemplatefieldCombo,
                select:this.loadModifyTemplateEnumfieldCombo
            },
            'batchModifyModifyFormView button[itemId=addToModify]':{//批量修改窗口 加入修改
                click:this.addToModify
            },
            'batchModifyModifyFormView button[itemId=deleteModify]':{//批量修改窗口　删除修改
                click:this.deleteModify
            },
            'batchModifyModifyFormView button[itemId=clear]':{//批量修改窗口　清除
                click:function (btn) {
                    var formview = btn.up('batchModifyModifyFormView');
                    var fieldModifyPreviewGrid = formview.down('grid');
                    fieldModifyPreviewGrid.getStore().removeAll();
                    this.loadModifyTemplatefieldCombo(formview.getForm().findField('fieldname'));
                }
            },
            'batchModifyModifyFormView button[itemId=getPreview]':{//批量修改窗口　获取预览
                click:function (btn) {
                    var formview = btn.up('batchModifyModifyFormView');
                    var formWin = formview.up('window');
                    var fieldModifyPreviewGrid = formview.down('grid');
                    var fieldModifyPreviewGridStore = fieldModifyPreviewGrid.getStore();
                    var fieldModifyPreviewGridData = fieldModifyPreviewGridStore.data.items;
                    if(fieldModifyPreviewGridData.length==0){
                        XD.msg('请选择修改字段');
                        return;
                    }
                    var fieldModifyData = "";
                    var operateFieldcodes = [];
                    if(fieldModifyPreviewGridData.length > 0){//如果有已存在的修改字段
                        for (var i = 0; i < fieldModifyPreviewGridData.length; i++) {
                            var item = fieldModifyPreviewGridData[i];
                            if (i < fieldModifyPreviewGridData.length - 1) {
                                fieldModifyData += item.get('fieldcode')+'∪'+item.get('fieldname')+'∪'+item.get('fieldvalue')+"∩";
                            } else {
                                fieldModifyData += item.get('fieldcode')+'∪'+item.get('fieldname')+'∪'+item.get('fieldvalue');
                            }
                            operateFieldcodes.push(item.get('fieldcode'));
                        }
                    }
                    var batchModifyResultPreviewWin = Ext.create('Ext.window.Window',{
                        width:'65%',
                        height:'70%',
                        title:'批量操作预览',
                        draggable : true,//可拖动
                        resizable : false,//禁止缩放
                        modal:true,
                        closeAction: 'hide',
                        closeToolText:'关闭',
                        layout:'fit',
                        items:[{
                            xtype:'batchModifyResultPreviewGrid',
                            fieldmodifydata:fieldModifyData,
                            operateFlag:formWin.title,
                            formview:formview
                        }]
                    });
                    delTempByUniqueType('modi');
                    var resultPreviewGrid = batchModifyResultPreviewWin.down('batchModifyResultPreviewGrid');
                    var params = resultPreviewGrid.formview.resultgrid.getStore().proxy.extraParams;
                    if (formview.fromOutside && typeof(params.content) !== 'undefined') {//外面
                        params['basicCondition'] = params.condition;
                        params['basicOperator'] = params.operator;
                        params['basicContent'] = params.content;
                    }
                    delete params.condition;
                    delete params.operator;
                    delete params.content;
                    params['entryidArr'] = resultPreviewGrid.formview.entryids.split(',');
                    params['fieldModifyData'] = resultPreviewGrid.fieldmodifydata;
                    params['multiValue'] = operateFieldcodes;
                    params['excludeValues'] = operateFieldcodes;
                    params['flag'] = resultPreviewGrid.operateFlag;
                    params['isSelectAll'] = resultPreviewGrid.formview.isSelectAll;
                    params['type'] = '目录管理';
                    params['info'] = '批量操作';
                    var type = true;
                    resultPreviewGrid.initGrid(params,type);
                    batchModifyResultPreviewWin.show();
                    window.batchModifyResultPreviewWins = batchModifyResultPreviewWin;
                    Ext.on('resize',function(a,b){
                        window.batchModifyResultPreviewWins.setPosition(0, 0);
                        window.batchModifyResultPreviewWins.fitContainer();
                    });
                }
            },
            'batchModifyModifyFormView button[itemId=exit]':{//批量修改窗口　退出
                click:function (btn) {
                    formAndGridView.down('manageDirectoryGridView').getStore().proxy.url='/manageDirectory/entriesPost';
                    formAndGridView.down('manageDirectoryGridView').getStore().getProxy().actionMethods={read:'POST'};
                    btn.up('window').close();
                }
            },
            'batchModifyResultPreviewGrid button[itemId=batchUpdateBtn]':{//批量操作预览－执行批量更新
                click:this.doBatchUpdate
            },
            'batchModifyResultPreviewGrid button[itemId=backBtn]':{//批量操作预览－返回
                click:function (btn) {
                    var batchType=btn.up('batchModifyResultPreviewGrid').operateFlag;
                    if(batchType=='批量修改'){
                        batchType='modi';
                    }else if(batchType=='批量增加'){
                        batchType='add';
                    }else{
                        batchType='repl';
                    }
                    delTempByUniqueType(batchType);
                    btn.up('batchModifyResultPreviewGrid').formview.fromOutside = false;
                    btn.up('window').hide();
                }
            },

            'manageDirectoryGridView [itemId=batchRepace]':{//批量替换
                click:this.doBatchReplace
            },
            'batchModifyReplaceFormView [itemId=templatefieldCombo]':{
                render:this.loadReplaceTemplatefieldCombo
            },
            'batchModifyReplaceFormView button[itemId=getPreview]':{//批量替换窗口　获取预览
                click:function (btn) {
                    var formview = btn.up('batchModifyReplaceFormView');
                    var formWin = formview.up('window');
                    var fieldcodeandnameCombo = formview.getForm().findField('fieldname');
                    var fieldcodeAndName = fieldcodeandnameCombo.getValue();
                    var searchcontentField = formview.getForm().findField('searchcontent');
                    var searchcontent = searchcontentField.getValue();
                    var replacecontentField = formview.getForm().findField('replacecontent');
                    var replacecontent = replacecontentField.getValue();
                    var containspace = formview.down('[itemId=ifContainSpaces]').getValue();
                    var ifContainspace = containspace?true:false;
                    var allowempty = formview.down('[itemId=ifAllowEmpty]').getValue();

                    if(!fieldcodeAndName){XD.msg('请选择修改字段');return;}
                    if(!searchcontent){XD.msg('查找内容不允许为空');return;}
                    if(!replacecontent && !allowempty){XD.msg('替换值不允许为空');return;}
                    if(searchcontent==replacecontent && !ifContainspace){XD.msg('替换前后数据无变化');return;}
                    var fieldReplaceData = [fieldcodeAndName+'∪'+searchcontent+'∪'+replacecontent];
                    var operateFieldcodes =[fieldcodeAndName.split('_')[0]];
                    var batchModifyResultPreviewWin = Ext.create('Ext.window.Window',{
                        width:'65%',
                        height:'70%',
                        title:'批量操作预览',
                        draggable : true,//可拖动
                        resizable : false,//禁止缩放
                        modal:true,
                        closeAction: 'hide',
                        closeToolText:'关闭',
                        layout:'fit',
                        items:[{
                            xtype:'batchModifyResultPreviewGrid',
                            fieldreplacedata:fieldReplaceData,
                            ifcontainspace:ifContainspace,
                            operateFlag:formWin.title,
                            formview:formview
                        }]
                    });
                    delTempByUniqueType('repl');
                    var resultPreviewGrid = batchModifyResultPreviewWin.down('batchModifyResultPreviewGrid');
                    var params = resultPreviewGrid.formview.resultgrid.getStore().proxy.extraParams;
                    if (formview.fromOutside && typeof(params.content) !== 'undefined') {//外面
                        params['basicCondition'] = params.condition;
                        params['basicOperator'] = params.operator;
                        params['basicContent'] = params.content;
                    }
                    delete params.condition;
                    delete params.operator;
                    delete params.content;
                    params['entryidArr'] = resultPreviewGrid.formview.entryids.split(',');
                    params['fieldReplaceData'] = resultPreviewGrid.fieldreplacedata;
                    params['ifContainSpace'] = resultPreviewGrid.ifcontainspace;
                    params['multiValue'] = operateFieldcodes;
                    params['excludeValues'] = operateFieldcodes;
                    params['flag'] = resultPreviewGrid.operateFlag;
                    params['isSelectAll'] = resultPreviewGrid.formview.isSelectAll;
                    params['type'] = '目录管理';
                    params['info'] = '批量操作';

                    var type = true;
                    resultPreviewGrid.initGrid(params,type);
                    resultPreviewGrid.getStore().on('load',function(store){
                        if(!resultPreviewGrid.operateCount){//仅第一次load该store完成时给列表的operateCount赋值
                            resultPreviewGrid.operateCount = resultPreviewGrid.getStore().totalCount;
                        }
                    });
                    batchModifyResultPreviewWin.show();
                    window.batchModifyResultPreviewWins = batchModifyResultPreviewWin;
                    Ext.on('resize',function(a,b){
                        window.batchModifyResultPreviewWins.setPosition(0, 0);
                        window.batchModifyResultPreviewWins.fitContainer();
                    });
                }
            },
            'batchModifyReplaceFormView button[itemId=exit]':{//批量替换窗口　退出
                click:function (btn) {
                    formAndGridView.down('manageDirectoryGridView').getStore().proxy.url='/manageDirectory/entriesPost';
                    formAndGridView.down('manageDirectoryGridView').getStore().getProxy().actionMethods={read:'POST'};
                    btn.up('window').close();
                }
            },
            'manageDirectoryGridView [itemId=batchAdd]':{//批量增加
                click:this.doBatchAdd
            },
            'batchModifyAddFormView [itemId=templatefieldCombo]':{
                render:this.loadAddTemplatefieldCombo
            },
            'batchModifyAddFormView button[itemId=getPreview]':{//批量增加窗口　获取预览
                click:function (btn) {
                    var formview = btn.up('batchModifyAddFormView');
                    var formWin = formview.up('window');
                    var fieldcodeandnameCombo = formview.getForm().findField('fieldname');
                    var fieldcodeAndName = fieldcodeandnameCombo.getValue();
                    var addcontentField = formview.getForm().findField('addcontent');
                    var addcontent = addcontentField.getValue();
                    var inserttype = formview.getValues()['insertPlace'];
                    var insertplaceindexField = formview.getForm().findField('insertPlaceIndex');
                    var insertplaceindex = insertplaceindexField.getValue();

                    if(!fieldcodeAndName){XD.msg('请选择修改字段');return;}
                    if(!addcontent){XD.msg('添加内容不允许为空');return;}
                    if(!inserttype){XD.msg('请检查位置设置信息');return;}
                    if(inserttype=='anywhere' && !insertplaceindex){XD.msg('请输入插入字符位置');return;}
                    if(isNaN(insertplaceindex)){XD.msg('插入字符位置输入项格式不正确');return;}
                    if(parseInt(insertplaceindex)<1){XD.msg('插入字符位置输入项最小值为1');return;}
                    if(parseInt(insertplaceindex)>8000){XD.msg('插入字符位置输入项最大值为8000');return;}

                    var inserttypeAndPlaceindex,fieldModifyData;
                    if(inserttype=='anywhere'){
                        inserttypeAndPlaceindex = inserttype+'_'+insertplaceindex;
                        fieldModifyData = [fieldcodeAndName+'∪'+addcontent+'∪'+inserttypeAndPlaceindex];
                    }else{
                        fieldModifyData = [fieldcodeAndName+'∪'+addcontent+'∪'+inserttype];
                    }
                    var operateFieldcodes =[fieldcodeAndName.split('_')[0]];
                    var batchModifyResultPreviewWin = Ext.create('Ext.window.Window',{
                        width:'65%',
                        height:'70%',
                        title:'批量操作预览',
                        draggable : true,//可拖动
                        resizable : false,//禁止缩放
                        modal:true,
                        closeAction: 'hide',
                        closeToolText:'关闭',
                        layout:'fit',
                        items:[{
                            xtype:'batchModifyResultPreviewGrid',
                            fieldmodifydata:fieldModifyData,
                            operateFlag:formWin.title,
                            formview:formview
                        }]
                    });
                    delTempByUniqueType('add');
                    var resultPreviewGrid = batchModifyResultPreviewWin.down('batchModifyResultPreviewGrid');
                    var params = resultPreviewGrid.formview.resultgrid.getStore().proxy.extraParams;
                    if (formview.fromOutside && typeof(params.content) !== 'undefined') {//外面
                        params['basicCondition'] = params.condition;
                        params['basicOperator'] = params.operator;
                        params['basicContent'] = params.content;
                    }
                    delete params.condition;
                    delete params.operator;
                    delete params.content;
                    params['entryidArr'] = resultPreviewGrid.formview.entryids.split(',');
                    params['fieldModifyData'] = resultPreviewGrid.fieldmodifydata;
                    params['multiValue'] = operateFieldcodes;
                    params['excludeValues'] = operateFieldcodes;
                    params['flag'] = resultPreviewGrid.operateFlag;
                    params['isSelectAll'] = resultPreviewGrid.formview.isSelectAll;
                    params['type'] = '目录管理';
                    params['info'] = '批量操作';

                    var type = true;
                    resultPreviewGrid.initGrid(params,type);
                    batchModifyResultPreviewWin.show();
                    window.batchModifyResultPreviewWins = batchModifyResultPreviewWin;
                    Ext.on('resize',function(a,b){
                        window.batchModifyResultPreviewWins.setPosition(0, 0);
                        window.batchModifyResultPreviewWins.fitContainer();
                    });
                }
            },
            'batchModifyAddFormView button[itemId=exit]':{//批量增加窗口　退出
                click:function (btn) {
                    formAndGridView.down('manageDirectoryGridView').getStore().proxy.url='/manageDirectory/entriesPost';
                    formAndGridView.down('manageDirectoryGridView').getStore().getProxy().actionMethods={read:'POST'};
                    btn.up('window').close();
                }
            },
            'manageDirectoryGridView [itemId=backAccept]': {//退回目录传输
                click: this.backAcceptHandler
            }
        });
    },

    findView: function (btn) {
        return btn.up('manageDirectoryFormAndGridView');
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
        if (active.getXType() == "manageDirectoryGridView") {
            return active;
        } else if (active.getXType() == "panel") {
            return active.down('[itemId=northgrid]');
        }
    },

    findGridToView: function (btn) {
        return this.findView(btn).down('formAndGrid').down('manageDirectoryGridView');
    },

    findDfView: function (btn) {
        return this.findView(btn).down('formView').down('manageDirectoryFormView').down('dynamicform');
    },

    //切换到单个表单界面视图
    activeToForm: function (form) {
        var view = this.findView(form);
        var formView = view.down('formView');
        var acquisitionform = formView.down('manageDirectoryFormView');
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
        if (btn.up('formView') || btn.xtype == 'entrygrid' || btn.xtype == 'manageDirectoryGridView') {
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
        return this.findView(btn).down('formAndGrid').down('manageDirectoryFormView');
    },

    findFormInnerView: function (btn) {
        return this.findView(btn).down('formAndInnerGrid').down('manageDirectoryFormView');
    },

    findFormToView: function (btn) {
        return this.findView(btn).down('formView').down('manageDirectoryFormView');
    },

    initSouthGrid:function (form) {
        var formAndGridView = this.findView(form).down('formAndGrid');//保存表单与表格视图
        var gridview = formAndGridView.down('manageDirectoryGridView');
        gridview.initGrid({nodeid:form.nodeid});
    },

    findTreeView : function (btn) {
        return btn.up('manageDirectoryFormAndGridView').down('treepanel');
    },

    findInnerGrid:function(btn){
        return this.findView(btn).down('[itemId=southgrid]');
    },

    //切换到表单界面视图
    activeForm: function (form) {
        var view = this.findView(form);
        var formAndGridView = view.down('formAndGrid');//保存表单与表格视图
        var formview = formAndGridView.down('manageDirectoryFormView');
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
                    type: formview.findParentByType('manageDirectoryFormView').operateFlag,
                    eleid: formview.entryid,
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
                    url: '/manageDirectory/entries',
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
        var acquisitionform = form.up('manageDirectoryFormView');
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
/*        var etips = form.up('manageDirectoryFormView').down('[itemId=etips]');
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
/*        var eleview = this.getCurrentAcquisitionform(form).down('electronic');
        var solidview = this.getCurrentAcquisitionform(form).down('solid');*/
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
                url: '/manageDirectory/getDefaultInfo',//通过节点的id获取模板中所有配置值默认数据
                success: function (response) {
                    var info = Ext.decode(response.responseText);
                    form.loadRecord({
                        getData: function () {
                            return info.data;
                        }
                    });
                }
            });
/*            eleview.initData();
            solidview.initData();*/
        } else {
            Ext.Ajax.request({
                method: 'GET',
                scope: this,
                url: '/manageDirectory/entries/' + entryid,
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
/*        form.fileLabelStateChange(eleview, operate);
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
        /*acquisitionform.down('electronic').operateFlag='modify';*/
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
            grid.getStore().proxy.url='/manageDirectory/entriesPost';
            Ext.Msg.wait('正在删除数据，请耐心等待……', '正在操作');
            Ext.Ajax.request({
                method: 'post',
                scope: this,
                url: '/manageDirectory/delete',
                params:tempParams,
                timeout:XD.timeout,
                success: function (response, opts) {
                    XD.msg(Ext.decode(response.responseText).msg);
                    grid.getStore().proxy.extraParams.entryids='';
                    grid.delReload(grid.selModel.getSelectionLength());
//                    grid.initGrid({nodeid: node.data.fnid});//刷新整个数据管理列表以及下面的数据显示
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
        /*var eleids = currentAcquisitionform.down('electronic').getEleids();*/
        var formview = currentAcquisitionform.down('dynamicform');
        var fieldCode = formview.getRangeDateForCode();//字段编号，用于特殊的自定义字段(范围型日期)
        var nodename = this.getNodename(formview.nodeid);
        var params = {
            nodeid: formview.nodeid,
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
            url: '/manageDirectory/entries',
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
            url: '/manageDirectory/entries',
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
        var userGridView = view.findParentByType('manageDirectoryGridView');
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
        var selectItem = Ext.create("ManageDirectory.view.ManageDirectoryExportSetView");
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
        var selectView = view.findParentByType('manageDirectoryExportSetView');
        var FieldCode = selectView.items.get(0).getValue();
        userFieldCode = FieldCode;
        var exporUrl = "";
        if (FieldCode.length>0) {
            var win = Ext.create("ManageDirectory.view.ManageDirectoryExportMsgView", {});
            win.show();
        }else {
            XD.msg("请选择需要导出的字段")
        }
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
    //加入修改
    addToModify:function (btn) {
        /*第一步：判断字段及替换值是否填写正确*/
        var formview = btn.up('batchModifyModifyFormView');
        var combobox = formview.getForm().findField('fieldname');
        var fieldStr = combobox.getValue();
        if(!fieldStr){
            XD.msg('字段值不允许为空');
            return;
        }
        var value = formview.down('[itemId=enumfieldCombo]').lastMutatedValue;
        var updateFieldvalue = formview.getForm().findField('fieldvalue').getValue();
        var allowEmpty = formview.down('[itemId=ifAllowEmpty]').getValue();
        if(!allowEmpty){
            if(!updateFieldvalue && typeof(value) == 'undefined') {
                XD.msg('替换值不允许为空');
                return;
            }
        }
        /*第二步：将需要修改的字段数据追加至列表显示*/
        var fieldModifyPreviewGrid = formview.down('grid');
        var fieldModifyPreviewGridStore = fieldModifyPreviewGrid.getStore();
        var existedData = fieldModifyPreviewGridStore.data.items;
        var existedDataArr = "";
        if(existedData.length > 0){//如果有已存在的修改字段
            for (var i = 0; i < existedData.length; i++) {
                var item = existedData[i];
                if (i < existedData.length - 1) {
                    existedDataArr += item.get('fieldcode')+'∪'+item.get('fieldname')+'∪'+item.get('fieldvalue')+"∩";
                } else {
                    existedDataArr += item.get('fieldcode')+'∪'+item.get('fieldname')+'∪'+item.get('fieldvalue');
                }
            }
        }
        var params = {
            fieldcode:fieldStr.split('_')[0],
            fieldname:fieldStr.split('_')[1],
            fieldvalue:!updateFieldvalue?value:updateFieldvalue,
            existedDataArr:existedDataArr
        };
        fieldModifyPreviewGridStore.proxy.extraParams = params;
        fieldModifyPreviewGridStore.load({
            callback:function () {
                formview.getForm().findField('fieldname').setValue('');
                formview.getForm().findField('fieldvalue').setValue('');
                // 清除枚举值的下拉框
                formview.getForm().findField('code').setValue('');
            }
        });
        /*第三步：刷新字段值下拉框store*/
        this.initModifyComboStore(btn,'add');
    },
    //删除修改
    deleteModify:function (btn) {
        /*第一步：判断是否选定需要删除项*/
        var formview = btn.up('batchModifyModifyFormView');
        var fieldModifyPreviewGrid = formview.down('grid');
        var records = fieldModifyPreviewGrid.getSelectionModel().getSelected().items;
        var selectCount = records.length;
        if(selectCount==0){
            XD.msg('请选择需要删除的记录');
            return;
        }
        /*第二步：删除已选择的字段数据，刷新列表*/
        var delIds = [];
        for(var i = 0; i < records.length; i++){
            delIds.push(records[i].get('fieldcode'));
        }
        fieldModifyPreviewGrid.delIds = delIds;
        var fieldModifyPreviewGridStore = fieldModifyPreviewGrid.getStore();
        var allData = fieldModifyPreviewGridStore.data.items;
        var allIds = [];
        for(var i = 0; i < allData.length; i++){
            allIds.push(allData[i].get('fieldcode'));
        }
        for(var i=0;i<delIds.length;i++){
            for(var j=0;j<allIds.length;j++){
                if(delIds[i]==allIds[j]){
                    allIds.splice(j,1);
                    j--;
                }
            }
        }
        var remainIds = allIds;
        var remainData = [];
        for(var i=0;i<allData.length;i++){
            for(var j=0;j<remainIds.length;j++){
                if(allData[i].get('fieldcode')==remainIds[j]){
                    remainData.push(allData[i]);
                }
            }
        }
        var remainDataArr = [];
        if(remainData.length==0){
            fieldModifyPreviewGridStore.removeAll();
        }else{
            Ext.each(remainData,function (item) {
                remainDataArr.push(item.get('fieldcode')+'∪'+item.get('fieldname')+'∪'+item.get('fieldvalue'));
            });
            Ext.apply(fieldModifyPreviewGridStore.proxy.extraParams, {remainDataArr:remainDataArr});
            fieldModifyPreviewGridStore.load();
        }
        /*第三步：刷新字段值下拉框store*/
        this.initModifyComboStore(btn,'delete');
    },
    //初始化字段值下拉框
    initModifyComboStore:function (btn,operate) {
        var formview = btn.up('batchModifyModifyFormView');
        var gridview = formview.down('grid');
        var combobox = formview.getForm().findField('fieldname');

        var gridRecords = gridview.getStore().data.items;
        var fieldcodes = [];
        for(var i = 0; i < gridRecords.length; i++){
            fieldcodes.push(gridRecords[i].get('fieldcode'));
        }
        if(operate=='add'){
            fieldcodes.push(combobox.getValue().split('_')[0]);
        }
        if(operate=='delete'){
            for(var i=fieldcodes.length-1;i>=0;i--){//倒着检测，不用考虑位置影响
                Ext.each(gridview.delIds,function (item) {
                    if(fieldcodes[i]==item){
                        fieldcodes.splice(i,1);
                    }
                });
            }
        }
        fieldcodes.join(',');

        var combostore = combobox.getStore();
        combostore.proxy.extraParams.datanodeidAndFieldcodes = formview.resultgrid.nodeid+'∪'+fieldcodes;
        combostore.reload();

        var enumfieldCombo = formview.down('[itemId=enumfieldCombo]');
        enumfieldCombo.getStore().removeAll();
    },
    doBatchReplace:function (btn) {
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
        var batchModifyReplaceWin = Ext.create('Ext.window.Window',{
            width:'100%',
            height:'100%',
            title:'批量替换',
            // draggable : true,//可拖动
            resizable : false,//禁止缩放
            modal:true,
            closeToolText:'关闭',
            layout:'fit',
            items:[{
                xtype: 'batchModifyReplaceFormView',
                entryids:entryids,
                resultgrid:resultGrid,
                isSelectAll:isSelectAll,
                fromOutside:fromOutside
            }]
        });
        batchModifyReplaceWin.show();
        window.batchModifyReplaceWins = batchModifyReplaceWin;
        Ext.on('resize',function(a,b){
            window.batchModifyReplaceWins.setPosition(0, 0);
            window.batchModifyReplaceWins.fitContainer();
        });
    },
    doBatchAdd:function (btn) {
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
        var selectAll = resultGrid.down('[itemId=selectAll]').checked;
        var isSelectAll = false;
        if (selectAll) {
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
        var batchModifyAddWin = Ext.create('Ext.window.Window',{
            width:'100%',
            height:'100%',
            title:'批量增加',
            // draggable : true,//可拖动
            // resizable : false,//禁止缩放
            modal:true,
            closeToolText:'关闭',
            layout:'fit',
            items:[{
                xtype: 'batchModifyAddFormView',
                entryids:entryids,
                resultgrid:resultGrid,
                isSelectAll:isSelectAll,
                fromOutside:fromOutside
            }]
        });
        batchModifyAddWin.show();
        window.batchModifyAddWins = batchModifyAddWin;
        Ext.on('resize',function(a,b){
            window.batchModifyAddWins.setPosition(0, 0);
            window.batchModifyAddWins.fitContainer();
        });
    },
    //执行批量更新
    doBatchUpdate:function (btn) {
        var previewGrid = btn.up('batchModifyResultPreviewGrid');
        if(previewGrid.operateFlag=='批量修改'){
            this.doModifyBatchUpdate(btn);
        }
        if(previewGrid.operateFlag=='批量替换'){
            this.doReplaceBatchUpdate(btn);
        }
        if(previewGrid.operateFlag=='批量增加'){
            this.doAddBatchUpdate(btn);
        }
    },
    doModifyBatchUpdate:function (btn) {
        var resultPreviewGrid = btn.findParentByType('batchModifyResultPreviewGrid');
        var fieldData = resultPreviewGrid.fieldmodifydata.split('∩');//fieldcode∪fieldname∪fieldvalue
        var modifyDetail = '';
        for (var i = 0; i < fieldData.length; i++) {
            var data = fieldData[i].split('∪');
            modifyDetail += '['+data[1]+']设置为“'+data[2]+'”，';
        }
        modifyDetail = modifyDetail.substring(0, modifyDetail.length-1);
        var operateCount = resultPreviewGrid.formview.resultgrid.getSelectionModel().getSelectionLength();//此处操作记录条数为所有选定记录总条数
        var updateConfirmMsg = '本次操作将把['+resultPreviewGrid.formview.resultgrid.nodefullname+']所选记录的'+modifyDetail+',记录数：共'+operateCount+'条, 是否继续?';
        XD.confirm(updateConfirmMsg,function (){
            updateData(btn,'modi');
        },this);
    },
    doReplaceBatchUpdate:function (btn) {
        var resultPreviewGrid = btn.findParentByType('batchModifyResultPreviewGrid');
        var fieldmodifydatas = resultPreviewGrid.fieldreplacedata[0].split('∪');//[fieldcode_fieldname,addcontent,inserttype(或inserttype_insertplaceindex)]
        var fieldname = fieldmodifydatas[0].split('_')[1];
        var replaceDetail = '“'+fieldmodifydatas[1]+'”字符串';
        var ifcontainspace = resultPreviewGrid.ifcontainspace;
        if(ifcontainspace){
            replaceDetail+='及其前后空格值';
        }
        var replacecontent = fieldmodifydatas[2];
        if (replacecontent != '') {
            replaceDetail += '替换为“'+replacecontent+'”字符串';
        } else {
            replaceDetail += '替换为空字符串';
        }
        var operateCount = resultPreviewGrid.operateCount;//列表load完成后的记录总条数
        var updateConfirmMsg = '本次操作将把['+resultPreviewGrid.formview.resultgrid.nodefullname+']所选记录的['+fieldname+']里的'+replaceDetail+',记录数：共'+operateCount+'条, 是否继续?';
        if(operateCount == 0){
            XD.msg('未找到包含需替换内容的记录');
            return;
        }
        XD.confirm(updateConfirmMsg,function (){
            updateData(btn,'repl');
        },this);
    },
    doAddBatchUpdate:function (btn) {
        var resultPreviewGrid = btn.findParentByType('batchModifyResultPreviewGrid');
        var fieldmodifydatas = resultPreviewGrid.fieldmodifydata[0].split('∪');//[fieldcode_fieldname,searchcontent,replacement]
        var fieldname = fieldmodifydatas[0].split('_')[1];
        var addcontent = fieldmodifydatas[1];
        var insertPlaceInfo = fieldmodifydatas[2];
        var inserttype,placeindex;
        if(insertPlaceInfo.indexOf('_')!=-1){
            inserttype = insertPlaceInfo.split('_')[0];
            placeindex = insertPlaceInfo.split('_')[1];
        }else{
            inserttype = insertPlaceInfo;
        }
        var updateConfirmMsg;
        var operateCount = resultPreviewGrid.formview.resultgrid.getSelectionModel().getSelectionLength();//此处操作记录条数为所有选定记录总条数
        if(inserttype=='anywhere' && placeindex){
            updateConfirmMsg = '本次操作将在['+resultPreviewGrid.formview.resultgrid.nodefullname+']所选记录的['+fieldname+']的第'+placeindex+'位增加“'+addcontent+'”字符串，记录数：共'+operateCount+'条, 是否继续?';
        }
        if(inserttype=='front'){
            updateConfirmMsg = '本次操作将在['+resultPreviewGrid.formview.resultgrid.nodefullname+']所选记录的['+fieldname+']前面增加“'+addcontent+'”字符串，记录数：共'+operateCount+'条, 是否继续?';
        }
        if(inserttype=='behind'){
            updateConfirmMsg = '本次操作将在['+resultPreviewGrid.formview.resultgrid.nodefullname+']所选记录的['+fieldname+']后面增加“'+addcontent+'”字符串，记录数：共'+operateCount+'条, 是否继续?';
        }

        XD.confirm(updateConfirmMsg,function (){
            updateData(btn,'add');
        },this);
    },

    //监听键盘按下事件
    addKeyAction:function(view) {
        var controller=this;
        var currentView;

        // view.saveBtn=view.up('manageDirectoryFormView').down('[itemId=save]');
        document.onkeydown = function () {
            if(formlayout == 'formgrid'){
                currentView = view.up('manageDirectoryFormAndGridView').down('formAndGrid').down('manageDirectoryFormView');
            }else if(formlayout == 'forminnergrid'){
                currentView = view.up('manageDirectoryFormAndGridView').down('formAndInnerGrid').down('manageDirectoryFormView');
            }else if(formlayout == 'formview'){
                currentView = view.up('manageDirectoryFormAndGridView').down('formView').down('manageDirectoryFormView');
            }
            if(!view.saveBtn && currentView){
                view.saveBtn = currentView.down('[itemId=save]');
            }
            /*currentView = view.up('manageDirectoryFormView').down('save');

            if(!view.saveBtn && currentView){
                view.saveBtn=currentView.down('[itemId=save]');
                view.operateFlag=currentView.operateFlag;
            }*/
            var oEvent = window.event;
            if (oEvent.ctrlKey && !oEvent.shiftKey && !oEvent.altKey && oEvent.keyCode == 83) { //这里只能用alt，shift，ctrl等去组合其他键event.altKey、event.ctrlKey、event.shiftKey 属性
                // XD.msg('Ctrl+S');
                // controller.submitForm(view.saveBtn);//保存
                Ext.defer(function () {
                    if(view.saveBtn){
                        controller.submitForm(view.saveBtn);//保存
                    }
                },1);
                event.returnValue = false;//阻止event的默认行为
                // return false;//阻止event的默认行为
            }
        }
    },

    backAcceptHandler:function (btn) {
        var grid = this.getGrid(btn);
        var selectAll=grid.down('[itemId=selectAll]').checked;
        var tree = this.findGridView(btn).down('treepanel');
        var node = tree.selModel.getSelected().items[0];
        if (!node) {
            XD.msg('请选择节点');
            return;
        }
        if(grid.selModel.getSelectionLength() == 0){
            XD.msg('请至少选择一条需要退回的数据');
            return;
        }
        XD.confirm('确定要退回这' + grid.selModel.getSelectionLength() + '条数据吗',function(){
            Ext.MessageBox.wait('正在退回数据...','提示');
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
            Ext.Ajax.request({
                method: 'post',
                scope: this,
                url: '/manageDirectory/BackAcceptSubmit',
                params:tempParams,
                timeout:XD.timeout,
                success: function (response, opts) {
                    Ext.MessageBox.hide();
                    var responseText = Ext.decode(response.responseText);
                    if(responseText.success == true){
                        XD.msg(responseText.msg);
                        grid.getStore().reload();
                    }else{
                        XD.msg(responseText.msg+"，"+responseText.data);
                    }
                },
                failure : function() {
                    Ext.MessageBox.hide();
                    XD.msg('操作失败');
                }
            })
        },this);
    }
});

function delTempByUniqueType(type) {//清除本机当前用户关联的的临时条目数据
    Ext.Ajax.request({
        method: 'POST',
        params: {batchType: type},
        url: '/batchModify/delTempByUniqueType',
        async:false,
        success: function (response) {
        }
    });
}

function delTempByUniquetag() {
    Ext.Ajax.request({
        method: 'DELETE',
        url: '/batchModify/delTempByUniquetag',
        asych:false,
        success: function (response) {
        }
    })
}

function updateData(btn,batchtype) {
    var batchModifyResultPreviewWin = btn.up('window');
    var resultPreviewGrid = batchModifyResultPreviewWin.down('batchModifyResultPreviewGrid');
    var params = resultPreviewGrid.formview.resultgrid.getStore().proxy.extraParams;
    params['entryidArr']= resultPreviewGrid.formview.entryids.split(',');
    params['fieldModifyData'] = resultPreviewGrid.fieldmodifydata;
    params['fieldReplaceData'] = resultPreviewGrid.fieldreplacedata;
    params['flag'] = resultPreviewGrid.operateFlag;
    params['isSelectAll'] = resultPreviewGrid.formview.isSelectAll;
    params['type'] = '目录管理';
    params['batchtype'] = batchtype

    if(resultPreviewGrid.ifcontainspace!=undefined){
        params.ifContainSpace = resultPreviewGrid.ifcontainspace;
    }
    Ext.Msg.wait('正在执行'+resultPreviewGrid.operateFlag+'操作，请耐心等待……','正在操作');

    var columnArray = [];
    var columns = resultPreviewGrid.columnManager.getColumns();
    for (var j = 0; j < columns.length; j++) {
        if (columns[j].xtype === 'gridcolumn') {
            var subtext = columns[j].dataIndex + '-' + columns[j].text;
            columnArray.push(subtext);
        }
    }
    params['columnArray'] = columnArray;
    var downloadForm = document.createElement('form');
    document.body.appendChild(downloadForm);
    var inputTextElement;
    for (var prop in params){
        inputTextElement = document.createElement('input');
        inputTextElement.name = prop;
        inputTextElement.value = params[prop];
        downloadForm.appendChild(inputTextElement);
    }
    downloadForm.action = '/batchModify/export';
    downloadForm.method = "post";
    downloadForm.submit();
    Ext.Ajax.setTimeout(36000000);
    Ext.Ajax.request({
        url: '/batchModify/updateEntryindex',
        method: 'post',
        params: params,
        sync : true,
        //timeout:XD.timeout,
        success: function (resp) {
            var respText = Ext.decode(resp.responseText);
            batchModifyResultPreviewWin.hide();
            Ext.Msg.wait(resultPreviewGrid.operateFlag+'操作成功','正在操作').hide();
            XD.msg(respText.msg);
            resultPreviewGrid.formview.resultgrid.down('[itemId=selectAll]').setValue(false);
            Ext.apply(resultPreviewGrid.formview.resultgrid.getStore().getProxy().extraParams, {
                info: '',
                condition:resultPreviewGrid.formview.resultgrid.getStore().proxy.extraParams['basicCondition'],
                operator:resultPreviewGrid.formview.resultgrid.getStore().proxy.extraParams['basicOperator'],
                content:resultPreviewGrid.formview.resultgrid.getStore().proxy.extraParams['basicContent']
            });
            var managementgrid = resultPreviewGrid.formview.resultgrid;
            managementgrid.getStore().proxy.extraParams.entryidArr='';
            managementgrid.notResetInitGrid();
        },
        failure:function () {
            Ext.Msg.wait(resultPreviewGrid.operateFlag+'操作失败','正在操作').hide();
            XD.msg('更新失败');
        }
    })
}





