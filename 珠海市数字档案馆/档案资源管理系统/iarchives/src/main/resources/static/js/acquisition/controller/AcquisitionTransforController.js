/**
 * 数据采集-数据移交控制器
 * Created by Rong on 2018/6/20.
 */
var acquisitionGrid;
var _certEncode;              //签名证书Base64编码
var _sealImageEncode;        //签章图片Base64编码  移交
var _sealImageEncode_edit;        //签章图片Base64编码  审核
var _srcBytes;              //pdf文件Base64编码
var _sameName;//本机所用的数字证书和用户绑定证书一致
var _usrCertNO;//数字证书编号
var volumeNodeId="";    //卷内节点
Ext.define('Acquisition.controller.AcquisitionTransforController', {
    extend: 'Ext.app.Controller',
    views:['transfor.AcquisitionShowTransdocGridView',
        'transfor.AcquisitionTransdocEntryGridView',
        'transfor.AcquisitionTransdocView',
        'transfor.AcquisitionDocFormView',
        'transfor.LongRetentionDetailView',
        'transfor.SendbackreasonFormView','transfor.AcquisitionPreviewTransEntryGridView','transfor.AcquisitionPreviewTransView',
        'transfor.LongRetentionAcGridView'],
    init:function(){
        this.control({

            'mediaItemsDataView [itemId=transfor]': {                              //移交
                click: this.transforHandler_Media
            },
            'acquisitiongrid [itemId=transfor]': {                              //移交
                click: this.transforHandler
            },
            'acquisitiongrid [itemId=addTransfor]': {                           //加入移交
                click: this.addtransforHandler
            },
            'acquisitiongrid [itemId=processingTransfor]': {                    //处理移交
                click: this.showPreviewTransDocHandler
            },
            'acquisitiongrid [itemId=showtransdoc]':{                           //查看移交单据
                click:this.showTransdocHandler
            },
            'acquisitionDocFormView button[itemId=CancelBtnID]':{
                click:this.cancelHandler                                            //取消移交
            },
            'acquisitionDocFormView button[itemId=SendBtnID]':{
                click:this.sendHandler                                              //提交移交
            },
            'acquisitionShowTransdocGridView [itemId=showEntryDetail]':{    //查看移交单据列表 查看详细条目
                click:this.showEntryDetailHandler
            },
            'acquisitionTransdocEntryGridView [itemId=look]':{                //查看移交单据条目详情列表 查看
                click:this.transdocLookHandler
            },
            'acquisitionShowTransdocGridView [itemId=print]':{                //查看移交单据列表 打印
                click:this.printTransdocHandler
            },
            'acquisitionShowTransdocGridView [itemId=reTransfor]':{          //查看移交单据列表 重新移交
                click:this.reTransforHandler
            },
            'acquisitionShowTransdocGridView [itemId=deleteTransfor]':{      //查看移交单据列表 删除移交单据
                click:this.deleteTransforHandler
            },
            'acquisitionShowTransdocGridView [itemId=back]':{                 //查看移交单据列表 返回
                click:function (btn) {
                    acquisitionGrid.getStore().reload();
                    btn.up('window').hide();
                }
            },
            'acquisitionShowTransdocGridView [itemId=lookBack]':{             //查看移交单据列表 查看退回原因
                click:function (view) {
                    var docGrid = view.findParentByType('acquisitionShowTransdocGridView');
                    var record = docGrid.getSelectionModel().getSelection();
                    if (record.length != 1) {
                        XD.msg('请选择一条需要查看的单据');
                        return;
                    }
                    var docid = record[0].get('docid');
                    var sendbackreasonWin = Ext.create('Ext.window.Window',{//弹出窗口，填写退回原因
                        width:900,
                        height:300,
                        title:'查看退回原因',
                        draggable : true,//可拖动
                        modal:true,
                        closeToolText:'关闭',
                        layout:'fit',
                        items:[{
                            xtype:'sendbackreasonFormView'
                        }]
                    });
                    var form = sendbackreasonWin.down("sendbackreasonFormView");
                    form.load({
                        url: '/audit/getAuditDoc',
                        params: {
                            docid: docid
                        },
                        success: function (form, action) {
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
                    sendbackreasonWin.show();
                }
            },
            'acquisitionTransdocEntryGridView [itemId=back]':{              //查看移交单据条目详情列表 返回
                click:this.activeDocGrid
            },
            'acquisitionTransdocEntryGridView ': {
                eleview: this.activeEleForm
            },
            'acquisitionShowTransdocGridView button[itemId=urging]': {      //催办
                click: this.manualUrging
            },
            'acquisitionShowTransdocGridView button[itemId=signId]': {      //签章
                click: this.caSign
            },
            'acquisitionShowTransdocGridView button[itemId=signVerify]': {      //验证签章
                click: this.signVerify
            },
            'longRetentionAcGridView [itemId=lookdetail]': {//查看验证明细
                click: this.lookdetailsHandler
            },
            'longRetentionAcGridView [itemId=transforTwo]': {//确定移交
                click: this.transforTwoHandler
            },
            'acquisitionPreviewTransEntryGridView [itemId=look]': {  //移交预览-查看
                click: this.lookPreviewEntryHandler
            },
            'acquisitionPreviewTransEntryGridView [itemId=delete]': {  //移交预览-删除
                click: this.deletetransforHandler
            },
            'acquisitionPreviewTransEntryGridView [itemId=transfor]': {  //移交预览-移交
                click: this.transforHandler_preview
            },
            'acquisitionPreviewTransEntryGridView [itemId=back]': {  //移交预览-返回
                click:function (view) {
                    view.up('window').hide();
                }
            },
        });
    },

    //手动催办
    manualUrging: function (view) {
        var acquisitionTransdocEntryGridView = view.findParentByType('acquisitionShowTransdocGridView');
        var select = acquisitionTransdocEntryGridView.getSelectionModel();
        if (!select.hasSelection()) {
            XD.msg('请选择一条数据!');
            return;
        }
        var details = select.getSelection();
        if(details.length!=1){
            XD.msg('只支持单条数据催办!');
            return;
        }
        if( details[0].get("state")!="待审核"&&details[0].get("state")!="已送审"){
            XD.msg('请选择正确的数据催办!');
            return;
        }
        Ext.MessageBox.wait('正在处理请稍后...');
        Ext.Ajax.request({
            params: {transfercode: details[0].get("transfercode"),sendMsg:acquisitionTransdocEntryGridView.down("[itemId=message]").checked},
            url: '/acquisition/manualUrging',
            method: 'POST',
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
    },

    //签章
    caSign: function (view) {
        var acquisitionTransdocEntryGridView = view.findParentByType('acquisitionShowTransdocGridView');
        var select = acquisitionTransdocEntryGridView.getSelectionModel();
        if (!select.hasSelection()) {
            XD.msg('请选择一条数据!');
            return;
        }
        var details = select.getSelection();
        if(details.length!=1){
            XD.msg('只支持单条数据签章!');
            return;
        }
        if(details[0].get("state")!="已审核"||details[0].get("transforcasign")!="Y"){
            XD.msg('请选择已审核的数据且已进行移交签章的数据进行签章!');
            return;
        }
        //Ext.MessageBox.wait('正在处理请稍后...');
        var signPrint=0;//移交签章标记  0没有 1有
        if(_certEncode) {//有数字证书的才进行报表签章
            /*//获取签章base64编码
             getUserSealImage();*/
            if(_sealImageEncode&&_sealImageEncode.length>200){//有移交签章才进行签章打印
                signPrint=1;
            }
        }else{
            XD.msg('请确定已插上个人数字证书!');
            return;
        }
        if(_certEncode&&signPrint==1) {//有证书存在且有签章存在
            if(_certEncode/*&&_sameName==2*/){//证书拥有者的操作才进行报表签章
                window.docid=details[0].get("docid").trim();
                //获取移交签章的pdf文件流
                var pdfData=getFileBase64(window.docid);
                // 对PDF签章，签章成功后再打开pdf页面
                seal_SignSealPosition(pdfData,415,682,89);
            }
        }else{
            XD.msg('请确定个人数字证书有绑定签章!');
            return;
        }
    },

    //验证签章
    signVerify: function (view) {
        var acquisitionTransdocEntryGridView = view.findParentByType('acquisitionShowTransdocGridView');
        var select = acquisitionTransdocEntryGridView.getSelectionModel();
        if (!select.hasSelection()) {
            XD.msg('请选择一条数据!');
            return;
        }
        var details = select.getSelection();
        if(details.length!=1){
            XD.msg('只支持单条数据验证签章!');
            return;
        }
        if(details[0].get("transforcasign")!="Y"){
            XD.msg('请选择已有过审核签章的数据进行签章验证!');
            return;
        }
        //Ext.MessageBox.wait('正在处理请稍后...');
        var signPrint=0;//移交签章标记  0没有 1有
        if(_certEncode) {//有数字证书的才能进行签章验证
            /*//获取签章base64编码
             getUserSealImage();*/
            if(_sealImageEncode&&_sealImageEncode.length>200){//有移交签章才进行签章验证
                signPrint=1;
            }
        }else{
            XD.msg('请确定已插上个人数字证书!');
            return;
        }
        if(_certEncode&&signPrint==1) {//有证书存在且有签章存在
            if(_certEncode/*&&_sameName==2*/){//证书拥有者的操作才进行签章验证
                Ext.Ajax.request({
                    url: '/acquisition/PDFVerifySign',
                    async:false,
                    methods:'Post',
                    params:{
                        docid:details[0].get("docid").trim()
                        //docid:''
                    },
                    success: function (response) {
                        var returnMsg=Ext.decode(response.responseText);
                        XD.msg(returnMsg.msg);
                    },
                    failure:function(){
                        XD.msg('验证中断！');
                    }
                });

            }else{
                XD.msg('请确定人数字证书有绑定签章!');
                return;
            }
        }
    },

    /**
     * 获取数据采集主控制器
     * @returns {*|Ext.app.Controller}
     */
    findMainControl:function(){
        return this.application.getController('AcquisitionController');
    },
    //获取数据采集查看移交单据视图
    findDocView: function (btn) {
        return btn.findParentByType('acquisitionTransdocView');
    },
    //获取查看移交单据界面的单据列表视图
    findDocGridView:function (btn) {
        return this.findDocView(btn).down('acquisitionShowTransdocGridView');
    },
    //获取查看移交单据界面的条目详情列表视图
    findDocEntryGridView:function (btn) {
        return this.findDocView(btn).down('acquisitionTransdocEntryGridView');
    },
    //切换到查看移交单据界面的单据列表视图
    activeDocGrid: function (btn) {
        var view = this.findDocView(btn);
        var docview = this.findDocGridView(btn);
        view.setActiveItem(docview);
        return docview;
    },
    //切换到查看移交单据界面的条目详情列表视图
    activeDocEntryGrid: function (btn) {
        var view = this.findDocView(btn);
        var docentryview = this.findDocEntryGridView(btn);
        view.setActiveItem(docentryview);
        return docentryview;
    },

    /**
     * 查看移交单据
     * @param btn
     */
    showTransdocHandler:function (btn) {
        var grid = this.findMainControl().getGrid(btn);
        acquisitionGrid =  grid;
        var tree = this.findMainControl().findGridView(btn).down('treepanel');
        var node = tree.selModel.getSelected().items[0];
        if(!node){
            XD.msg('请选择节点');
            return;
        }
        var showTransdocWin = Ext.create('Ext.window.Window',{
            modal:true,
            width:1000,
            height:530,
            title:'查看移交单据',
            layout:'fit',
            closeToolText:'关闭',
            closeAction:'hide',
            items:[{
                xtype: 'acquisitionTransdocView',
                nodeid:grid.nodeid
            }],
            listeners:{
                "close":function () {
                    acquisitionGrid.getStore().reload();
                }
            }
        });
        var showDocView = showTransdocWin.down('acquisitionTransdocView');
        var docGrid = showDocView.down('acquisitionShowTransdocGridView');
        docGrid.getStore().setPageSize(XD.pageSize);
        docGrid.initGrid({nodeid:showDocView.nodeid});
        var buttons = docGrid.down("toolbar").query('button');
        var tbseparator = docGrid.down("toolbar").query('tbseparator');
        Ext.Ajax.request({//根据审批id判断是否可以催办
            url: '/acquisition/findByWorkId',
            method: 'GET',
            success: function (resp) {
                var respDate = Ext.decode(resp.responseText).data;
                if(respDate.urgingstate=="1"){
                    buttons[4].show();
                    docGrid.down('[itemId=message]').show();
                }
            }
        });
        if(auditOpened == 'true'){
            //显示移交状态 、退回原因列
            docGrid.columns[6].show();
            docGrid.columns[9].show();
            //显示重新移交、删除移交单据和分隔符
            buttons[2].show();
            tbseparator[1].show();
            tbseparator[2].show();

        }else{
            //隐藏移交状态 、退回原因列
            docGrid.columns[6].hide();
            docGrid.columns[9].hide();
            //隐藏重新移交、删除移交单据和分隔符
            buttons[2].hide();
            tbseparator[1].hide();
            tbseparator[2].hide();
        }
        if (netcatUse == '0'){
            docGrid.columns[8].hide();
            docGrid.columns[7].hide();
        }
        showTransdocWin.show();
        if(netcatUse=='1'){//已启用签章配置
            if(_certEncode){//没有证书编号的时候，读取一下
            }else{
                getUserCert();//获取用户证书 Base64编码
            }
        }
    },

    /**
     * 查看与单据相关联的条目详细内容
     * @param btn
     */
    showEntryDetailHandler:function (btn) {
        var ids = [];
        var showDocView = this.findDocView(btn);
        var docGrid = this.findDocGridView(btn);
        var entryGrid = this.findDocEntryGridView(btn);
        var record = docGrid.getSelectionModel().getSelection();
        if(record.length!=1){
            XD.msg('请选择一条需要查看的单据');
            return;
        }
        var docid = record[0].get('docid');
        var docState = record[0].get('state');
        var params = {
            docid:docid,
            docState:docState,
            nodeid:showDocView.nodeid//请求模板数据，显示列表
        };
        var buttons = entryGrid.down("toolbar").query('button');
        var tbseparator = entryGrid.down("toolbar").query('tbseparator');
        if(docState == '已审核'){
            buttons[1].hide();
            tbseparator[0].hide();
        }else{
            buttons[1].show();
            tbseparator[0].show();
        }
        entryGrid.initGrid(params);
        entryGrid.parentXtype = 'acquisitionTransdocView';
        entryGrid.formXtype = 'acquisitionform';
        this.activeDocEntryGrid(btn);
    },

    /**
     * 数据移交查看条目详细内容
     * @param btn
     */
    transdocLookHandler: function (btn) {
        var grid = this.findDocEntryGridView(btn);
        var form = this.findMainControl().findDfView(btn);
        var records = grid.selModel.getSelection();
        var nodeid = this.findDocView(btn).nodeid;
        if (records.length == 0) {
            XD.msg('请至少选择一条需要查看的数据');
            return;
        }
        var entryids = [];
        for(var i=0;i<records.length;i++){
            entryids.push(records[i].get('entryid'));
        }
        var entryid = records[0].get('entryid');
        var initFormFieldState = this.findMainControl().initFormField(form, 'hide', nodeid);
        form.operate = 'look';
        form.entryids = entryids;
        form.entryid = entryids[0];
        if(!initFormFieldState){//表单控件加载失败
            return;
        }
        this.findMainControl().initFormData('look',form, entryid);
        this.findMainControl().activeToForm(form);
        this.findMainControl().loadFormRecord('look',form, entryid);//最后加载表单条目数据
    },

    /**
     * 打印移交单据
     * @param btn
     */
    printTransdocHandler:function (btn) {
        var ids = [];
        var states = [];
        var transforcasigns = [];
        var params = {};
        var ureportName="移交单据管理_未打钩";
        var docGrid = btn.up('window').down('acquisitionShowTransdocGridView');
        var record = docGrid.getSelectionModel().getSelection();
        if(record.length!==1){
            XD.msg('请选择一条需要打印的移交单据');
            return;
        }
        Ext.each(record,function(){
            ids.push(this.get('docid').trim());
            states.push(this.get('state'));
            if(this.get('editcasign')=='Y'||this.get('editcasign')=='0'){
                ureportName='移交单据管理';
            }
            transforcasigns.push(this.get('transforcasign'));
        });
        if(states.indexOf('已审核')!=-1 && (states.indexOf('已退回')!=-1 ||states.indexOf('待审核')!=-1 || states.indexOf('已移交')!=-1)){
            XD.msg('请单独打印移交状态为“已审核”的单据');
            return;
        }

        var signPrint=0;//移交签章标记  0没有 1有
        if(_certEncode) {//有数字证书的才进行报表签章
            //获取移交和审核的签章base64编码
            //getSigncode(ids.join(","));
            if(_sealImageEncode&&_sealImageEncode.length>200){//有移交签章才进行签章打印
                signPrint=1;
            }
        }

        if(transforcasigns.indexOf('Y')!=-1){//已有移交签章
            var pdfData=getFileBase64(ids.join(","));
            sessionStorage.setItem("_imgUrl", pdfData);
            var url = '../../../js/pdfJs/web/ureportviewer.html';
            window.open(url, '_blank');
            return;
        }
        if(reportServer == 'UReport') {
            params['docid'] = ids.join(",");
            XD.UReportPrint(null, ureportName, params);
        } else if(reportServer == 'FReport'){
            XD.FRprint(null, ureportName, ids.length > 0 ? "'docid':'" + ids.join(",")+"'": '');
        }
    },

    /**
     * 重新移交（仅限移交状态为“已退回”的单据）
     * @param btn
     */
    reTransforHandler:function (btn) {
        var ids = [];
        var showDocView = this.findDocView(btn);
        var docGrid = this.findDocGridView(btn);
        var record = docGrid.getSelectionModel().getSelection();
        if(record.length<1){
            XD.msg('请选择需要重新移交的单据');
            return;
        }
        var flag = false;
        Ext.each(record,function(){
            if(this.get('state')!='已退回'){
                flag = true;
            }
        });
        if(flag){
            XD.msg('请选择移交状态为“已退回”的单据');
            return;
        }

        Ext.each(record,function(){
            ids.push(this.get('docid'));
        });
        var docids = ids.join(',');
        XD.confirm('是否确认重新移交？',function () {
            Ext.MessageBox.wait('正在移交数据...','提示');
            Ext.Ajax.request({
                url: '/acquisition/reTransfor',
                params:{
                    docids:docids
                },
                success: function (response) {
                    Ext.MessageBox.hide();
                    docGrid.getStore().reload();
                    var responseText = Ext.decode(response.responseText);
                    if(responseText.success==false && responseText.data!=null){
                        Ext.MessageBox.alert("提示信息", responseText.msg, function(){});
                        return;
                    }else{
                        XD.msg(responseText.msg);
                    }
                },
                failure:function () {
                    Ext.MessageBox.hide();
                    XD.msg('操作失败');
                }
            });
        },this);
    },
    
    /**
     * 删除移交单据
     * @param {} btn
     */
    deleteTransforHandler: function (btn) {
    	var ids = [];
        var showDocView = this.findDocView(btn);
        var docGrid = this.findDocGridView(btn);
        var record = docGrid.getSelectionModel().getSelection();
        if(record.length < 1){
            XD.msg('请选择需要删除的移交单据');
            return;
        }
		for (var i = 0; i < record.length; i++) {
			if (record[i].data.state != '已审核') {
                ids.push(record[i].data.docid);
            }
		    if (record[i].data.state == '已审核') {
		    	XD.msg('不能删除已审核入库的移交单据。');
                return;
            }
        }
        XD.confirm('是否确认删除所选中移交单据？',function () {
        	var docids = ids.join(',');
            Ext.Ajax.request({
                url: '/acquisition/deleteNodeTransdoc',
                params:{
                    docids:docids
                },
                success: function (response) {
                    docGrid.initGrid({nodeid:showDocView.nodeid});
                    var responseText = Ext.decode(response.responseText);
                    if(responseText.success == false && responseText.data != null){
                        Ext.MessageBox.alert("提示信息", responseText.msg, function(){});
                        return;
                    }
                    acquisitionGrid.initGrid({nodeid:showDocView.nodeid});
                    XD.msg(responseText.msg);
                },
                failure:function () {
                    XD.msg('操作失败');
                }
            });
        },this);
    },

    /**
     * 数据移交，填写移交相关信息
     * @param btn
     */
    transforHandler_Media: function (btn) {
        if(netcatUse=="1") {
            if (_certEncode) {//没有证书编号的时候，读取一下
            } else {
                getUserCert();
            }
        }
        var grid = this.findMainControl().getGrid(btn);
        var record = grid.acrossSelections;
        var count = record.length;//总条目数
        var tree = this.findMainControl().findGridView(btn).down('treepanel');
        var node = tree.selModel.getSelected().items[0];
        if (!node) {
            XD.msg('请选择节点');
            return;
        }
        if (record.length == 0) {
            XD.msg('请至少选择一条需要移交的数据');
            return;
        }
        var innergrid = this.findMainControl().findInnerGrid(btn);
        var docwin=new Ext.create({
            xtype:'acquisitionDocFormView',
            grid:grid,
            innergrid:innergrid
        });//传递innergrid对象
        var tree=this.findMainControl().findGridView(btn).down('treepanel');
        var classnode=tree.selModel.getSelected().items[0].data.classlevel;
        if(classnode!=2) {
            //docwin.down('[itemId=isSynch]').hide();
        }
        var isSelectAll = false;
        if(isSelectAll){
            record = grid.acrossDeSelections;
            isSelectAll = true;
        }
        var tmp = [];
        for (var i = 0; i < record.length; i++) {
            tmp.push(record[i].get('entryid'));
        }
        var entryids = tmp.join(",");
        var tempParams = grid.down('dataview').getStore().proxy.extraParams;
        tempParams['entryids'] = entryids;
        tempParams['isSelectAll'] = isSelectAll;
        tempParams['totalCount'] = count;//条目总数
        tempParams['module'] = 'capture';//采集模块
        tempParams['isVolume'] = '';//是否为案卷

        var innergrid = this.findMainControl().findInnerGrid(btn);
        var tree=this.findMainControl().findGridView(btn).down('treepanel');
        var classnode=tree.selModel.getSelected().items[0].data.classlevel;

        //移交前先进性四性验证
        XD.confirm('移交前需要对这些数据进行四性验证', function () {
            Ext.MessageBox.wait('正在进行数据包安全认证...', '提示');
            Ext.Ajax.request({
                url: '/longRetention/verification',
                method: 'post',
                scope: this,
                params: tempParams,
                timeout: XD.timeout,
                success: function (res) {
                    XD.msg('执行验证成功');
                    Ext.MessageBox.hide();
                    var response = Ext.decode(res.responseText);
                    // var volumeEntryIds=response.data.join(",");
                    // entryids=entryids+","+volumeEntryIds;
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
                            grid:grid,
                            //volumeEntryIds:volumeEntryIds,
                            classnode:classnode,
                            innergrid:innergrid
                        }]
                    });
                    var fsGrid = fsWin.down('longRetentionAcGridView');
                    volumeNodeId=response.msg;
                    fsGrid.initGrid({nodeid:node.get('fnid'),entryids:entryids,checkAll:isSelectAll,volumeNodeId:volumeNodeId});
                    fsWin.show();
                }
            });
        }, this);
    },
    addtransforHandler:function(btn){
        var grid = this.findMainControl().getGrid(btn);
        var selectAll=grid.down('[itemId=selectAll]').checked;
        var record = grid.selModel.getSelection();
        var tree = this.findMainControl().findGridView(btn).down('treepanel');
        var node = tree.selModel.getSelected().items[0];
        if (!node) {
            XD.msg('请选择节点');
            return;
        }
        if (grid.selModel.getSelectionLength() == 0) {
            XD.msg('请至少选择一条需要加入移交的数据');
            return;
        }
        var isSelectAll = false;
        if(selectAll){
            record = grid.acrossDeSelections;//取消选择的条目
            isSelectAll = true;
        }
        var tmp = [],delEntryIds=[];
        for (var i = 0; i < record.length; i++) {
            tmp.push(record[i].get('entryid'));
            if(selectAll) {
                delEntryIds.push(record[i].get('entryid'));
            }
        }
        var tempParams = grid.getStore().proxy.extraParams;
        tempParams['entryids'] = tmp;
        tempParams['isSelectAll'] = isSelectAll;
        Ext.MessageBox.wait('正在加入移交中...', '提示');
        Ext.Ajax.request({
            url: '/acquisition/addtransfor',
            method: 'post',
            scope: this,
            params: tempParams,
            timeout: XD.timeout,
            success: function (res) {
                Ext.MessageBox.hide();
                grid.getStore().reload();
                XD.msg("加入移交成功");
            },
            failure: function (response,opts) {
                Ext.MessageBox.hide();
                XD.msg("加入移交失败");
            }
        });
    },

    /**
     * 查看加入移交的列表
     * @param btn
     */
    showPreviewTransDocHandler:function (btn) {
        var grid = this.findMainControl().getGrid(btn);
        acquisitionGrid = grid;
        var tree = this.findMainControl().findGridView(btn).down('treepanel');
        var node = tree.selModel.getSelected().items[0];
        if (!node) {
            XD.msg('请选择节点');
            return;
        }
        var preview = Ext.create('Ext.window.Window', {
            modal: true,
            width: 1200,
            height: 580,
            title: '移交条目列表',
            layout: 'fit',
            closeToolText: '关闭',
            closeAction: 'hide',
            draggable : false,//禁止拖动
            resizable : false,//禁止缩放
            items: [{
                xtype: 'acquisitionPreviewTransView',
                nodeid: grid.nodeid,
                grid:grid,
                innergrid: this.findMainControl().findInnerGrid(btn)
            }],
            listeners: {
                "close": function () {
                    //acquisitionGrid.getStore().reload();
                }
            }
        });
        var docGrid = preview.down('acquisitionPreviewTransEntryGridView');
        docGrid.getStore().setPageSize(XD.pageSize);
        docGrid.initGrid({nodeid: grid.nodeid});
        preview.show();
    },

    deletetransforHandler: function (btn) {//删除加入移交的详情
        var grid = btn.up('acquisitionPreviewTransView').down('acquisitionPreviewTransEntryGridView');
        var record = grid.getSelectionModel().getSelection();
        var selectAll=grid.down('[itemId=selectAll]').checked;
        if (grid.selModel.getSelectionLength() == 0) {
            XD.msg('请选择需要删除的数据');
            return;
        }
        var tmp=[];
        if(selectAll){
            record = grid.acrossDeSelections;//取消选择的条目
        }
        for (var i = 0; i < record.length; i++) {
            tmp.push(record[i].get('entryid'));
        }
        var tempParams=grid.getStore().proxy.extraParams;
        tempParams["entryids"]=tmp;
        tempParams["isSelectAll"]=selectAll;
        Ext.MessageBox.wait('正在加入移交中...', '提示');
        Ext.Ajax.request({
            url: '/acquisition/deleteTransfor',
            method: 'post',
            scope: this,
            params: tempParams,
            timeout: XD.timeout,
            success: function (res) {
                Ext.MessageBox.hide();
                XD.msg("删除成功");
                acquisitionGrid.getStore().reload();
                grid.notResetInitGrid();
            },
            failure: function (response,opts) {
                Ext.MessageBox.hide();
                XD.msg("删除失败");
            }
        });
    },

    //查看加入移交的条目详情
    lookPreviewEntryHandler: function (btn) {
        var grid = btn.up('acquisitionPreviewTransView').down('acquisitionPreviewTransEntryGridView');
        var form = this.findMainControl().findDfView(btn);
        var records = grid.selModel.getSelection();
        var nodeid =  btn.up('acquisitionPreviewTransView').nodeid;
        if (records.length == 0) {
            XD.msg('请至少选择一条需要查看的条目');
            return;
        }
        var entryids = [];
        for(var i=0;i<records.length;i++){
            entryids.push(records[i].get('entryid'));
        }
        var entryid = records[0].get('entryid');
        var initFormFieldState = this.findMainControl().initFormField(form, 'hide', nodeid);
        form.operate = 'look';
        form.entryids = entryids;
        form.entryid = entryids[0];
        if(!initFormFieldState){//表单控件加载失败
            return;
        }
        this.findMainControl().initFormData('look',form, entryid);
        this.findMainControl().activeToForm(form);
        this.findMainControl().loadFormRecord('look',form, entryid);//最后加载表单条目数据
    },

    /**
     * 加入移交界面-数据移交
     * @param btn
     */
    transforHandler_preview: function (btn) {
        if(netcatUse=="1") {
            if(_certEncode){//没有证书编号的时候，读取一下
            }else{
                getUserCert();
            }
        }
        var preview =btn.up('acquisitionPreviewTransView');
        var grid =preview.down('acquisitionPreviewTransEntryGridView');
        var selectAll=grid.down('[itemId=selectAll]').checked;
        var record = grid.selModel.getSelection();
        var count = grid.getStore().getTotalCount();//总条目数
        if (grid.selModel.getSelectionLength() == 0) {
            XD.msg('请至少选择一条需要移交的数据');
            return;
        }else if(grid.selModel.getSelectionLength() >=1000&&!selectAll){
            XD.msg('暂不支持超过1000条目移交，可选择所有页进行操作');
            return;
        }
        var isSelectAll = false;
        if(selectAll){
            record = grid.acrossDeSelections;//取消选择的条目
            isSelectAll = true;
        }
        var tmp = [],delEntryIds=[];
        for (var i = 0; i < record.length; i++) {
            tmp.push(record[i].get('entryid'));
            if(selectAll) {
                delEntryIds.push(record[i].get('entryid'));
            }
        }
        var entryids = tmp.join(",");
        var tempParams = grid.getStore().proxy.extraParams;
        tempParams['entryids'] = entryids;
        tempParams['isSelectAll'] = isSelectAll;
        tempParams['totalCount'] = count;//条目总数
        tempParams['module'] = 'capture';//采集模块
        tempParams['isVolume'] = '';//是否为案卷
        tempParams['nodefullname'] =preview.grid.nodefullname;//节点全称
        tempParams['transforType'] ="1";//节点全称
        //移交前先进性四性验证
        XD.confirm('移交前需要对这些数据进行四性验证', function () {
            Ext.MessageBox.wait('正在进行数据包安全认证...', '提示');
            Ext.Ajax.request({
                url: '/longRetention/verification',
                method: 'post',
                scope: this,
                params: tempParams,
                timeout: XD.timeout,
                success: function (res) {
                    XD.msg('执行验证成功');
                    Ext.MessageBox.hide();
                    var response = Ext.decode(res.responseText);
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
                            grid:grid,
                            innergrid:preview.innergrid,
                            delEntryIds:delEntryIds,
                            preview:preview
                        }]
                    });
                    fsWin.on('close', function () {
                        grid.getStore().proxy.extraParams.entryids=null;
                    }, grid);
                    var fsGrid = fsWin.down('longRetentionAcGridView');
                    volumeNodeId=response.msg;
                    var fsarams=grid.getStore().proxy.extraParams;
                    fsGrid.getStore().setPageSize(XD.pageSize);
                    fsGrid.initGrid({nodeid:preview.grid.nodeid,transforType:"1",entryids:entryids,checkAll:isSelectAll,condition:fsarams.condition,operator:fsarams.operator,content:fsarams.content,
                        volumeNodeId:volumeNodeId});
                    fsWin.show();
                }
            });
        }, this);
    },

    /**
     * 数据移交，填写移交相关信息
     * @param btn
     */
    transforHandler: function (btn) {
        if(netcatUse=="1") {
            if(_certEncode){//没有证书编号的时候，读取一下
            }else{
                getUserCert();
            }
        }
        var grid = this.findMainControl().getGrid(btn);
        acquisitionGrid = grid;
        var selectAll=grid.down('[itemId=selectAll]').checked;
        var record = grid.selModel.getSelection();
        var count = grid.getStore().getTotalCount();//总条目数
        var tree = this.findMainControl().findGridView(btn).down('treepanel');
        var node = tree.selModel.getSelected().items[0];
        if (!node) {
            XD.msg('请选择节点');
            return;
        }
        if (grid.selModel.getSelectionLength() == 0) {
            XD.msg('请至少选择一条需要移交的数据');
            return;
        }else if(grid.selModel.getSelectionLength() >=1000&&!selectAll){
            XD.msg('暂不支持超过1000条目移交，可选择所有页进行操作');
            return;
        }
        var isSelectAll = false;
        if(selectAll){
            record = grid.acrossDeSelections;//取消选择的条目
            isSelectAll = true;
        }
        var tmp = [],delEntryIds=[];
        for (var i = 0; i < record.length; i++) {
            tmp.push(record[i].get('entryid'));
            if(selectAll) {
                delEntryIds.push(record[i].get('entryid'));
            }
        }
        var entryids = tmp.join(",");
        var tempParams = grid.getStore().proxy.extraParams;
        tempParams['entryids'] = entryids;
        tempParams['isSelectAll'] = isSelectAll;
        tempParams['totalCount'] = count;//条目总数
        tempParams['module'] = 'capture';//采集模块
        tempParams['isVolume'] = '';//是否为案卷
        tempParams['nodefullname'] =grid.nodefullname;//节点全称
        tempParams['transforType'] ="2";//节点全称

        var innergrid = this.findMainControl().findInnerGrid(btn);
        var tree=this.findMainControl().findGridView(btn).down('treepanel');
        var classnode=tree.selModel.getSelected().items[0].data.classlevel;

        //移交前先进性四性验证
        XD.confirm('移交前需要对这些数据进行四性验证', function () {
            Ext.MessageBox.wait('正在进行数据包安全认证...', '提示');
            Ext.Ajax.request({
                url: '/longRetention/verification',
                method: 'post',
                scope: this,
                params: tempParams,
                timeout: XD.timeout,
                success: function (res) {
                    XD.msg('执行验证成功');
                    Ext.MessageBox.hide();
                    var response = Ext.decode(res.responseText);
                    // var volumeEntryIds=response.data.join(",");
                    // entryids=entryids+","+volumeEntryIds;
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
                            grid:grid,
                            //volumeEntryIds:volumeEntryIds,
                            classnode:classnode,
                            innergrid:innergrid,
                            delEntryIds:delEntryIds,
                        }]
                    });
                    fsWin.on('close', function () {
                        grid.getStore().proxy.extraParams.entryids=null;
                    }, grid);
                    var fsGrid = fsWin.down('longRetentionAcGridView');
                    volumeNodeId=response.msg;
                    var fsarams=grid.getStore().proxy.extraParams;
                    fsGrid.getStore().setPageSize(XD.pageSize);
                    fsGrid.initGrid({nodeid:node.get('fnid'),transforType:"2",entryids:entryids,checkAll:isSelectAll,condition:fsarams.condition,operator:fsarams.operator,content:fsarams.content,
                        volumeNodeId:volumeNodeId});
                    fsWin.show();
                }
            });
        }, this);
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
        var win = Ext.create('Acquisition.view.transfor.LongRetentionDetailView');
        win.down('[itemId=closeBtn]').on('click', function () {
            win.close()
        });
        win.down('[itemId=authenticity]').html = record[0].data.authenticity;
        win.down('[itemId=integrity]').html = record[0].data.integrity;
        win.down('[itemId=usability]').html = record[0].data.usability;
        win.down('[itemId=safety]').html = record[0].data.safety;
        win.show();
    },

    transforTwoHandler: function (btn) {//验证后的移交
        var fsGrid = btn.up('longRetentionAcGridView');
        var selectAll=fsGrid.down('[itemId=selectAll]').checked;
        var count = fsGrid.getStore().getTotalCount();//总条目数
        var record = fsGrid.selModel.getSelection();
        if (record.length == 0&&!selectAll) {
            XD.msg('请选择一条需要移交的数据');
            return;
        }
        var entryids = [];
        var parententryids=[];//案卷条目
        var isSelectAll = false;
        var totalCount=record.length;
        if(selectAll){
            record = fsGrid.acrossDeSelections;//取消选择的条目
            isSelectAll = true;
            totalCount=count-totalCount;
            for(var j=0;j<fsGrid.delEntryIds.length;j++){
                entryids.push(fsGrid.delEntryIds[j]);
            }
        }
        for (var i = 0; i < record.length; i++) {
            if(!selectAll) {
                if (record[i].data.checkstatus.indexOf("不通过") > -1) {
                    XD.msg('请选择验证通过的数据');
                    return;
                }
            }
            entryids.push(record[i].get('entryid'));
            if(record[i].get('nodeid').trim()!=volumeNodeId){   //获取案卷条目id
                parententryids.push(record[i].get('entryid'))
            }
        }
        var gridParams=fsGrid.grid.getStore().proxy.extraParams;//列表检索条件
        var store = fsGrid.getStore();
        var tempParams=store.proxy.extraParams;
        if(gridParams['content']!=""&&tempParams['content']!="") {
            if (tempParams['content'] == "" || tempParams['content'] == null) {
                tempParams['condition'] = gridParams['condition'];
                tempParams['operator'] = gridParams['operator'];
                tempParams['content'] = gridParams['content'];
            } else if (gridParams['content'] != null&&gridParams['content']!="") {
                tempParams['condition'] += "," + gridParams['condition'];
                tempParams['operator'] += "," + gridParams['operator'];
                tempParams['content'] += "," + gridParams['content'];
            }
        }
        tempParams['entryids'] = entryids;
        tempParams['transforType']="";
        if(fsGrid.preview){
            tempParams['nodeid'] = fsGrid.preview.nodeid;
            tempParams['transforType']="1";
        }else {
            tempParams['nodeid'] =fsGrid.grid.findParentByType('acquisitionFormAndGrid').down('[itemId=treepanelId]').selection.get('fnid');
            tempParams['transforType']="2";
        }
        tempParams['isSelectAll'] = isSelectAll;
        tempParams['totalCount'] =totalCount;//条目总数
        var innergrid = fsGrid.innergrid;
        var docwin=new Ext.create({
            itemId:'transforWinId',
            modal: true,
            xtype:'acquisitionDocFormView',
            grid:fsGrid.grid,//加入移交列表
            preview:fsGrid.preview,//加入移交列表
            fsGrid:fsGrid,//验证列表
            tempParams:tempParams,
            innergrid:innergrid,
            parententryids:parententryids   //案卷条目id
        });//传递innergrid对象
        var classnode=fsGrid.classnode;
        if(classnode!=2) {
            //docwin.down('[itemId=isSynch]').hide();
        }
        var sendform=docwin.items.get('formitemid');
        Ext.MessageBox.wait('验证移交结果是否通过中...', '提示');
        sendform.load({
            url: '/acquisition/getNewDoc',
            params: tempParams,
            async:false,
            success: function (response) {
                Ext.MessageBox.hide();
                docwin.show();
                var approveNodeStore = sendform.down('[itemId=approveNodeId]').getStore();
                approveNodeStore.proxy.extraParams.workText = "采集移交审核";
                approveNodeStore.reload(); //加载数据
            },
            failure: function (response,opts) {
                Ext.MessageBox.hide();
                XD.msg("请选择验证通过的数据");
            }
        });
    },
    
    /**
     * 提交数据移交
     * @param btn
     */
    sendHandler:function (btn) {
        var transforcasign='0';
        if(_certEncode){//有证书编号
            transforcasign='1';
        }
        var grid;
        var nodeid;
        var transforType="";
        if(btn.findParentByType('acquisitionDocFormView').preview){
            grid = btn.findParentByType('acquisitionDocFormView').preview;
            nodeid= grid.nodeid;
            transforType="1"
        }else {
            grid = btn.findParentByType('acquisitionDocFormView').grid;
            nodeid= grid.findParentByType('acquisitionFormAndGrid').down('[itemId=treepanelId]').selection.get('fnid');
            transforType="2"
        }
        var fsGrid = btn.findParentByType('acquisitionDocFormView').fsGrid;
        var tempParams=btn.findParentByType('acquisitionDocFormView').tempParams;
        var condition="",operator="",content="";
        var spman = btn.findParentByType('acquisitionDocFormView').down('[itemId=spmanId]').getValue();
        var formitem=btn.findParentByType('acquisitionDocFormView').items.get('formitemid').getForm();
        var approvenodeid = btn.findParentByType('acquisitionDocFormView').down('[itemId=approveNodeId]').getValue();
        Ext.MessageBox.wait('正在移交数据请稍后...','提示');
        formitem.submit({
            scope:this,
            url: '/acquisition/sendformSubmit',
            params:{
                condition: condition,
                operator: operator,
                content: content,
                nodeid:nodeid,//案卷节点
                spman:spman,
                transforcasign:'',//移交用户数字证书
                editcasign:'',//审核用户数字证书
                approvenodeid:approvenodeid,
                volumeNodeId:volumeNodeId, //  卷内节点
            },
            success: function (response, opts) {
                var innergrid = btn.findParentByType('acquisitionDocFormView').innergrid;
                if(Ext.decode(opts.response.responseText).data.state == '已审核'){
                    XD.msg(Ext.decode(opts.response.responseText).msg);
                    btn.findParentByType('acquisitionDocFormView').close();
                    if(!innergrid.getCollapsed()){
                        innergrid.collapse();
                        innergrid.setTitle('查看卷内');
                        innergrid.notResetInitGrid();
                    }
                    return;
                }
                Ext.Ajax.request({
                    scope:this,
                    url: '/acquisition/entries/transfor',
                    timeout:XD.timeout,
                    params:{
                        //innserids:inner,
                        transdocid:Ext.decode(opts.response.responseText).data.docid,
                        condition: condition,
                        operator: operator,
                        content: content,
                        nodeid:nodeid,
                    },
                    success: function (response) {
                        Ext.MessageBox.hide();
                        XD.msg(Ext.decode(response.responseText).msg);
                        btn.findParentByType('acquisitionDocFormView').close();
                        var entryids;
                        if(grid.down('acquisitionPreviewTransEntryGridView')){//更新加入移交列表
                            grid.down('acquisitionPreviewTransEntryGridView').notResetInitGrid();
                            entryids=grid.down('acquisitionPreviewTransEntryGridView').dataParams.entryids;//选中移交的条目
                        }else {
                            entryids=grid.dataParams.entryids;//选中移交的条目
                        }
                        acquisitionGrid.notResetInitGrid();//更新采集列表
                        //更新验证列表
                        fsGrid.initGrid({nodeid:fsGrid.dataParams.nodeid,transforType:transforType,entryids:entryids,checkAll:fsGrid.dataParams.isSelectAll});//更新验证列表
                        if(!innergrid.getCollapsed()){
                            innergrid.collapse();
                            innergrid.notResetInitGrid();
                        }
                        //采集业务数据
                        captureServiceMetadataByYJ(entryids,'数据采集','移交',Ext.decode(opts.response.responseText).data.docid);
                    },
                    failure: function (response) {
                        Ext.MessageBox.hide();
                        XD.msg("移交成功，日志写入出错");
                    }
                });
            },
            failure: function (response, opts) {
                Ext.MessageBox.hide();
                var resp = Ext.decode(opts.response.responseText);
                if (resp.msg == '档号记录重复'||resp.msg =='卷内文件档号记录重复') {
                    XD.msg(resp.data);
                }else{
                    XD.msg(resp.msg);
                }
            }
        });
    },

    cancelHandler:function (btn) {
        var formview=btn.findParentByType('acquisitionDocFormView');
        formview.close();
    }
});

