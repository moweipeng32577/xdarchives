/**
 * Created by Administrator on 2019/10/25.
 */
var win,duplicateflag;//数据查重之后用
var _certEncode;              //签名证书Base64编码
var _sealImageEncode;        //签章图片Base64编码  当前
var _sealImageEncode_trans;        //签章图片Base64编码 移交记录的签章
var _usrCertNO;//数字证书编号
var borrowdocData,isVolumeBtn=false;    //单据信息，案卷按钮或卷内按钮
var pwdOk=false;
Ext.define('TransforAuditDeal.controller.TransforAuditDealController',{
    extend: 'Ext.app.Controller',

    views:['TranforAuditDealView','AuditLookDocView', 'AuditFormView', 'AuditGridView',
        'ElectronicVersionView','AuditDocGridView', 'SendbackreasonFormView',
        'DuplicateCheckingSelectView','DuplicateCheckingGridView','ElectronicVersionGridView',
        'LongRetentionAcGridView','LongRetentionDetailView','AuditVolumeGridView'
    ],
    models:[
        'BatchModifyTemplatefieldModel', 'FieldModifyPreviewGridModel','BatchModifyTemplateEnumfieldModel'
        ,'ElectronicVersionGridModel','LongRetentionGridModel'
    ],
    stores:[
        'AuditDocGridStore',
        'BatchModifyTemplatefieldStore','FieldModifyPreviewGridStore',//批量操作
        'BatchModifyTemplateEnumfieldStore','DuplicateCheckingSelectStore','ElectronicVersionGridStore',
        'NextNodeStore','NextSpmanStore','ApproveOrganStore','LongRetentionGridStore'
    ],
    init:function(){
        var count = 0;
        this.control({
            'tranforAuditDealView': {
                render: function (view) {
                    Ext.Ajax.request({
                        url: '/audit/getDocByTaskId',
                        async:false,
                        params:{
                            state:"",
                            taskid:taskid
                        },
                        success : function(response) {
                            var data = Ext.decode(response.responseText).data;
                            if(data.volumenodeid==null||data.volumenodeid==""){
                                var auditVolumeGrid=view.down('auditVolumeGrid');
                                auditVolumeGrid.hide();
                                auditVolumeGrid.setDisabled(false);
                            }else {
                            }
                            borrowdocData=data;
                            var auditgrid = view.down('auditgrid');
                            var state=data.state;
                            if(state=="已审核"){
                                state="已入库"
                            }
                            auditgrid.initGrid({nodeid:data.nodeid,docid:data.docid,parententryid:"true",state:state});
                        },
                        failure: function() {
                            XD.msg('表单读取失败');
                        }
                    });
                    if(netcatUse=='1'){//启用签章
                        getUserCert();//没有证书编号的时候，读取一下
                    }
                },
                afterrender:function (view) {
                    if (type == '完成') {//当类型为'完成'时,界面只显示查看按钮
                        var buttons = view.down("toolbar").query('button');
                        var tbseparator = view.down("toolbar").query('tbseparator');
                        //隐藏除查看意外的按钮
                        hideToolbarBtnTbsByItemId(buttons,tbseparator);
                    }
                }
            },
            'auditVolumeGrid': {
                // itemclick:function(view, record){
                //     this.itemclickHandler(view,record,borrowdocData.nodeid);
                // },
                eleview: this.activeEleForm,
                afterrender:function (view) {
                    if (type == '完成') {//当类型为'完成'时,界面只显示查看按钮
                        var buttons = view.down("toolbar").query('button');
                        var tbseparator = view.down("toolbar").query('tbseparator');
                        //隐藏除查看意外的按钮
                        hideToolbarBtnTbsByItemId(buttons,tbseparator);
                    }
                }
            },
            'auditDocGridView [itemId=back]': {
                click: function (btn) {
                    if (flag == '1') {
                        parent.approve.close();
                    } else if (flag == "2") {
                        parent.auditApprove.close();
                    } else {
                        parent.closeObj.close(parent.layer.getFrameIndex(window.name));
                    }
                }
            },

            'tranforAuditDealView [itemId=lookDoc]':{
                click:function (btn) {
                    var docwin=new Ext.create('TransforAuditDeal.view.AuditLookDocView');
                    var sendform=docwin.items.get('formitemid');
                    sendform.load({
                        url: '/audit/getDocByTaskId',
                        async:false,
                        params:{
                            state:"",
                            taskid:taskid
                        },
                        success : function() {
                            docwin.show();
                        },
                        failure: function() {
                            XD.msg('表单读取失败');
                        }
                    });
                }
            },
            'auditLookDocView button[itemId=closeBtnID]':{
                click:function (btn) {
                    btn.findParentByType('auditLookDocView').close();
                }
            },
            'auditgrid [itemId=look]' : {
                click:function (view) {
                    isVolumeBtn=false;
                    this.lookHandler(view);
                }
            },
            'auditVolumeGrid [itemId=look]': {
                click:function (view) {
                    isVolumeBtn=true;
                    this.lookHandler(view);
                }
            },
            'auditgrid [itemId=modify]' : {
                click:function (view) {
                    isVolumeBtn = false;
                    this.modifyHandler(view)
                }
            },
            'auditVolumeGrid [itemId=modify]' : {
                click:function (view) {
                    isVolumeBtn = true;
                    this.modifyHandler(view)
                }
            },
            'auditgrid': {
                itemclick:function(view, record){
                    this.itemclickHandler(view,record,borrowdocData.volumenodeid);
                },
                eleview: this.activeEleForm,
                rowdblclick: function (view, record) {
                    var entryid = record.get('entryid');
                    var form = this.findFormView(view).down('dynamicform');
                    this.initFormField(form, 'hide', record.get('nodeid'));
                    this.initFormData('look',form, entryid);
                },
                afterrender:function (view) {
                    if (type == '完成') {//当类型为'完成'时,界面只显示查看按钮
                        var buttons = view.down("toolbar").query('button');
                        var tbseparator = view.down("toolbar").query('tbseparator');
                        //隐藏除查看意外的按钮
                        hideToolbarBtnTbsByItemId(buttons,tbseparator);
                    }
                }
            },
            'tranforAuditDealView [itemId=move]' : {//入库
                click:this.moveHandler
            },
            'tranforAuditDealView [itemId=sendback]' : {//退回
                click:this.sendbackHandler
            },
            'sendbackreasonFormView [itemId=affirmSendback]':{//确认退回
                click:this.affirmSendbackHandler
            },
            //表单操作-------------start
            'AuditFormView [itemId=save]' : {//保存
                click:function(btn){
                    this.submitForm(btn);
                }
            },
            'AuditFormView [itemId=preBtn]':{
                click:this.preHandler
            },
            'AuditFormView [itemId=nextBtn]':{
                click:this.nextHandler
            },
            'AuditFormView [itemId=continuesave]' : {//连续录入
                click:function(btn){
                    this.continueSubmitForm(btn);
                }
            },
            'AuditFormView [itemId=backs]' : {
                click:function(btn){
                    var electronic = btn.up('tranforAuditDealView').down('electronic treepanel').getStore();
                    electronic.proxy.extraParams.parentClassId = null;
                    var solid = btn.up('tranforAuditDealView').down('solid treepanel').getStore();
                    solid.proxy.extraParams.parentClassId = null;
                    this.activeGrid(btn, true);
                }
            },
            'tranforAuditDealView [itemId=back]' : {//返回
                click:function(btn){
                    if (flag == '1') {
                        parent.approve.close();
                    } else if (flag == "2") {
                        parent.auditApprove.close();
                    } else {
                        parent.closeObj.close(parent.layer.getFrameIndex(window.name));
                    }
                }
            },

            //结束表单操作-------------end
            'electronic [itemId=getEleVersion]': {//获取电子文件历史版本
                click: function (btn) {
                    var electronic = btn.findParentByType('electronic');
                    var eleTree = electronic.down('treepanel');
                    eleTree.getStore().proxy.extraParams.parentClassId = null;
                    var records = eleTree.getView().getChecked();
                    var recordList = [];
                    for (var i = 0; i < records.length; i++){
                        if (records[i].get('cls') == 'file') {//file表示是文件，folder表示文件夹
                            recordList.push(records[i]);//删除选中中非文件的分类文件夹节点。
                        }
                    }
                    if (recordList.length != 1) {
                        XD.msg('请选择一条需要查看历史版本的数据');
                        return;
                    }
                    var eleid = recordList[0].get('fnid');
                    var getVersionGridView = Ext.create('Ext.window.Window', {
                        width: 1000,
                        height: 600,
                        title: '电子文件历史版本',
                        draggable: true,//可拖动
                        resizable: false,//禁止缩放
                        modal: true,
                        closeToolText: '关闭',
                        layout: 'fit',
                        items: [{
                            xtype: 'electronicVersionGridView'
                        }],
                        listeners: {
                            "close": function () {
                                eleTree.getStore().reload();
                            }
                        }
                    });
                    var store = getVersionGridView.down('electronicVersionGridView').getStore();
                    store.proxy.extraParams.eleid = eleid;
                    store.load({callback:function(r,options,success){
                        if (r.length < 1){
                            XD.msg('无历史版本记录');
                        }else{
                            eleTree.on('load',function (node) {//当有历史版本时增加事件
                                var nodeItems = node.data.items;
                                for (var i = 0; i < nodeItems.length && recordList.length==1; i++){
                                    if (nodeItems[i].get('fnid') == recordList[0].get('fnid')){//把获取到的所有节点中选出上次操作时选中的节点。
                                        nodeItems[i].set('checked',true)//当刷新当前节点数据后仍然可以选中刷新数据前的节点
                                        this.getSelectionModel().select(nodeItems[i]);
                                        recordList = [];//清空recordList
                                        break;
                                    }
                                }
                            });
                            getVersionGridView.down('electronicVersionGridView').getSelectionModel().clearSelections();
                            getVersionGridView.eleTree = eleTree;
                            window.getVersionGridView = getVersionGridView;
                            getVersionGridView.show();
                        }
                    }});
                }
            },
            'electronicVersionGridView [itemId=lookeleVersion]': {//查看电子文件历史版本
                click: function (btn) {
                    var electronicVersionGridView = btn.findParentByType('electronicVersionGridView');
                    var select = electronicVersionGridView.getSelectionModel().getSelection();
                    if (select.length != 1) {
                        XD.msg("只能选择一条数据");
                        return;
                    }
                    var eleVersionid = select[0].get('id');
                    var fileName = select[0].get('filename');
                    window.eleVersionid = eleVersionid;
                    window.eletype = 'capture';
                    var electronicVersion = Ext.create("Ext.window.Window", {
                        width: '100%',
                        height: '100%',
                        title: '查看电子文件历史版本',
                        modal: true,
                        header: false,
                        draggable: false,//禁止拖动
                        resizable: false,//禁止缩放
                        closeToolText: '关闭',
                        layout: 'fit',
                        items: [{xtype: 'electronicVersion'}]
                    });
                    electronicVersion.down('electronicVersion').initData(eleVersionid,fileName);
                    electronicVersion.show();
                }
            },
            'electronicVersionGridView [itemId=deleleVersion]': {//删除电子文件历史版本
                click: function (btn) {
                    var electronicVersionGridView = btn.findParentByType('electronicVersionGridView');
                    var select = electronicVersionGridView.getSelectionModel().getSelection();
                    if (select.length == 0) {
                        XD.msg("请至少选择一条需要操作的数据");
                        return;
                    }
                    var eleVersionids = [];
                    var version = [];
                    for (var i = 0; i < select.length; i++) {
                        eleVersionids.push(select[i].get('id'));
                        version.push(select[i].get('version'));
                    }
                    // XD.confirm('确定要删除这 ' + select.length + ' 条数据吗?', function () {
                    XD.confirm('确定删除'+version+'历史版本的数据吗?', function () {
                        Ext.Ajax.request({
                            url: '/acquisition/delVersion',
                            params: {
                                eleVersionids: eleVersionids
                            },
                            success: function (response) {
                                var info = Ext.decode(response.responseText);
                                if (info.success) {
                                    XD.msg('成功删除' + info.data + '条数据');
                                    electronicVersionGridView.getStore().reload();
                                }
                            },
                            failure: function () {
                                XD.msg('操作失败');
                            }
                        });
                    }, this);
                }
            },
            'electronicVersionGridView [itemId=rebackVersion]': {//回滚电子文件历史版本
                click: function (btn) {
                    var electronicVersionGridView = btn.findParentByType('electronicVersionGridView');
                    var select = electronicVersionGridView.getSelectionModel().getSelection();
                    if (select.length != 1) {
                        XD.msg("只能选择一条需要操作的数据");
                        return;
                    }
                    var eleVersionid = select[0].get('id');
                    XD.confirm('确定回滚至'+select[0].get('version')+'版本吗？回滚后，当前版本将不可恢复，请先做好备份', function () {
                        Ext.Ajax.request({
                            url: '/acquisition/rebackVersion',
                            params: {
                                eleVersionid: eleVersionid
                            },
                            success: function (response) {
                                XD.msg('回滚成功');
                            },
                            failure: function () {
                                XD.msg('操作失败');
                            }
                        });
                    });
                }
            },
            'electronicVersionGridView [itemId=loadVersion]': {//下载电子文件历史版本
                click: function (btn) {
                    var electronicVersionGridView = btn.findParentByType('electronicVersionGridView');
                    var select = electronicVersionGridView.getSelectionModel().getSelection();
                    if(select.length == 0){
                        XD.msg("请至少选择一条需要操作的数据");
                        return;
                    }
                    var eleVersionids = [];
                    for(var i=0;i<select.length;i++){
                        eleVersionids.push(select[i].get('id'));
                    }
                    Ext.Ajax.request({
                        url: '/acquisition/ifVersionFileExist',
                        params: {
                            eleVersionids: eleVersionids
                        },
                        success: function (response) {
                            var responseText = Ext.decode(response.responseText);
                            if(responseText.success==true){
                                if(eleVersionids.length==1){
                                    var eleVersionid = eleVersionids[0];
                                    location.href = '/acquisition/downloadEleVersion/eleVersionid/'+ eleVersionid;
                                }else{
                                    location.href = '/acquisition/downloadEleVersion/eleVersionids/'+ eleVersionids;
                                }

                            }else{
                                XD.msg('下载失败！'+responseText.msg);
                                return;
                            }
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },
            'electronicVersionGridView [itemId=backVersion]': {//电子文件历史版本 返回
                click: function (btn) {
                    window.getVersionGridView.eleTree.getStore().reload();
                    window.getVersionGridView.close();
                }
            },
            //----------------------------------档号查重和档号对齐------------------------------------------//
            //档号对齐功能
            'tranforAuditDealView button[itemId=numberAlignment]': {click: this.doCodesettingAlign},

            //数据查重创建窗口
            'tranforAuditDealView button[itemId=dataDuplicate]': {click: this.doDuplicateChecking},
            //数据查重关闭窗口
            'duplicateCheckingSelectView button[itemId=close]': {
                click: function (btn) {
                    btn.findParentByType('duplicateCheckingSelectView').close();
                }
            },
            //数据查重的提交
            'duplicateCheckingSelectView button[itemId=submit]': {
                click: function (btn) {
                    var duplicateCheckingSelectWin = btn.findParentByType('duplicateCheckingSelectView');
                    var selectValue = duplicateCheckingSelectWin.down('[itemId=itemselectorID]').getValue();
                    if (selectValue.length === 0) {
                        XD.msg('请选择查重字段');
                        return;
                    }
                    var treeView = btn.findParentByType('duplicateCheckingSelectView').treeView;
                    //    var mainView = treeView.up('duplicateChecking');

                    var grid = treeView;
                    var formParams = {};
                    var fieldColumn = [];

                    formParams['multiValue'] = selectValue;
                    formParams['nodeid'] = treeView.nodeid;
                    formParams['docid'] = treeView.dataParams.docid;

                    duplicateCheckingSelectWin.hide();
                    if(fieldColumn.length==0){
                        win = Ext.create('Ext.window.Window',{
                            title:'数据查重结果',
                            layout:'card',activeItem:0,
                            bodyStyle : "padding:0px;",
                            height: '600px',
                            width: '1000px',
                            items:[
                                {
                                    xtype: 'duplicateCheckingGridView'
                                },
                                {
                                    xtype:'AuditFormView'
                                },
                                {
                                    xtype:'tranforAuditDealView'
                                }
                            ]
                        });
                        win.items.map.duplicateCheckingGridViewId.initGrid(formParams)
                        win.show();
                        duplicateflag=1;
                        win.on("close",function(){
                            duplicateflag=0;
                        });

                    }else{
                        grid.dataParams = formParams;
                        var store = grid.getStore();
                        Ext.apply(store.getProxy(),{
                            extraParams:formParams
                        });
                        store.loadPage(1);
                    }
                }
            },



            'duplicateCheckingGridView [itemId=edit]': {//数据查重之后的修改
                click: this.editHandler
            },
            'duplicateCheckingGridView [itemId=look]': {//数据查重之后的查看
                click: this.duplicateCheckinglook
            },
            'duplicateCheckingGridView [itemId=delete]': {//数据查重之后的删除
                click: this.duplicatedel
            },


            ///////////批量操作－－－－－－－－－－－－－－－start////////////////////////////
            'auditgrid [itemId=batchModify]':{//批量修改
                click:function (btn) {
                    isVolumeBtn=false;
                    this.doBatchModify(btn)
                }
            },
            'auditVolumeGrid [itemId=batchModify]':{//批量修改
                click:function (btn) {
                    isVolumeBtn=true;
                    this.doBatchModify(btn)
                }
            },
            'batchModifyModifyFormView [itemId=templatefieldCombo]':{
                render:this.loadModifyTemplatefieldCombo,
                select: this.loadModifyTemplateEnumfieldCombo
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
                    delTempByUniquetag();
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
                    params['type'] = '数据采集';
                    params['docid'] = resultPreviewGrid.formview.docid

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
                    var formview = btn.up('batchModifyModifyFormView');
                    var fieldModifyPreviewGrid = formview.down('grid');
                    fieldModifyPreviewGrid.getStore().removeAll();
                    btn.up('window').close();
                }
            },
            'batchModifyResultPreviewGrid button[itemId=batchUpdateBtn]':{//批量操作预览－执行批量更新
                click:this.doBatchUpdate
            },
            'batchModifyResultPreviewGrid button[itemId=backBtn]':{//批量操作预览－返回
                click:function (btn) {
                    delTempByUniquetag();
                    btn.up('batchModifyResultPreviewGrid').formview.fromOutside = false;
                    btn.up('window').hide();
                }
            },

            'auditgrid [itemId=batchReplace]':{//批量替换
                click:function (btn) {
                    isVolumeBtn=false;
                    this.doBatchReplace(btn);
                }
            },
            'auditVolumeGrid [itemId=batchReplace]':{//批量替换
                click:function (btn) {
                    isVolumeBtn=true;
                    this.doBatchReplace(btn);
                }
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
                    delTempByUniquetag();
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
                    params['type'] = '数据采集';

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
                    btn.up('window').close();
                }
            },
            'auditgrid [itemId=batchAdd]':{//批量增加
                click:function(btn){
                    isVolumeBtn=false;
                    this.doBatchAdd(btn);
                }
            },
            'auditVolumeGrid [itemId=batchAdd]':{//批量增加
                click:function(btn){
                    isVolumeBtn=true;
                    this.doBatchAdd(btn);
                }
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

                    var inserttypeAndPlaceindex, fieldModifyData;
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
                    delTempByUniquetag();
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
                    params['type'] = '数据采集';

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
                    btn.up('window').close();
                }
            },
            'longRetentionAcGridView [itemId=lookdetail]': {//查看验证明细
                click: this.lookdetailsHandler
            },
            'longRetentionAcGridView [itemId=transforTwo]': {//安全验证页 完成
                click: this.moveConfirm
            }
        })
    },
    //进入模块主页面时加载列表数据
    initGrid:function(view){
        // var tree = this.findGridView(view).down('treepanel');
        // var selectedNode = tree.selModel.getSelected().items[0];
        // if(selectedNode){
        //     return;
        // }
        // Ext.defer(function(){
        //     view.nodeid = tree.getStore().getRoot().firstChild.data.fnid;
        //     view.getStore().proxy.extraParams.nodeid = view.nodeid;//加载列表数据
        //     view.initColumns(view);
        //     view.getStore().reload();
        // },1);
    },
    // 进入模块主页面时加载检索条件默认值
    initSearchCondition:function(combo){
        // var tree = this.findGridView(combo).down('treepanel');
        // Ext.defer(function(){
        //     combo.nodeid = tree.getStore().getRoot().firstChild.data.fnid;
        //     combo.getStore().proxy.extraParams.nodeid = combo.nodeid;//加载列表数据
        //     combo.getStore().reload();
        // },2);
    },

    //获取数据审核应用视图
    findView:function(btn){
        return btn.findParentByType('tranforAuditDealView');
    },

    //获取表单界面视图
    findFormView:function(btn){
        if (duplicateflag){
            return win.down('AuditFormView');
        }
        return this.findView(btn).down('AuditFormView');
    },

    //获取列表界面视图
    findGridView:function(btn){
        return this.findView(btn).getComponent('gridview');
    },

    findInnerGrid:function(btn){
        if(isVolumeBtn){
            return this.findView(btn).down('auditVolumeGrid');
        }else {
            return this.findView(btn).down('auditgrid');
        }

    },

    //切换到列表界面视图
    activeGrid:function(btn, flag){
        var view = this.findView(btn);
        if (duplicateflag){
            win.setActiveItem(win.items.items[0])
        }else {
            view.setActiveItem(this.findGridView(btn));
        }
        if(document.getElementById('mediaFrame')){
            document.getElementById('mediaFrame').setAttribute('src','');
        }
        if(document.getElementById('solidFrame')){
            document.getElementById('solidFrame').setAttribute('src','');
        }
        // if (document.getElementById('longFrame')) {
        //     document.getElementById('longFrame').setAttribute('src', '');
        // }
        if(flag){//根据参数确定是否需要刷新数据
            var grid = this.findInnerGrid(btn);
            grid.notResetInitGrid();
        }
    },

    //切换到表单界面视图
    activeForm:function(btn, e){
        var view = this.findView(btn);
        var formview = this.findFormView(btn);
        view.setActiveItem(formview);
        formview.items.get(0).enable();
        formview.setActiveTab(0);
        return formview;
    },

    activeEleForm:function(obj){
        var view = this.findView(obj.grid);
        var formview = this.findFormView(obj.grid);
        view.setActiveItem(formview);
        formview.items.get(0).disable();
        var eleview = formview.down('electronic');
        var solidview  = formview.down('solid');
        eleview.operateFlag = "look"; //电子文件查看标识符
        solidview.operateFlag = "look";//利用文件查看标识符
        eleview.entrytype = 'capture';//改变公共form的entrytype
        solidview .entrytype = 'solid';
        eleview.initData(obj.entryid);
        solidview.initData(obj.entryid);
        var from =formview.down('dynamicform');
        //电子文件按钮权限
        var elebtns = eleview.down('toolbar').query('button');
        from.getELetopBtn(elebtns,eleview.operateFlag );
        var soildbtns = solidview.down('toolbar').query('button');
        from.getELetopBtn(soildbtns,solidview.operateFlag);
        formview.setActiveTab(1)
        return formview;
    },

    itemclickHandler: function (view, record,nodeid) {
        var auditgrid = view.findParentByType('tranforAuditDealView').down('auditVolumeGrid');
        auditgrid.nodeid = borrowdocData.nodeid;
        auditgrid.nodefullname =borrowdocData.nodefullname;
        var state=borrowdocData.state;
        if(state=="已审核"){
            state="已入库"
        }
        auditgrid.initGrid({docid:borrowdocData.docid,state:state, nodeid: nodeid,parententryid: record.get("entryid")});
    },

    //修改
    modifyHandler:function(btn){
        var grid = this.findInnerGrid(btn);
        var records = grid.selModel.getSelection();
        if(records.length == 0){
            XD.msg('请至少选择一条需要修改的数据');
            return;
        }
        var entryids = [];
        for(var i=0;i<records.length;i++){
            entryids.push(records[i].get('entryid'));
        }
        var entryid = records[0].get('entryid');
        var form = this.findFormView(btn).down('dynamicform');
        form.operate = 'modify';
        form.entryids = entryids;
        form.entryid = entryids[0];
        form.docid = grid.dataParams.docid;
        var initFormFieldState = this.initFormField(form, 'show', records[0].get('nodeid'));
        if(!initFormFieldState){//表单控件加载失败
            return;
        }
        this.initFormData(form, 'modify', entryid);
    },

    //查看卷内文件（数据审核模块无此需求）
    itemclickHandlerShowJN : function(view, record, item, index, e){
        var fileArchivecode = record.get('archivecode');//案卷档号
        var southgrid = this.findInnerGrid(view);
        southgrid.dataUrl = '/audit/entries/innerfile/'+fileArchivecode + '/';
        southgrid.on('reconfigure',function(grid,store){
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
        southgrid.initGrid({nodeid:this.getNodeid(record.get('nodeid'))});
        southgrid.parentXtype = 'audit';
        southgrid.formXtype = 'AuditFormView';
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
            XD.msg('请检查模板设置信息是否正确');
            return;
        }
        form.templates = formField;
        form.initField(formField,operate);//重新动态添加表单控件
//        }
        return '加载表单控件成功';
    },

    ifCodesettingCorrect:function (nodeid) {
        var codesetting = [];
        Ext.Ajax.request({
            url: '/codesetting/getCodeSettingFields',
            async:false,
            params:{
                nodeid:nodeid
            },
            success: function (response, opts) {
                if(Ext.decode(response.responseText).success==true){
                    codesetting = Ext.decode(response.responseText).data;
                }
            }
        });
        if(codesetting.length==0){
            return;
        }
        return '档号设置信息正确';
    },

    ifSettingCorrect:function (nodeid,templates) {
        var hasArchivecode = false;//表单字段是否包含档号（archivecode）
        Ext.each(templates,function (item) {
            if(item.fieldcode=='archivecode'){
                hasArchivecode = true;
            }
        });
        if(hasArchivecode){//若表单字段包含档号，则判断档号设置是否正确
            var codesettingState = this.ifCodesettingCorrect(nodeid);
            if(!codesettingState){
                XD.msg('请检查档号设置信息是否正确');
                return;
            }
        }
        return '档号设置正确';
    },

    changeBtnStatus:function(form, operate){
        var savebtn = this.findFormView(form).down('[itemId=save]');
        var tbseparator = this.findFormView(form).getDockedItems('toolbar')[0].query('tbseparator');
        if (operate == 'look') {//查看时隐藏保存按钮
            savebtn.setVisible(false);
            tbseparator[0].setVisible(false);
        } else if (operate == 'modify') {
            savebtn.setVisible(true);
            tbseparator[0].setVisible(true);
        }
    },
    getCurrentAuditform:function (btn) {
        return btn.up('AuditFormView');
    },

    //点击上一条
    preHandler:function(btn){
        if (duplicateflag) {
            var form = win.down('dynamicform');
        }else {
            var currentAuditform = this.getCurrentAuditform(btn);
            var form = currentAuditform.down('dynamicform');
        }
        this.preNextHandler(form, 'pre');
    },

    //点击下一条
    nextHandler:function(btn){
        if (duplicateflag) {
            var form = win.down('dynamicform');
        }else {
            var currentAuditform = this.getCurrentAuditform(btn);
            var form = currentAuditform.down('dynamicform');
        }
        this.preNextHandler(form, 'next');
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
            this.initFormData(form, form.operate, entryid);
            return;
        }
        this.initFormData(form, 'look', entryid);
    },

    initFormData:function(form, operate, entryid){
        form.type = '数据审核';
        var formview = form.up('AuditFormView');
        var nullvalue = new Ext.data.Model();
        var fields = form.getForm().getFields().items;
        if(operate == 'modify' || operate == 'look') {
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
        var currentForm = this.findFormView(form);
        var etips = currentForm.down('[itemId=etips]');
        etips.show();
        var btns = currentForm.down('[itemId=tips]');
        if(operate!='look'){
            var settingState = this.ifSettingCorrect(form.nodeid,form.templates);
            if(!settingState){
                return;
            }
            btns.show();
            Ext.each(fields,function (item) {
                if(!item.freadOnly){
                    item.setReadOnly(false);
                }
            });
        } else {
            btns.hide();
            Ext.each(fields,function (item) {
                item.setReadOnly(true);
            });
        }
        if (!duplicateflag) {
            this.activeForm(form);
        }
        if (duplicateflag){
            var eleview = win.down('AuditFormView').down('electronic');
            var solidview = win.down('AuditFormView').down('solid');
        }else {
            var eleview = this.findFormView(form).down('electronic');
            var solidview = this.findFormView(form).down('solid');
        }
        eleview.operateFlag = operate; //电子文件查看标识符
        solidview.operateFlag =operate;//利用文件查看标识符
        Ext.Ajax.request({
            method: 'GET',
            scope: this,
            url: '/audit/entries/' + entryid+"/"+type,
            success: function (response, opts) {
                var entry = Ext.decode(response.responseText);
                form.loadRecord({getData: function () {return entry;}});
                //字段编号，用于特殊的自定义字段(范围型日期)
                var fieldCode = form.getRangeDateForCode();
                if (fieldCode != null) {
                    //动态解析数据库日期范围数据并加载至两个datefield中
                    form.initDaterangeContent(entry);
                }
                eleview.entrytype = 'capture';
                if(type=="完成"){
                    eleview.entrytype = 'manage';
                }
                eleview.initData(entry.entryid);
                solidview.entrytype = 'solid';
                solidview.initData(entry.entryid);
            }
        });
        form.fileLabelStateChange(eleview,operate);
        form.fileLabelStateChange(solidview,operate);
        this.changeBtnStatus(form,operate);
    },

    //条目切换，上一条下一条
    preNextHandler:function(form,type){
        var dirty = !!form.getForm().getFields().findBy(function(f){
            return f.wasDirty;
        });
        if(form.operate == 'modify' && dirty){
            XD.confirm('数据已修改，确定保存吗？',function() {
                //保存数据
                var formview = this.form;
                var nodename = this.ref.getNodename(formview.nodeid);
                var params={
                    nodeid: formview.nodeid,
                    type: formview.findParentByType('AuditFormView').operateFlag,
                    eleid: formview.findParentByType('AuditFormView').down('electronic').getEleids(),
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
                    url: '/acquisition/entries',
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
            },{
                ref:this,
                form:form,
                type:type
            },function(){
                this.ref.refreshFormData(this.form, this.type)
            });
        }else{
            this.refreshFormData(form, type);
        }
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

    //查看
    lookHandler:function(btn){
        var grid = this.findInnerGrid(btn);
        var records = grid.selModel.getSelection();
        if(records.length == 0){
            XD.msg('请至少选择一条需要查看的数据');
            return;
        }
        var entryids = [];
        for(var i=0;i<records.length;i++){
            entryids.push(records[i].get('entryid'));
        }
        var entryid = records[0].get('entryid');
        var form = this.findFormView(btn).down('dynamicform');
        var initFormFieldState = this.initFormField(form, 'hide', records[0].get('nodeid'));
        form.operate = 'look';
        form.entryids = entryids;
        form.entryid = entryids[0];
        if(!initFormFieldState){//表单控件加载失败
            return;
        }
        this.initFormData(form, 'look', entryid);
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

    updateSubsequentData:function (entryid,operate,pages) {
        var state;
        var params = {
            entryid:entryid,
            flag:operate
        };
        if(pages!=undefined){
            params.pages = pages;
        }
        Ext.Ajax.request({
            url: '/acquisition/updateSubsequentData',
            async:false,
            params:params,
            success: function (resp) {
                var resp = Ext.decode(resp.responseText);
                if(resp.success==false){
                    Ext.MessageBox.alert("提示信息", resp.msg, function(){
                    });
                }else{
                    state = '更新后续数据成功';
                }
            }
        });
        return state;
    },

    //保存表单数据，返回列表界面视图
    submitForm: function (btn) {
        var formView = this.findFormView(btn);
        var eleids = formView.down('electronic').getEleids();
        var formview = formView.down('dynamicform');

        //bug #2928判断对象是否为空。(点击列表原文查看电子文件的from保存按钮。 不知道有什么需求，暂时返回grid界面)
        var formValues  = formview.getValues();
        var data = {};
        var arr = Object.keys(formValues);
        if(arr.length == 0){
            this.activeGrid(btn,true);
        }
        else {
            var fieldCode = formview.getRangeDateForCode();//字段编号，用于特殊的自定义字段(范围型日期)
            var nodename = this.getNodename(formview.nodeid);
            var params = {
                nodeid: formview.nodeid,
                eleid: eleids,
                type: formView.operateFlag,
                operate: nodename
            };
            if (fieldCode != null) {
                params[fieldCode] = formview.getDaterangeValue();
            }
            var archivecodeSetState = formview.setArchivecodeValueWithNode(nodename);
            if (!archivecodeSetState) {//若档号设置失败，则停止后续的表单提交
                return;
            }
            var operateType = formview.operateType;
            var submitType = formview.submitType;
            Ext.MessageBox.wait('正在保存请稍后...', '提示');
            formview.submit({
                method: 'POST',
                url: '/acquisition/entries',
                params: params,
                scope: this,
                success: function (form, action) {
                    Ext.MessageBox.hide();
                    var nodeid=formview.nodeid
                    var pages = action.result.data.pages;
                    if (action.result.success == true) {
                        if (operateType == 'insertion') {
                            var state = this.updateSubsequentData(this.entryID, submitType, pages);
                            //切换到列表界面,同时刷新列表数据(判断树节点nodeid是否和表单指定的nodeid)
                            if (formview.nodeid != nodeid) {
                                this.activeGrid(btn, false);
                                this.findInnerGrid(btn).getStore().reload();
                            } else {
                                this.activeGrid(btn, true);
                                this.findInnerGrid(btn).getStore().removeAll();
                            }
                            if (!state) {
                                return;//保存条目成功，但插件后更新后续数据计算项及档号失败
                            }
                            //XD.msg(action.result.msg);//避免两个提示同时出现
                        }
                        //多条时切换到下一条。单条时或最后一条时切换到列表界面,同时刷新列表数据
                        if (formview.entryids && formview.entryids.length > 1 && formview.entryid != formview.entryids[formview.entryids.length - 1]) {
                            this.refreshFormData(formview, 'next');
                        } else {
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
            if (formview.operateType) {
                formview.operateType = undefined;
            }
        }
    },

    //入库
    moveHandler:function(btn){
        var editcasign='0';
        if(_certEncode){//有证书编号
            editcasign='1';
        }
        var grid = btn.findParentByType('tranforAuditDealView');
        var docid = borrowdocData.docid;
        window.docid=docid.trim();
        var state = borrowdocData.state;
        var entryExistState = this.checkEntryExistState(docid,borrowdocData.nodeid,'入库',state);
        var Node = grid.down('[itemId=nextNodeId]');
        var nextNode = grid.down('[itemId=nextNodeId]').getValue();
        var nextSpman = grid.down('[itemId=nextSpmanId]').getValue();
        if(!entryExistState){//单据所关联的条目已被删除
            return;
        }

        if(Node.store.data.items[0].data.orders==3&&netcatUse=='1'){//政务网 流程的第二步 立档单位领导审核  进行签章保存后在移交
            var signPrint=0;//移交签章标记  0没有 1有
            if(_certEncode) {//有数字证书的才进行报表签章
                /*//获取签章base64编码
                getUserSealImage();*/
                if(_sealImageEncode&&_sealImageEncode.length>200){//有移交签章才进行签章打印
                    signPrint=1;
                }
            }
            if(_certEncode&&signPrint==1) {//有证书存在且有签章存在
                XD.confirm('移交前需要先进行移交签章？',function(){
                    //根据移交单据的标记，先判断有没有相关的签章内容，有的话就先生成pdf，然后对pdf签章，最后再打开pdf
                    Ext.Ajax.request({
                        url: '/audit/updateTrandoc',
                        params: {
                            docid: docid.trim(),
                            type: "1"
                        },
                        method: 'POST',
                        async: false,
                        success: function (resp) {
                        }
                    });
                    Ext.Ajax.request({//获取pdf报表的base64编码
                        url: '/acquisition/getUreportPdf',
                        params:{
                            docids:docid.trim(),
                            reportName:encodeURI('移交单据管理_未打钩')
                        },
                        method: 'POST',
                        success: function (resp) {//得到pdf报表的base64编码
                            XD.msg(Ext.decode(resp.responseText).msg);
                            _srcBytes= Ext.decode(resp.responseText).data;//报表的base64编码
                            // 对PDF签章，签章成功后再打开pdf页面
                            seal_SignSealPosition(_srcBytes,200,682,89,_sealImageEncode,1,3);

                            //签章完后在进行移交
                            XD.confirm('已签章，是否确定移交？',function(){
                                if(pwdOk) {
                                    auditToNext(docid, borrowdocData.nodeid, taskid, nextNode, nextSpman, _usrCertNO);
                                }else{
                                    XD.msg('请输入正确密码');
                                }
                            });

                        },
                        failure:function(){
                            XD.msg('表单生成失败');
                        }
                    });
                });
            }else{
                XD.msg("移交单位的档案到档案馆时需要用数字证书进行移交单签章");
                return;
            }
        }else if(Node.store.data.items[0].data.orders==4){//流程的第三步 档案馆档案员接收 需要先进行四性验证
            //移交前先进性四性验证
            XD.confirm('接收立档单位移交的档案时需要对这些数据进行四性验证', function () {
                Ext.MessageBox.wait('正在进行数据包安全认证...', '提示');
                XD.msg('执行验证成功');
                Ext.MessageBox.hide();
                var fsWin = Ext.create('Ext.window.Window',{
                    width:'80%',
                    height:'80%',
                    title: '安全验证结果',
                    draggable : false,//禁止拖动
                    resizable : false,//禁止缩放
                    modal:true,
                    closeToolText:'关闭',
                    closeAction:'hide',
                    layout:'fit',
                    items:[{
                        xtype: 'longRetentionAcGridView',
                        docid: docid.trim(),
                        nodeid: borrowdocData.nodeid,
                        nextNode: nextNode,
                        nextSpman: nextSpman
                    }]
                });
                var fsGrid = fsWin.down('longRetentionAcGridView');
                fsGrid.initGrid({docid:docid.trim()});
                fsWin.show();
            }, this);
        }else if(Node.store.data.items[0].data.orders==captureAuditSize&&netcatUse=='1') {//政务网 流程的第4步 档案馆签章  进行签章保存后再入库
            var signPrint=0;//移交签章标记  0没有 1有
            if(_certEncode) {//有数字证书的才进行报表签章
                /*//获取签章base64编码
                 getUserSealImage();*/
                if(_sealImageEncode&&_sealImageEncode.length>200){//有移交签章才进行签章打印
                    signPrint=1;
                }
            }
            if(_certEncode&&signPrint==1) {//有证书存在且有签章存在
                XD.confirm('入库前需要先进行审核签章？',function(){
                    Ext.Ajax.request({
                        url: '/audit/updateTrandoc',
                        params: {
                            docid: docid.trim(),
                            type: "2"
                        },
                        method: 'POST',
                        async: false,
                        success: function (resp) {
                        }
                    });
                    getSigncode(docid.trim());//获取移交签章
                    Ext.Ajax.request({//获取pdf报表的base64编码
                        url: '/acquisition/getUreportPdf',
                        params:{
                            docids:docid.trim(),
                            reportName:encodeURI('移交单据管理')
                        },
                        method: 'POST',
                        success: function (resp) {//得到pdf报表的base64编码
                            XD.msg(Ext.decode(resp.responseText).msg);
                            _srcBytes= Ext.decode(resp.responseText).data;//报表的base64编码
                            // 对报表的base64吗进行签章，签章成功后对返回的base64码再用当前的数字证书签章进行审核签章
                            seal_SignSealPosition(_srcBytes,200,682,89,_sealImageEncode_trans,1,5);
                            //签章完后在进行移交
                            XD.confirm('已签章，是否确定移交？',function(){
                                if(pwdOk) {
                                    auditToNext(docid, borrowdocData.nodeid, taskid, nextNode, nextSpman, _usrCertNO);
                                }else{
                                    XD.msg('请输入正确密码');
                                }
                            });

                        },
                        failure:function(){
                            XD.msg('表单生成失败');
                        }
                    });


                    //签章完后在进行入库
                    XD.confirm('已签章，是否确定入库？',function(){
                        auditToNext(docid,borrowdocData.nodeid,taskid,nextNode,nextSpman);
                    });
                });
            }else{
                XD.msg("移交单位的档案入库时需要用数字证书进行审核签章");
                return;
            }
        }else{//直接审批进入下一步
            if(Node.store.data.items[0].data.orders==5){  //政务网 流程的第四步 档案馆领导审核

            }
            XD.confirm('是否确定完成？',function(){
                auditToNext(docid,borrowdocData.nodeid,taskid,nextNode,nextSpman);
            });
        }
    },

    lookdetailsHandler: function (btn) {//查看详细
        var grid = btn.up('longRetentionAcGridView');
        var record = grid.selModel.getSelection();
        if (record.length == 0) {
            XD.msg('请选择一条需要查看的数据');
            return;
        }
        if (record.length > 1) {
            XD.msg('只能选择一条数据');
            return;
        }
        if (record[0].data.authenticity.indexOf("未检测") > -1 || record[0].data.authenticity == '') {
            XD.msg('该记录未检测，无法查看检测明细！');
            return;
        }
        var win = Ext.create('TransforAuditDeal.view.LongRetentionDetailView');
        win.down('[itemId=closeBtn]').on('click', function () {
            win.close()
        });
        win.down('[itemId=authenticity]').html = record[0].data.authenticity;
        win.down('[itemId=integrity]').html = record[0].data.integrity;
        win.down('[itemId=usability]').html = record[0].data.usability;
        win.down('[itemId=safety]').html = record[0].data.safety;
        win.show();
    },

    moveConfirm: function (btn) {//验证后完成审核移交
        var grid = btn.up('longRetentionAcGridView');
        XD.confirm('是否确定完成？',function(){
            Ext.Ajax.request({
                url: '/audit/updateTrandoc',
                params: {
                    docid: docid.trim(),
                    type: "0"
                },
                method: 'POST',
                async:false,
                success: function (resp) {
                }
            });
            auditToNext(grid.docid,grid.nodeid,taskid,grid.nextNode,grid.nextSpman);
        });
    },

    //数据查重的窗口创建
    doDuplicateChecking: function (btn) {
        var treeView = btn.findParentByType('tranforAuditDealView').down('[itemId=onlygrid]');
        if(treeView.store.data.length>100000){
            XD.msg('当前数据量过大无法使用该功能');
            return;
        }
        var selectWin = Ext.create('TransforAuditDeal.view.DuplicateCheckingSelectView',{treeView: treeView});
        selectWin.items.get(0).store.proxy.extraParams = {nodeid: treeView.nodeid};
        selectWin.items.get(0).getStore().load(function () {
            selectWin.items.get(0).setValue();
        });
        selectWin.show();
        window.WselectWin = selectWin;
    },

    editHandler: function (btn) {
        formvisible = true;
        formlayout = 'formview';
        var acquisitionform = win.down('AuditFormView');
        acquisitionform.down('electronic').operateFlag='modify';
        acquisitionform.operateFlag = 'modify';
        var grid = btn.findParentByType('duplicateCheckingGridView');
        var form = acquisitionform.down('dynamicform');
        var records = grid.selModel.getSelection();
        var selectCount = records.length;
        // var selectAll = grid.down('[itemId=selectAll]').checked;
        // if(selectAll){
        //     XD.msg('不支持选择所有页修改');
        //     return;
        // }
        if (selectCount == 0) {
            XD.msg('请至少选择一条需要修改的数据');
            return;
        }
        var initFormFieldState = this.initFormField(form, 'show', records[0].data.nodeid);
        if (typeof(initFormFieldState) != 'undefined') {
            var entryids = [];
            for(var i=0;i<records.length;i++){
                entryids.push(records[i].get('entryid'));
            }
            form.operate = 'modify';
            form.entryids = entryids;
            form.entryid = entryids[0];
            this.initFormData(form, 'modify', entryids[0]);
            this.duplicateactiveInnerForm(form);
            if(form.operateType){
                form.operateType = undefined;
            }
        }
    },

    //档号查重结果页面的删除
    duplicatedel: function (btn) {
        var grid = win.getLayout().getActiveItem()
        //    var selectAll=grid.down('[itemId=selectAll]').checked;
        if(grid.selModel.getSelectionLength() == 0){
            XD.msg('请至少选择一条需要删除的数据');
            return;
        }
        XD.confirm('确定要删除这' + grid.selModel.getSelectionLength() + '条数据吗',function(){
            Ext.MessageBox.wait('正在删除数据...','提示');
            var record = grid.selModel.getSelection();
            var isSelectAll = false;
            // if(selectAll){
            //     record = grid.acrossDeSelections;
            //     isSelectAll = true;
            // }
            var tmp = [];
            for (var i = 0; i < record.length; i++) {
                tmp.push(record[i].get('entryid'));
            }
            var entryids = tmp.join(",");
            grid.getStore().proxy.url='/duplicateChecking/auditFindBySearch';
            var tempParams = grid.getStore().proxy.extraParams;
            tempParams['entryids'] = entryids;
            tempParams['isSelectAll'] = isSelectAll;
            Ext.Msg.wait('正在删除数据，请耐心等待……', '正在操作');
            Ext.Ajax.request({
                method: 'post',
                scope: this,
                url: '/audit/delete',
                params:tempParams,
                timeout:XD.timeout,
                success: function (response, opts) {
                    XD.msg(Ext.decode(response.responseText).msg);
                    grid.getStore().proxy.extraParams.entryids='';
                    grid.delReload(grid.selModel.getSelectionLength());
                    Ext.MessageBox.hide();
                },
                failure : function() {
                    Ext.MessageBox.hide();
                    XD.msg('操作失败');
                }
            })
        },this);
    },

    //数据查重之后的切换到单个表单界面视图
    duplicateactiveInnerForm: function (form) {
        var acquisitionform = win.down('AuditFormView')
        win.setActiveItem(acquisitionform);
        acquisitionform.items.get(0).enable();
        acquisitionform.setActiveTab(0);
    },

    //数据查重结果的查看
    duplicateCheckinglook: function (btn) {
        var grid = btn.findParentByType('duplicateCheckingGridView');
        var form = win.down('AuditFormView').down('dynamicform');
        var records = grid.selModel.getSelection();
        var selectCount = records.length;

        if(selectCount == 0){
            XD.msg('请至少选择一条需要查看的数据');
            return;
        }
        var entryids = [];
        for(var i=0;i<records.length;i++){
            entryids.push(records[i].get('entryid'));
        }
        var initFormFieldState = this.initFormField(form, 'show', records[0].data.nodeid);
        form.operate = 'look';
        form.entryids = entryids;
        form.entryid = entryids[0];
        this.initFormData(form,'look', entryids[0]);
        this.duplicateactiveInnerForm(form);
    },

    //退回
    sendbackHandler:function(btn){
        var grid = btn.findParentByType('tranforAuditDealView');
        var docid = borrowdocData.docid;
        var state = borrowdocData.state;
        var entryExistState = this.checkEntryExistState(docid,borrowdocData.nodeid,'退回',state);
        if(!entryExistState){//单据所关联的条目已被删除
            return;
        }
        XD.confirm('是否确定退回？',function(){
            var sendbackreasonWin = Ext.create('Ext.window.Window',{//弹出窗口，填写退回原因
                width:900,
                height:300,
                title:'退回确认',
                draggable : true,//可拖动
                // resizable : false,//禁止缩放
                modal:true,
                closeToolText:'关闭',
                layout:'fit',
                items:[{
                    xtype:'sendbackreasonFormView',
                    grid:grid
                }]
            });
            sendbackreasonWin.show();
        });
    },
    affirmSendbackHandler:function (btn) {
        var editcasign='0';
        if(_certEncode){//有证书编号
            editcasign='1';
        }
        var sendbackreasonWin = btn.up('window');
        var sendbackreasonForm = sendbackreasonWin.down('sendbackreasonFormView');
        var sendbackreason = sendbackreasonForm.getForm().findField('sendbackreason').getValue();
        if(!sendbackreason){
            XD.msg('请填写退回原因');
            return;
        }
        Ext.MessageBox.wait('正在执行退回请稍后...','提示');
        Ext.Ajax.request({
            url:'/audit/sendback',
            params:{
                docid:borrowdocData.docid,
                sendbackreason:sendbackreason,
                taskid:taskid
            },
            success:function(response){
                Ext.MessageBox.hide();
                sendbackreasonWin.close();
                XD.msg(Ext.decode(response.responseText).msg);
                Ext.defer(function () {
                    if (flag == '1') {
                        parent.wgridView.notResetInitGrid({state:'待处理',type:'采集移交审核'});
                        parent.approve.close();
                    } else if (flag == "2") {
                        parent.auditApprove.docGrid.getStore().reload();
                        parent.auditApprove.close();
                    } else {
                        parent.closeObj.close(parent.layer.getFrameIndex(window.name));
                    }
                },1000);
            },failure: function (response) {
                Ext.MessageBox.hide();
                XD.msg("退回出错");
            }
        })
    },
    //检验单据所关联的条目是否存在
    checkEntryExistState:function (docid,nodeid,operate,state) {
        var existEntryCount;
        Ext.Ajax.request({
            url: '/audit/entries',
            method: 'GET',
            async:false,
            params:{
                docid:docid,
                nodeid:nodeid,
                state:state,
                page:1,start:0,limit:XD.pageSize,//手动传入分页参数
            },
            success: function (response) {
                existEntryCount = Ext.decode(response.responseText).numberOfElements;
            }
        });
        if(existEntryCount==0){
            XD.msg('该单据所关联的条目数据已被删除，'+operate+'操作失败');
            return;
        }else{
            return '条目存在';
        }
    },
    //批量修改
    doBatchModify:function (btn) {
        var resultGrid = this.findInnerGrid(btn);
        var records = resultGrid.getSelectionModel().getSelection();
        var selectCount = resultGrid.getSelectionModel().getSelectionLength();
        if(selectCount==0){
            XD.msg('请选择数据');
            return;
        }
        resultGrid.nodeid = records[0].get("nodeid");
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
        var params  = resultGrid.getStore().proxy.extraParams;
        if(typeof(params.content)!=='undefined'){
            fromOutside = true;
        }
        var batchModifyModifyWin = Ext.create('Ext.window.Window',{
            width:'100%',
            height:'100%',
            title:'批量修改',
            // draggable : true,//可拖动
            modal:true,
            closeToolText:'关闭',
            layout:'fit',
            items:[{
                xtype: 'batchModifyModifyFormView',
                docid:resultGrid.getStore().proxy.extraParams['docid'],
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
        window.batchModifyModifyWins = batchModifyModifyWin;
        Ext.on('resize',function(a,b){
            window.batchModifyModifyWins.setPosition(0, 0);
            window.batchModifyModifyWins.fitContainer();
        });
    },

    loadModifyTemplateEnumfieldCombo: function (view) {//加载批量修改form的下拉框(处理枚举值)
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

    loadModifyTemplatefieldCombo:function (view) {//加载批量修改form的下拉框
        var combostore = view.getStore();
        var batchModifyModifyFormView = view.up('batchModifyModifyFormView');
        combostore.proxy.extraParams.datanodeidAndFieldcodes = batchModifyModifyFormView.resultgrid.nodeid;
        combostore.load();
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
        var existedDataArr = [];
        if(existedData.length>0){
            Ext.each(existedData,function (item) {
                existedDataArr.push(item.get('fieldcode')+'∪'+item.get('fieldname')+'∪'+item.get('fieldvalue'));
            });
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
        var resultGrid = this.findInnerGrid(btn);
        var records = resultGrid.getSelectionModel().getSelection();
        var selectCount = resultGrid.getSelectionModel().getSelectionLength();
        if(selectCount==0){
            XD.msg('请选择数据');
            return;
        }
        resultGrid.nodeid = records[0].get("nodeid");
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
        var params  = resultGrid.getStore().proxy.extraParams;
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
        var resultGrid = this.findInnerGrid(btn);
        var records = resultGrid.getSelectionModel().getSelection();
        var selectCount = resultGrid.getSelectionModel().getSelectionLength();
        if(selectCount==0){
            XD.msg('请选择数据');
            return;
        }
        resultGrid.nodeid = records[0].get("nodeid");
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
            modifyDetail += '[' + data[1] + ']设置为“' + data[2] + '”，';
        }
        modifyDetail = modifyDetail.substring(0, modifyDetail.length - 1);
        var operateCount = resultPreviewGrid.formview.resultgrid.getSelectionModel().getSelectionLength();//此处操作记录条数为所有选定记录总条数
        if(parent.auditApprove){
            var updateConfirmMsg = '本次操作将把[' + parent.auditApprove.docGrid.getSelectionModel().getSelection()[0].get('nodefullname') + ']所选记录的' + modifyDetail + ',记录数：共' + operateCount + '条, 是否继续?';
        }else if(parent.approve){
            var updateConfirmMsg = '本次操作将把[' + parent.approve.docGrid.getSelectionModel().getSelection()[0].get('nodefullname') + ']所选记录的' + modifyDetail + ',记录数：共' + operateCount + '条, 是否继续?';
        }else if(resultPreviewGrid.formview.resultgrid.nodefullname){
            var updateConfirmMsg = '本次操作将把[' + resultPreviewGrid.formview.resultgrid.nodefullname + ']所选记录的' + modifyDetail + ',记录数：共' + operateCount + '条, 是否继续?';
        }else{
            var fullName;
            Ext.Ajax.request({
                url: '/audit/getNodeFullName',
                async:false,
                methods:'get',
                params:{
                    nodeid:resultPreviewGrid.formview.resultgrid.dataParams.nodeid
                },
                success: function (response) {
                    fullName=response.responseText;
                }
            });
            var updateConfirmMsg = '本次操作将把[' + fullName + ']所选记录的' + modifyDetail + ',记录数：共' + operateCount + '条, 是否继续?';
        }

        XD.confirm(updateConfirmMsg, function () {
            updateData(btn,"modi");
        }, this);
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
        replaceDetail+='替换为“'+replacecontent+'”字符串';
        var operateCount = resultPreviewGrid.operateCount;//列表load完成后的记录总条数

        if(parent.auditApprove){
            var updateConfirmMsg = '本次操作将把[' + parent.auditApprove.docGrid.getSelectionModel().getSelection()[0].get('nodefullname') + ']所选记录的' + replaceDetail + ',记录数：共' + operateCount + '条, 是否继续?';
        }else if(parent.approve){
            var updateConfirmMsg = '本次操作将把[' + parent.approve.docGrid.getSelectionModel().getSelection()[0].get('nodefullname') + ']所选记录的' + replaceDetail + ',记录数：共' + operateCount + '条, 是否继续?';
        }else if(resultPreviewGrid.formview.resultgrid.nodefullname){
            var updateConfirmMsg = '本次操作将把[' + resultPreviewGrid.formview.resultgrid.nodefullname + ']所选记录的' + replaceDetail + ',记录数：共' + operateCount + '条, 是否继续?';
        }else{
            var fullName;
            Ext.Ajax.request({
                url: '/audit/getNodeFullName',
                async:false,
                methods:'get',
                params:{
                    nodeid:resultPreviewGrid.formview.resultgrid.dataParams.nodeid
                },
                success: function (response) {
                    fullName=response.responseText;
                }
            });
            var updateConfirmMsg = '本次操作将把[' +fullName+ ']所选记录的' + replaceDetail + ',记录数：共' + operateCount + '条, 是否继续?';

        }
        if(operateCount===0){
            XD.msg('未找到包含需替换内容的记录');
            return;
        }
        XD.confirm(updateConfirmMsg,function (){
            updateData(btn,"repl");
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
        var nodefullname;
        if(parent.auditApprove){
            nodefullname=parent.auditApprove.docGrid.getSelectionModel().getSelection()[0].get('nodefullname');
        }else if(parent.approve){
            nodefullname=parent.approve.docGrid.getSelectionModel().getSelection()[0].get('nodefullname')
        }else if(resultPreviewGrid.formview.resultgrid.nodefullname){
            nodefullname=resultPreviewGrid.formview.resultgrid.nodefullname
        }else{

            Ext.Ajax.request({
                url: '/audit/getNodeFullName',
                async:false,
                methods:'get',
                params:{
                    nodeid:resultPreviewGrid.formview.resultgrid.dataParams.nodeid
                },
                success: function (response) {
                    nodefullname=response.responseText;
                }
            });
        }
        var operateCount = resultPreviewGrid.formview.resultgrid.getSelectionModel().getSelectionLength();//此处操作记录条数为所有选定记录总条数
        if(inserttype=='anywhere' && placeindex){
            updateConfirmMsg = '本次操作将在['+nodefullname+']相应条件下的记录的['+fieldname+']的第'+placeindex+'位增加“'+addcontent+'”字符串，记录数：共'+operateCount+'条, 是否继续?';
        }
        if(inserttype=='front'){
            updateConfirmMsg = '本次操作将在['+nodefullname+']相应条件下的记录的前面增加“'+addcontent+'”字符串，记录数：共'+operateCount+'条, 是否继续?';
        }
        if(inserttype=='behind'){
            updateConfirmMsg = '本次操作将在['+nodefullname+']相应条件下的记录的后面增加“'+addcontent+'”字符串，记录数：共'+operateCount+'条, 是否继续?';
        }

        XD.confirm(updateConfirmMsg,function (){
            updateData(btn,"add");
        },this);
    },

    //档号对齐
    doCodesettingAlign:function (btn) {//档号对齐（对所有检索出的数据进行操作）
        var grid = btn.findParentByType('tranforAuditDealView').down('[itemId=onlygrid]');
        if(grid.store.data.length>100000){
            XD.msg('当前数据量过大无法使用该功能');
            return;
        }

        var nodeId=borrowdocData.nodeid;
        var SimulationArchivecode;
        Ext.Ajax.request({
            url: '/codesetting/getSimulationArchivecode',
            async:false,
            params: {
                nodeid: nodeId
            },
            timeout:XD.timeout,
            success: function (resp) {
                var respText = Ext.decode(resp.responseText).data;
                if (respText) {
                    SimulationArchivecode=respText;
                } else {
                    XD.msg('未设置档号，请先进行档号设置！');
                }
            }
        });
        var gridStore = grid.getStore();
        if(gridStore.data.length==0){
            XD.msg('没有可操作数据');
            return;
        }
        var alignConfirmMsg = '本次操作将依据档号设置中设定的单位长度（当前档号设置的长度：'+SimulationArchivecode+'），在所有检索结果的想要字段前面补0至指定长度（值超过设定的单位长度将不处理），是否继续？？';
        Ext.Ajax.request({
            url: '/codesetting/getCodeSettingFields',
            async:false,
            params: {
                nodeid: nodeId
            },
            timeout:XD.timeout,
            success: function (resp) {
                var respText = Ext.decode(resp.responseText).data;
                if (respText) {
                    XD.confirm(alignConfirmMsg,function (){
                        alignCodesetting(btn);
                    },this);
                } else {
                    XD.msg('未设置档号，请先进行档号设置！');
                }
            }
        });
    },

    //只读机构权限按钮处理
    refreshToolbarBtnInnerGridOnlyRead: function (grid) {
        //著录表单下方的grid按钮
        var btns = grid.down("toolbar").query('button');
        //著录表单下方的grid按钮间的分隔符
        var tbs = grid.down("toolbar").query('tbseparator');
        //隐藏出查看以外的所有按钮
        for(var i=0;i<btns.length;i++){
            if(btns[i].itemId != 'look'){
                btns[i].hide();
            }
        }
        this.hideAll(tbs);  //隐藏所有分隔符
    },
    //显示所有按钮
    refreshToolbarBtnShowAll: function (grid) {
        //著录表单下方的grid按钮
        var btns = grid.down("toolbar").query('button');
        //著录表单下方的grid按钮间的分隔符
        var tbs = grid.down("toolbar").query('tbseparator');
        //显示所有
        this.showAll(btns);
        this.showAll(tbs);
    },
    //隐藏所有分隔符
    hideAll:function (hideType) {
        for(var num in hideType){
            hideType[num].hide();
        }
    },
    //显示所有
    showAll:function (hideType) {
        for(var num in hideType){
            hideType[num].show();
        }
    }

});
//档号对齐
function alignCodesetting(btn) {
    var grid = btn.findParentByType('tranforAuditDealView').down('auditgrid');
    var allSearchParams = grid.getStore().proxy.extraParams;
    Ext.Msg.wait('正在进行档号对齐，请耐心等待……', '正在操作');

    var columnArray = [];
    var columns = grid.columnManager.getColumns();
    for (var j = 0; j < columns.length; j++) {
        if (columns[j].xtype == 'gridcolumn') {
            var subtext = columns[j].dataIndex + '-' + columns[j].text;
            columnArray.push(subtext);
        }
    }
    allSearchParams['columnArray'] = columnArray;
    var downloadForm = document.createElement('form');
    document.body.appendChild(downloadForm);
    var inputTextElement;
    for (var prop in allSearchParams){
        inputTextElement = document.createElement('input');
        inputTextElement.name = prop;
        inputTextElement.value = allSearchParams[prop];
        downloadForm.appendChild(inputTextElement);
    }
    downloadForm.action = '/codesettingAlign/auditExport';
    downloadForm.method ='POST';
    downloadForm.submit();
    Ext.Ajax.setTimeout(XD.timeout);
    Ext.Ajax.request({
        scope:this,
        url: '/codesettingAlign/collectAlign',
        params:allSearchParams,
        sync : true,
        success: function (response) {

            Ext.MessageBox.hide();
            XD.msg(Ext.decode(response.responseText).msg);
            grid.notResetInitGrid();
        },
        failure: function () {
            Ext.MessageBox.hide();
            XD.msg('操作失败');
        }
    });
}


function delTempByUniquetag() {//清除本机当前用户关联的的临时条目数据
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
    params['entryidArr'] = resultPreviewGrid.formview.entryids.split(',');
    params['fieldModifyData'] = resultPreviewGrid.fieldmodifydata;
    params['fieldReplaceData'] = resultPreviewGrid.fieldreplacedata;
    params['flag'] = resultPreviewGrid.operateFlag;
    params['isSelectAll'] = resultPreviewGrid.formview.isSelectAll;
    params['type'] = '数据采集';
    params['batchtype'] = batchtype; //区分不同类型的批量操作

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
        params: params,
        sync : true,
        timeout:XD.timeout,
        success: function (resp) {
            var respText = Ext.decode(resp.responseText);
            batchModifyResultPreviewWin.hide();
            Ext.Msg.wait(resultPreviewGrid.operateFlag+'操作成功','正在操作').hide();
            XD.msg(respText.msg);
            params['condition'] = params.basicCondition;
            params['operator'] = params.basicOperator;
            params['content'] = params.basicContent;
            resultPreviewGrid.formview.resultgrid.notResetInitGrid();
        },
        failure:function () {
            Ext.Msg.wait(resultPreviewGrid.operateFlag+'操作失败','正在操作').hide();
            XD.msg('更新失败');
        }
    })
}


//隐藏除查看意外的按钮
function hideToolbarBtnTbsByItemId(btns,tbs) {
    for (var num in btns) {
        if (btns[num].itemId !='look'&&btns[num].itemId !='back'&&btns[num].itemId !='lookDoc'&&btns[num].itemId !='print') {
            btns[num].hide();
            if (num >= 1) {
                tbs[num-1].hide();
            } else {
                tbs[num].hide();
            }
        }
    }
}

//进入审批步骤
function auditToNext(docid,nodeid,taskid,nextNode,nextSpman,_usrCertNO){
    Ext.Msg.wait('正在进行入库操作，请耐心等待……', '正在操作');
    Ext.Ajax.request({
        url:'/audit/move',
        params:{
            docid: docid,
            nodeid: nodeid,
            taskid:taskid,
            nextNode:nextNode,
            usrCertNO:_usrCertNO,
            nextSpman:nextSpman
        },
        timeout:XD.timeout,
        success:function(response){
            Ext.MessageBox.hide();
            var resp = Ext.decode(response.responseText);
            if (resp.msg == '档号记录重复') {
                XD.msg(Ext.decode(response.responseText).data);
            } else {
                XD.msg(Ext.decode(response.responseText).msg);
                Ext.defer(function () {
                    if (flag == '1') {
                        parent.wgridView.notResetInitGrid({state:'待处理',type:'采集移交审核'});
                        parent.approve.close();
                    } else if (flag == "2") {
                        parent.auditApprove.docGrid.getStore().reload();
                        parent.auditApprove.close();
                    } else {
                        parent.closeObj.close(parent.layer.getFrameIndex(window.name));
                    }
                },1000);
                //采集业务数据
                captureServiceMetadataByRK(docid,'数据审核','入库');
            }
        }
    })
}

/**
 *获取业务元数据
 * @param entryids 条目集合
 * @param module  模块名
 * @oaram operation 业务行为（著录..）
 * @returns {*}
 */
function captureServiceMetadataByRK(docid,module,operation) {
    var r;
    Ext.Ajax.request({
        url: '/serviceMetadata/captureServiceMetadataByRK',
        async:true,
        methods:'Post',
        params:{
            docid:docid,
            module:module,
            operation:operation
        },
        success: function (response) {
            r = Ext.decode(response.responseText);
            console.log(r.msg+",条目数："+r.data);
        }
    });
    return r;
}

//步骤1：获取用户证书
function getUserCert() {
    var selectType = "{\"UIFlag\":\"default\", \"InValidity\":true,\"Type\":\"signature\", \"Method\":\"device\",\"Value\":\"any\"}";
    var selectCondition = "IssuerCN~'NETCA' && InValidity='True' && CertType='Signature'";
    netca_getCertStringAttribute(null, selectType, selectCondition, -1, successGetCertStringAttributeCallBack,
        failedGetCertStringAttributeCallBack);
}
function successGetCertStringAttributeCallBack(res) {
    /* document.getElementById("signatureCreator_certEncode").value=res.certCode;
     document.getElementById("signatureCreator_certEncodeEx").value=res.certCode;*/
    _certEncode=res.certCode;
    _usrCertNO=res.AppUsrCertNO;
   /* if(caUserid==res.AppUsrCertNO){
        _sameName=1;//证书和用户一致
    }*/
    //alert("用户证书编码： "+res.certCode);
    getUserSealImage();//获取用户签章图片 Base64编码
}

function failedGetCertStringAttributeCallBack(res) {
    alert(res.msg);
}

function seal_SignSealPosition(_srcBytes,_xPos,_yPos,_width,_sealImageEncode,type,orders) {
    var tarFilepath='';//存储路径
    var params = {
        srcFile: '',                      //源pdf文件
        srcBytes: _srcBytes,                    //源Pdf文件的Base64编码
        destFile: tarFilepath,                    //目标pdf文件
        certEncode: _certEncode,                //签名证书Base64编码
        selMode: 1,                      //操作模式
        signFieldText: '',          //签名域显示的文字
        sealImageEncode: _sealImageEncode,      //签章图片Base64编码
        revInfoIncludeFlag: false,//是否包含吊销信息
        SignPosition:                           //签名位置对象
            {
                pageNum: 1,                  //PDF文档的页码
                xPos: _xPos,                        //签名域/签章左下角的水平向右方向坐标
                yPos: _yPos,                        //签名域/签章左下角的垂直向上方向坐标
                width: _width,                      //签名域/签章的宽度
                height: _width                    //签名域/签章的高度
            },
        Tsa:                                    //时间戳对象
            {
                //tsaUrl: 'http://tsa.cnca.net/NETCATimeStampServer/TSAServer.jsp',                    //时间戳地址
                tsaUrl: '',                    //时间戳地址
                tsaUsr: '',                    //时间戳服务对应用户名
                tsaPwd: '',                    //时间戳服务对应用户的密码
                tsaHashAlgo: ''           //时间戳使用的hash算法，例如”sha-1”，”sha-256”等
            }
    };

    NetcaPKI.signatureCreatorSignSeal(params)
        .Then(function (res)
        {
            SignatureCreatorSuccessCallBack(res,type,orders);
        })
        .Catch(function (res)
        {
            SignatureCreatorFailedCallBack(res,type);
        });
}

function SignatureCreatorSuccessCallBack(res,type,orders) {
    pwdOk=true;
    var pdfData = res.destFileEncode;
    if (type) {
        //签章后保存文件到服务器
        generatePdf(pdfData, type);
    }
    if(orders!=5) {
        var result = "签名/章成功, 目标文件的Base64编码：\n" + res.destFileEncode;
        alert('签名/章成功');
        if (res.destFileEncode.length >= 20 * 1024 * 1024) {
            alert("注意：目标文件大于20M，会造成浏览器卡顿");
        }
        //if (type == 1 || type == 2) {//第二步的移交签章直接打开pdf  第五步的审核签章的二次签章直接打开pdf
            //签章后打开pdf文件
            sessionStorage.setItem("_imgUrl", pdfData);
            var url = '../../../js/pdfJs/web/ureportviewer.html';
            window.open(url, '_blank');
        // } else {//进行二次签章签章 用当前证书的签章
        //     seal_SignSealPosition(pdfData, 415, 682, 89, _sealImageEncode, 2,3);
        // }
    }else{
        pdfData = getFileBase64(window.docid);
        seal_SignSealPosition(pdfData, 415, 682, 89, _sealImageEncode,2,3);
    }
}
function SignatureCreatorFailedCallBack(res,type) {
    pwdOk=false;
    Ext.Ajax.request({
        url: '/audit/updateTrandoc',
        params: {
            docid: docid.trim(),
            type: type,
            pwdno:"1"
        },
        method: 'POST',
        async: false,
        success: function (resp) {
        }
    });
    alert("签名/章失败 " + res.msg);
}

//步骤2：获取用户签章图片
function getUserSealImage() {//传递的参数是用户证书编码
    netca_getSealImage(_certEncode, successGetUserSealImageCallBack, failedGetUserSealImageCallBack);//传递的参数是用户证书编码
}
function successGetUserSealImageCallBack(res) {
    /*document.getElementById("signatureCreator_sealImageEncode").value=res.sealImageBase64;
     document.getElementById("signatureCreator_sealImageEncodeEx").value=res.sealImageBase64;*/
    _sealImageEncode=res.sealImageBase64;
    //alert("用户签章图片编码： "+res.sealImageBase64);
}

function failedGetUserSealImageCallBack(res) {
    alert(res.msg);
}

function generatePdf(pdfData,type){
    Ext.Ajax.request({
        url: '/acquisition/generatePdf',
        async:false,
        methods:'Post',
        params:{
            pdfData:pdfData,
            docid:window.docid,
            usrCertNO:_usrCertNO,
            type:type
        },
        success: function (response) {
            XD.msg('生成签章PDF成功');
        },
        failure:function(){
            XD.msg('生成签章PDF失败');
        }
    });
}

function getSigncode(docid){
    Ext.Ajax.request({
        url: '/acquisition/getSigncode',
        async:false,
        methods:'POST',
        params:{
            docid:docid
        },
        success: function (response) {
            var returnMsg=Ext.decode(response.responseText);
            //XD.msg(returnMsg.msg);
            if(returnMsg.success==true){
                _sealImageEncode_trans=returnMsg.data.transforcaid;
                //_sealImageEncode_edit=returnMsg.data.editcaid;
            }else{
                _sealImageEncode_trans=undefined;
                //_sealImageEncode_edit=undefined;
            }
            //alert(editSign);
        },
        failure:function(){
            //XD.msg('获取数字签章base64失败');
            _sealImageEncode_trans=undefined;
            //_sealImageEncode_edit=undefined;
        }
    });
}

function getFileBase64(docid){
    var pdfData;
    Ext.Ajax.request({
        url: '/acquisition/getFileBase64',
        async:false,
        methods:'Post',
        params:{
            docid:docid
        },
        success: function (response) {
            XD.msg(Ext.decode(response.responseText).msg);
            pdfData= Ext.decode(response.responseText).data;
        },
        failure:function(){
            XD.msg('获取PDF源文件base64失败');
        }
    });
    return pdfData;
}