/**
 * Created by Rong on 2017/10/24.
 * 数据采集控制器
 */
var win;//数据查重之后用
var _certEncode;              //签名证书Base64编码
var _sealImageEncode;        //签章图片Base64编码  移交
var _sealImageEncode_edit;        //签章图片Base64编码  审核
var _srcBytes;              //pdf文件Base64编码
var _sameName;//本机所用的数字证书和用户绑定证书一致
var _usrCertNO;//数字证书编号
var pwdOk=false;
Ext.define('Audit.controller.AuditController',{
    extend: 'Ext.app.Controller',

    views:[
        'AuditAdminView','AuditDocTreeView','AuidtAdminGridView','AuditDocEntryGridView',
        'AuditFormView', 'SendbackreasonFormView'
    ],
    models:[
        'AuditDocTreeModel','AuditAdminGridModel'
    ],
    stores:[
        'AuditDocTreeStore','AuditAdminGridStore'
    ],

    init:function(){
        var count = 0;
        this.control({
            'auditAdminView': {
                render: function (view) {
                    view.down('auditDocTreeView').on('render', function (view) {
                        view.getSelectionModel().select(view.getRootNode().childNodes[0]);//默认选中第一个
                    });
                }
            },
            'auditDocTreeView': {
                render: function (view) {
                    view.getRootNode().on('expand', function (node) {
                        if (node.getOwnerTree().getSelectionModel().selected.length == 0) {
                            node.getOwnerTree().getSelectionModel().select(node.firstChild);
                        }
                    })
                },
                select: function (treemodel, record) {
                    if (record.get('leaf')) {
                        var auditAdminView = treemodel.view.findParentByType('auditAdminView');
                        var auidtAdminGridView = auditAdminView.down('auidtAdminGridView');
                        var treeview = auditAdminView.down('auditDocTreeView');
                        var treetext = treeview.getSelectionModel().getSelected().items[0].get('text');
                        var buttons = auidtAdminGridView.down("toolbar").query('button');
                        var tbseparator = auidtAdminGridView.down("toolbar").query('tbseparator');
                        var state = "待审核";
                        this.refreshToolbarBtnShowAll(auidtAdminGridView);
                        auidtAdminGridView.down('[itemId=approvemanId]').show();
                        auidtAdminGridView.down('[itemId=approvetimeId]').show();
                        if(netcatUse == '0'){//设置隐藏签章按钮和签章显示列
                            hideToolbarBtnTbsByItemId('signId',buttons,tbseparator);
                            auidtAdminGridView.down('[itemId=transforcasignId]').hide();
                            auidtAdminGridView.down('[itemId=editcasignId]').hide();
                        }
                        if(treetext.indexOf("入库")!=-1){
                            state = '已审核';
                            //隐藏办理按钮
                            hideToolbarBtnTbsByItemId('auditDeal',buttons,tbseparator);
                            //隐藏查看退回原因按钮
                            hideToolbarBtnTbsByItemId('lookBack',buttons,tbseparator);
                        }else if(treetext.indexOf("退回")!=-1){
                            state = '已退回';
                            //隐藏办理按钮
                            hideToolbarBtnTbsByItemId('auditDeal',buttons,tbseparator);
                        }else{
                            //待审核隐藏审核人以及审核时间列
                            auidtAdminGridView.down('[itemId=approvemanId]').hide();
                            auidtAdminGridView.down('[itemId=approvetimeId]').hide();
                            if(functionButton.length<1){   //没有办理按钮的功能权限
                                //隐藏办理按钮
                                hideToolbarBtnTbsByItemId('auditDeal',buttons,tbseparator);
                            }
                            //隐藏查看退回原因按钮
                            hideToolbarBtnTbsByItemId('lookBack',buttons,tbseparator);
                        }
                        auidtAdminGridView.initGrid({state: state});
                        if(netcatUse=='1'){//已启用签章配置
                            if(_certEncode){//没有证书编号的时候，读取一下
                            }else{
                                getUserCert();//获取用户证书 Base64编码
                            }
                        }
                    }
                }
            },

            //查看与单据相关联的条目详细内容
            'auidtAdminGridView button[itemId=lookAuditDoc]': {
                click: function (view) {
                    var docGrid = view.findParentByType('auidtAdminGridView');
                    var record = docGrid.getSelectionModel().getSelection();
                    if (record.length != 1) {
                        XD.msg('请选择一条需要查看的单据');
                        return;
                    }
                    var docid = record[0].get('docid');
                    var docState = record[0].get('state');
                    var nodeid = record[0].get('nodeid');
                    var entryGridWin = Ext.create('Ext.window.Window', {
                        modal: true,
                        width: 900,
                        height: 530,
                        title: '查看单据记录',
                        layout: 'fit',
                        closeToolText: '关闭',
                        closeAction: 'hide',
                        items: [{
                            xtype: 'auditDocEntryGridView'
                        }]
                    });
                    var entryGrid = entryGridWin.down("auditDocEntryGridView");
                    var params = {
                        docid: docid,
                        docState: docState,
                        nodeid: nodeid//请求模板数据，显示列表
                    };
                    var buttons = entryGrid.down("toolbar").query('button');
                    var tbseparator = entryGrid.down("toolbar").query('tbseparator');
                    if (docState == '已审核') {
                        buttons[1].hide();
                        tbseparator[0].hide();
                    } else {
                        buttons[1].show();
                        tbseparator[0].show();
                    }
                    entryGrid.initGrid(params);
                    entryGridWin.show();
                }
            },

            'auditDocEntryGridView button[itemId=back]':{ //查看单据记录 返回
                click:function (view) {
                    view.findParentByType('window').close();
                }
            },

            'auditDocEntryGridView button[itemId=look]':{ //查看单据记录 查看
                click:function (view) {
                    var entryGrid = view.findParentByType('auditDocEntryGridView');
                    var record = entryGrid.getSelectionModel().getSelection();
                    if (record.length == 0) {
                        XD.msg('请至少选择一条需要查看的数据');
                        return;
                    }
                    var nodeid = record[0].get('nodeid');
                    var entryids = [];
                    for(var i=0;i<record.length;i++){
                        entryids.push(record[i].get('entryid'));
                    }
                    var entryFormWin = Ext.create('Ext.window.Window', {
                        modal: true,
                        width: '100%',
                        height: '100%',
                        header:false,
                        layout: 'fit',
                        closeToolText: '关闭',
                        closeAction: 'hide',
                        items: [{
                            xtype: 'AuditFormView'
                        }]
                    });
                    var form = entryFormWin.down('dynamicform');
                    var entryid = record[0].get('entryid');
                    var initFormFieldState = this.initFormField(form, 'hide', nodeid);
                    form.operate = 'look';
                    form.entryids = entryids;
                    form.entryid = entryids[0];
                    if(!initFormFieldState){//表单控件加载失败
                        return;
                    }
                    this.initFormData(form, 'look', entryid,nodeid);
                    entryFormWin.show();
                }
            },

            'AuditFormView [itemId=back]' : {//返回
                click:function(btn){
                    btn.findParentByType("window").close();
                }
            },

            //查看退回原因
            'auidtAdminGridView button[itemId=lookBack]': {
                click: function (view) {
                    var docGrid = view.findParentByType('auidtAdminGridView');
                    var record = docGrid.getSelectionModel().getSelection();
                    if (record.length != 1) {
                        XD.msg('请选择一条需要查看的单据');
                        return;
                    }
                    var docid = record[0].get('docid');
                    var sendbackreasonWin = Ext.create('Ext.window.Window',{//弹出窗口，填写退回原因
                        width:400,
                        height:200,
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

            'auidtAdminGridView [itemId=print]':{                // 打印
                click:this.printTransdocHandler
            },

            'auidtAdminGridView [itemId=signId]': {      //签章
                click: this.caSign
            },

            'sendbackreasonFormView [itemId=back]' : {// 查看退回原因 返回
                click:function(btn){
                    btn.findParentByType("window").close();
                }
            },

            //办理
            'auidtAdminGridView button[itemId=auditDeal]': {
                click: function (view) {
                    var docGrid = view.findParentByType('auidtAdminGridView');
                    var record = docGrid.getSelectionModel().getSelection();
                    if (record.length != 1) {
                        XD.msg('请选择一条需要办理的单据');
                        return;
                    }
                    var docid = record[0].get('docid');
                    var taskid = this.getTaskid(docid);
                    var html = '<iframe id="frame1" src="/audit/mainDeal?flag=2&taskid=' + taskid + '"  frameborder="0" width="100%" height="100%"></iframe>';
                    window.auditApprove = Ext.create("Ext.window.Window", {
                        width: '100%',
                        height: '100%',
                        modal: true,
                        header: false,
                        draggable: false,//禁止拖动
                        resizable: false,//禁止缩放
                        html:html,
                        docGrid:docGrid
                    });
                    window.auditApprove.show();
                }
            },

            'AuditFormView [itemId=preBtn]':{
                click:this.preHandler
            },
            'AuditFormView [itemId=nextBtn]':{
                click:this.nextHandler
            },
        })
    },

    initFormField:function(form, operate, nodeid){
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
    getCurrentAuditform:function (btn) {
        return btn.up('AuditFormView');
    },

    //点击上一条
    preHandler:function(btn){
        var currentAuditform = this.getCurrentAuditform(btn);
        var form = currentAuditform.down('dynamicform');
        this.preNextHandler(form, 'pre');
    },

    //点击下一条
    nextHandler:function(btn){
        var currentAuditform = this.getCurrentAuditform(btn);
        var form = currentAuditform.down('dynamicform');
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

    initFormData:function(form, operate, entryid,nodeid){
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
        var currentForm = formview;
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

        var eleview = formview.down('electronic');
        var solidview = formview.down('solid');
        Ext.Ajax.request({
            method: 'GET',
            scope: this,
            url: '/audit/entries/' + entryid,
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
                eleview.initData(entry.entryid);
                solidview.entrytype = 'solid';
                solidview.initData(entry.entryid);
            }
        });
        form.fileLabelStateChange(eleview,operate);
        form.fileLabelStateChange(solidview,operate);
    },

    //条目切换，上一条下一条
    preNextHandler:function(form,type){
        this.refreshFormData(form, type);
    },

    //获取任务id
    getTaskid:function (docid) {
        var taskid;
        Ext.Ajax.request({
            url: '/audit/getTaskid',
            async:false,
            params:{
                docid:docid
            },
            success: function (response) {
                taskid = Ext.decode(response.responseText);
            }
        });
        return taskid;
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
    //显示所有
    showAll:function (hideType) {
        for(var num in hideType){
            hideType[num].show();
        }
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
        var docGrid = btn.up('auidtAdminGridView');
        var record = docGrid.getSelectionModel().getSelection();
        if(record.length!==1){
            XD.msg('请选择一条需要打印的移交单据');
            return;
        }
        Ext.each(record,function(){
            if(this.get('editcasign')=='Y'||this.get('editcasign')=='0'){
                ureportName='移交单据管理';
            }
            ids.push(this.get('docid').trim());
            states.push(this.get('state'));
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
            if(pdfData){
                sessionStorage.setItem("_imgUrl", pdfData);
                var url = '../../../js/pdfJs/web/ureportviewer.html';
                window.open(url, '_blank');
            }else{
                XD.msg('签章文件没找到');
            }
            return;
        }

        if(reportServer == 'UReport') {
            params['docid'] = ids.join(",");
            XD.UReportPrint(null, ureportName, params);
        } else if(reportServer == 'FReport'){
            XD.FRprint(null, ureportName, ids.length > 0 ? "'docid':'" + ids.join(",")+"'": '');
        }
    },

    //签章
    caSign: function (view) {
        var docGrid = view.up('auidtAdminGridView');
        var record = docGrid.getSelectionModel().getSelection();
        if(record.length!==1){
            XD.msg('请选择一条需要签章的移交单据');
            return;
        }
        if(record[0].get("state")!="已审核"||record[0].get("transforcasign")!="Y"){
            XD.msg('请选择已审核的数据且已进行移交签章的数据进行签章!');
            return;
        }
        //Ext.MessageBox.wait('正在处理请稍后...');
        var signPrint=0;//移交签章标记  0没有 1有
        if(_certEncode) {//有数字证书的才进行报表签章
            if(_sealImageEncode&&_sealImageEncode.length>200){//有移交签章才进行签章打印
                signPrint=1;
            }
        }else{
            XD.msg('请确定已插上个人数字证书!');
            return;
        }
        if(_certEncode&&signPrint==1) {//有证书存在且有签章存在
            if(_certEncode/*&&_sameName==2*/){//证书拥有者的操作才进行报表签章
                window.docid=record[0].get("docid").trim();
                if(record[0].get('editcasign')!='Y'&&record[0].get('editcasign')!='0') {//重新生成有验证项打钩的pdf，再盖章;
                    Ext.Ajax.request({
                        url: '/audit/updateTrandoc',
                        params: {
                            docid: window.docid.trim(),
                            type: "2"
                        },
                        method: 'POST',
                        async: false,
                        success: function (resp) {
                        }
                    });
                    Ext.Ajax.request({//获取pdf报表的base64编码
                        url: '/acquisition/getUreportPdf',
                        params: {
                            docids: window.docid.trim(),
                            reportName: encodeURI('移交单据管理')
                        },
                        async: false,
                        method: 'POST',
                        success: function (resp) {//得到pdf报表的base64编码
                            XD.msg(Ext.decode(resp.responseText).msg);
                            _srcBytes = Ext.decode(resp.responseText).data;//报表的base64编码
                            seal_SignSealPosition(_srcBytes, 200, 682, 89, docGrid, 1);
                            // setTimeout(function() {
                            //     Ext.Ajax.request({
                            //         url: '/audit/updateTrandoc',
                            //         params: {
                            //             docid: window.docid.trim(),
                            //             type: "2",
                            //             pwdno: "1"
                            //         },
                            //         method: 'POST',
                            //         async: false,
                            //         success: function (resp) {
                            //             docGrid.getStore().reload();
                            //         }
                            //     });
                            // }, 1500);
                        },
                        failure: function () {
                            XD.msg('表单生成失败');
                        }
                    });
                }else{
                    //获取移交签章后的pdf文件流
                    var pdfData = getFileBase64(window.docid);
                    if (pdfData) {
                        //对PDF签章，签章成功后再打开pdf页面
                        seal_SignSealPosition(pdfData, 415, 682, 89, docGrid,2);
                    } else {
                        XD.msg('签章文件没找到');
                    }
                }
            }
        }else{
            XD.msg('请确定个人数字证书有绑定签章!');
            return;
        }
    },

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

function SignatureCreatorSuccessCallBack(res,docGrid,type) {
    var pdfData = res.destFileEncode;
    pwdOk=true;
    //签章后保存文件到服务器
    generatePdf(pdfData);
    if(type==2) {//第二次盖章直接打开
        var result = "签名/章成功, 目标文件的Base64编码：\n" + res.destFileEncode;
        alert('签名/章成功');
        if (res.destFileEncode.length >= 20 * 1024 * 1024) {
            alert("注意：目标文件大于20M，会造成浏览器卡顿");
        }

        //签章后打开pdf文件
        sessionStorage.setItem("_imgUrl", pdfData);
        var url = '../../../js/pdfJs/web/ureportviewer.html';
        window.open(url, '_blank');
        docGrid.getStore().reload();
    }else {
        var pdfData = getFileBase64(window.docid);
        //getSigncode(window.docid);
        seal_SignSealPosition(pdfData, 415, 682, 89, docGrid,2);
    }
}

function SignatureCreatorFailedCallBack(res,docGrid) {
    pwdOk=false;
    Ext.Ajax.request({
        url: '/audit/updateTrandoc',
        params: {
            docid: window.docid.trim(),
            type: "2",
            pwdno:"1"
        },
        method: 'POST',
        async: false,
        success: function (resp) {
            docGrid.getStore().reload();
        }
    });
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
            docid:docid.trim()
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

function seal_SignSealPosition(_srcBytes,_xPos,_yPos,_width,docgrid,type) {
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
            SignatureCreatorSuccessCallBack(res,docgrid,type);
        })
        .Catch(function (res)
        {
            SignatureCreatorFailedCallBack(res,docGrid);
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