/** 移交
 *获取业务元数据
 * @param entryids 条目集合
 * @param module  模块名
 * @param operation 业务行为
 *
 * @returns {*}
 */
function captureServiceMetadataByYJ(entryids,module,operation,transdocid) {
    var r;
    Ext.Ajax.request({
        url: '/serviceMetadata/captureServiceMetadataByYJ',
        async:true,
        methods:'Post',
        params:{
            entryids:entryids,
            module:module,
            operation:operation,
            transdocid:transdocid

        },
        success: function (response) {
            r = Ext.decode(response.responseText);
            console.log(r.msg+",条目数："+r.data);
        }
    });
    return r;
}

function SignatureCreatorSuccessCallBack(res) {
    var result = "签名/章成功, 目标文件的Base64编码：\n" + res.destFileEncode;
    alert('签名/章成功');
    if(res.destFileEncode.length >= 20*1024*1024)
    {
        alert("注意：目标文件大于20M，会造成浏览器卡顿");
    }
    var pdfData=res.destFileEncode;

    //签章后打开pdf文件
    sessionStorage.setItem("_imgUrl", pdfData);
    var url = '../../../js/pdfJs/web/ureportviewer.html';
    window.open(url, '_blank');

    //签章后保存文件到服务器
    generatePdf(pdfData);

}
function SignatureCreatorFailedCallBack(res) {
    alert("签名/章失败 " + res.msg);
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
    /*if(caUserid==res.AppUsrCertNO){
        _sameName=1;//证书和用户一致
    }*/
    //alert("用户证书编码： "+res.certCode);
    getUserSealImage();//获取用户签章图片 Base64编码
}

function failedGetCertStringAttributeCallBack(res) {
    //alert(res.msg);
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
                _sealImageEncode=returnMsg.data.transforcaid;
                _sealImageEncode_edit=returnMsg.data.editcaid;
            }else{
                _sealImageEncode=undefined;
                _sealImageEncode_edit=undefined;
            }
            //alert(editSign);
        },
        failure:function(){
            //XD.msg('获取数字签章base64失败');
            _sealImageEncode=undefined;
            _sealImageEncode_edit=undefined;
        }
    });
}


function seal_SignSealPosition(_srcBytes,_xPos,_yPos,_width) {
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
            SignatureCreatorSuccessCallBack(res);
        })
        .Catch(function (res)
        {
            SignatureCreatorFailedCallBack(res);
        });
}


function generatePdf(pdfData){
    Ext.Ajax.request({
        url: '/acquisition/generatePdf',
        async:false,
        methods:'Post',
        params:{
            pdfData:pdfData,
            docid:window.docid,
            usrCertNO:_usrCertNO,
            type:2
        },
        success: function (response) {
            XD.msg('生成签章PDF成功');
        },
        failure:function(){
            XD.msg('生成签章PDF失败');
        }
    });
}