/**
 * Created by Rong on 2018/4/27.
 */
Ext.define('Qrcode.controller.QrcodeController',{
    extend: 'Ext.app.Controller',

    views: ['QrcodeView','ManagementFormView','StorageMsgView'],
    stores:['InwareStore','OutwareStore'],
    model:['InwareModel','OutwareModel'],

    init:function(){
        var that = this;
        this.control({
            'qrcodeView':{
                render:function(view){
                    //如果有档号值传入，就不是二维码读档页面
                    if(archivecode!=null){
                        var textfield = view.down('[itemId=qrcodeTxt]');
                        textfield.hide();//隐藏输入框
                        var form = view.down('dynamicform');
                        var permision=1;
                        //根据档号获取nodeid和entryid,再用他们去初始化表单
                        Ext.Ajax.request({
                            params: {dhCode: archivecode},
                            url: '/management/findidsAll',
                            method: 'post',
                            async: false,
                            success: function (response) {
                                var respText = Ext.JSON.decode(response.responseText);
                                //获取返回数据后加载表单
                                if (respText.success == true) {
                                    var entryids = [];
                                    var nodeid = respText.data[0].nodeid;
                                    Ext.each(respText.data,function (item) {
                                        entryids.push(item.entryid);
                                    })
                                    var initFormFieldState = that.initFormField(form, 'show', nodeid);//表单控件加载
                                    if(!initFormFieldState){//表单控件加载失败
                                        return;
                                    }
                                    form.entryids = entryids;
                                    form.entryid = entryids[0];
                                    that.initFormData('look', form, entryids[0]);//表单数据加载
                                    if(form.operateType){
                                        form.operateType = undefined;
                                    }
                                }
                            },
                            failure: function () {
                                XD.msg('nodeid获取中断');
                            }
                        });

                        if(permision==0){
                            return;
                        }
                        //库存信息加载
                        //按档号获取相应的entryid,按entryid找到相应的storage的stid，库存位置shid,根据stid获得相应的入库记录和出库记录
                        var shidTxtFile =view.down('[itemId=shidTxt]');
                        Ext.Ajax.request({
                            params: {dhCode: archivecode},
                            url: '/storage/shid',
                            method: 'post',
                            sync: true,
                            success: function (response) {
                                var respText = Ext.JSON.decode(response.responseText);
                                //获取返回数据后设置到文本框
                                if (respText.success == true) {
                                    var shidTxt=respText.msg;
                                    shidTxtFile.setValue(shidTxt);
                                }
                            },
                            failure: function () {
                                XD.msg('位置获取中断');
                            }
                        });
                        //加载表格内容
                        var inwaregrid =view.down('[itemId=inwaregrid]');
                        var outwaregrid =view.down('[itemId=outwaregrid]');
                        inwaregrid.getStore().load({
                            url:'/inware/inwares/'+archivecode
                        });
                        outwaregrid.getStore().load({
                            url:'/outware/outwares/'+archivecode
                        });
                    }
                }
            },
            'managementformView [itemId=preBtn]':{
            click:this.preHandler
            },
            'managementformView [itemId=nextBtn]':{
                click:this.nextHandler
            },
            'qrcodeView [itemId=qrcodeTxt]':{
                change : function(field,newValue,oldValue){//输入框监听
                    var dhCode= newValue;
                    if(''==dhCode || dhCode==null){XD.msg('请先扫码一个档号');return;}
                    var form = field.up('qrcodeView').down('dynamicform');
                    var permision=1;
                    //根据档号获取nodeid和entryid,再用他们去初始化表单
                    Ext.Ajax.request({
                        params: {dhCode: dhCode},
                        url: '/management/findids',
                        method: 'post',
                        async: false,
                        success: function (response) {
                            var respText = Ext.JSON.decode(response.responseText);
                            //获取返回数据后加载表单
                            if (respText.success == true) {
                                var ids=respText.msg;
                                if(ids=='0'){//没有权限
                                    permision=0;
                                    XD.msg('对不起，您没有权限查看这个档案');
                                    return;
                                }else if(ids==''){
                                    permision=0;
                                    XD.msg('档号不存在');
                                    return;
                                }
                                var entryids = [];
                                var arr = ids.split("-");
                                for(var i=0;i<arr.length;i++){
                                    entryids.push(arr[i]);
                                }
                                form.entryids=entryids;
                                var entryid=ids.substring(0,ids.indexOf('-'));
                                var nodeid=ids.substring(ids.indexOf('-')+1);
                                var initFormFieldState = that.initFormField(form, 'show', nodeid);//表单控件加载
                                if(!initFormFieldState){//表单控件加载失败
                                    return;
                                }
                                that.initFormData('look', form, entryid);//表单数据加载
                                if(form.operateType){
                                    form.operateType = undefined;
                                }
                            }
                        },
                        failure: function () {
                            XD.msg('nodeid获取中断');
                        }
                    });

                    if(permision==0){
                       return;
                    }
                    //库存信息加载
                    //按档号获取相应的entryid,按entryid找到相应的storage的stid，库存位置shid,根据stid获得相应的入库记录和出库记录
                    var shidTxtFile =field.up('qrcodeView').down('[itemId=shidTxt]');
                    Ext.Ajax.request({
                        params: {dhCode: dhCode},
                        url: '/storage/shid',
                        method: 'post',
                        sync: true,
                        success: function (response) {
                            var respText = Ext.JSON.decode(response.responseText);
                            //获取返回数据后设置到文本框
                            if (respText.success == true) {
                                var shidTxt=respText.msg;
                                shidTxtFile.setValue(shidTxt);
                            }
                        },
                        failure: function () {
                            XD.msg('位置获取中断');
                        }
                    });
                    //加载表格内容
                    var inwaregrid =field.up('qrcodeView').down('[itemId=inwaregrid]');
                    var outwaregrid =field.up('qrcodeView').down('[itemId=outwaregrid]');
                    inwaregrid.getStore().load({
                        url:'/inware/inwares/'+dhCode
                    });
                    outwaregrid.getStore().load({
                        url:'/outware/outwares/'+dhCode
                    });



                }
            },
            /*'managementformView': {
                tabchange: function (view) {
                    //先获取二维码传入的档号信息
                    var codeTextFile=view.up('qrcodeView').down('[itemId=qrcodeTxt]');
                    var dhCode=codeTextFile.getValue();
                    if(''==dhCode || dhCode==null){XD.msg('请先扫码一个档号');return;}
                    if (view.activeTab.title == '库存信息') {

                        //按档号获取相应的entryid,按entryid找到相应的storage的stid，库存位置shid,根据stid获得相应的入库记录和出库记录
                        var shidTxtFile =view.up('qrcodeView').down('[itemId=shidTxt]');
                        Ext.Ajax.request({
                            params: {dhCode: dhCode},
                            url: '/storage/shid',
                            method: 'post',
                            sync: true,
                            success: function (response) {
                                var respText = Ext.JSON.decode(response.responseText);
                                //获取返回数据后设置到文本框
                                if (respText.success == true) {
                                    var shidTxt=respText.msg;
                                    shidTxtFile.setValue(shidTxt);
                                }
                            },
                            failure: function () {
                                XD.msg('位置获取中断');
                            }
                        });
                        //加载表格内容
                        var inwaregrid =view.up('qrcodeView').down('[itemId=inwaregrid]');
                        var outwaregrid =view.up('qrcodeView').down('[itemId=outwaregrid]');
                        inwaregrid.getStore().load({
                            url:'/inware/inwares/'+dhCode
                        });
                        outwaregrid.getStore().load({
                            url:'/outware/outwares/'+dhCode
                        });

                       /!* var gridcard=view.down('storageMsgView');
                        gridcard.initGrid({nodeid:'4028802f5f90a1f7015f9102d29f00b0'});
                        gridcard.getStore().reload();*!/
                    }else if(view.activeTab.title == '条目'){

                        var form = view.up('qrcodeView').down('dynamicform');
                        //根据档号获取nodeid和entryid,再用他们去初始化表单
                        Ext.Ajax.request({
                            params: {dhCode: dhCode},
                            url: '/management/findids',
                            method: 'post',
                            sync: true,
                            success: function (response) {
                                var respText = Ext.JSON.decode(response.responseText);
                                //获取返回数据后加载表单
                                if (respText.success == true) {
                                    var ids=respText.msg;
                                    var entryid=ids.substring(0,ids.indexOf('-'));
                                    var nodeid=ids.substring(ids.indexOf('-')+1);
                                    var initFormFieldState =that.initFormField(form, nodeid);
                                    if(!initFormFieldState){//表单控件加载失败
                                        return;
                                    }
                                    that.initFormData('modify', form, entryid);
                                    if(form.operateType){
                                        form.operateType = undefined;
                                    }
                                }
                            },
                            failure: function () {
                                XD.msg('位置获取中断');
                            }
                        });
                    }
                }
            },*/
        });
    },
    //点击上一条
    preHandler:function(btn){
        var currentManagementform = btn.up('managementformView');
        var form = currentManagementform.down('dynamicform');
        this.preNextHandler(form, 'pre');
    },
    //点击下一条
    nextHandler:function(btn){
        var currentManagementform = btn.up('managementformView');
        var form = currentManagementform.down('dynamicform');
        this.preNextHandler(form, 'next');
    },
    //条目切换，上一条下一条
    preNextHandler:function(form,type){
        form.operate = 'look';
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
                    type: formview.findParentByType('managementform').operateFlag,
                    eleid: formview.findParentByType('managementform').down('electronic').getEleids(),
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
                    url: '/management/entries',
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
    findFormView: function (btn) {
        return this.findView(btn).down('formAndGrid').down('managementformView');
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
            form.initField(formField,operate,'capture');//重新动态添加表单控件
//        }
        return '加载表单控件成功';
    },

    initFormData:function(operate, form, entryid, state){
        form.reset();
        var eleview = this.findFormView(form).down('electronic');//原文
        var count = 1;
        if(operate == 'modify' || operate == 'look'){
            for(var i=0;i<form.entryids.length;i++){
                if(form.entryids[i]==entryid){
                    count=i+1;
                    break;
                }
            }
            var total = form.entryids.length;
            var totaltext = form.down('[itemId=totalText]');
            totaltext.setText('当前共有  ' + total + '  条，');
            var nowtext = form.down('[itemId=nowText]');
            nowtext.setText('当前记录是第  ' + count + '  条');
        }
        if(operate!='look'){
            var settingState = this.ifSettingCorrect(form.nodeid,form.templates);
            if(!settingState){
                eleview.initData('');//清空
                return;
            }
        }
        //this.activeForm(form);
        //var eleview = this.findFormView(form).down('electronic');
        //固化var solidview = this.findFormView(form).down('solid');
        //长期var longview = this.findFormView(form).down('long');
        if(typeof(state) == 'undefined'){
            Ext.Ajax.request({
                method: 'GET',
                scope: this,
                url: '/management/entries/' + entryid,
                success: function (response) {
                    var entry = Ext.decode(response.responseText);
                    if(operate == 'add'){
                        delete entry.entryid;
                        entry.filingyear = new Date().getFullYear();
                        entry.descriptiondate = Ext.util.Format.date(new Date(),'Y-m-d H:i:s');
                        entry.organ = Ext.decode(response.responseText).organ;//机构
                        entry.keyword = Ext.decode(response.responseText).keyword;//主题词
                        Ext.Ajax.request({
                            async:false,
                            url: '/user/getUserRealname',
                            success:function (response) {
                                entry.descriptionuser = Ext.decode(response.responseText).data;
                            }
                        });
                    }
                    if(operate == 'insertion'){
                        this.entryID = entryid;
                        delete entry.entryid;
                    }
                    form.loadRecord({getData: function () {return entry;}});
                    var fieldCode = form.getRangeDateForCode();//字段编号，用于特殊的自定义字段(范围型日期)
                    if (fieldCode != null) {
                        //动态解析数据库日期范围数据并加载至两个datefield中
                        form.initDaterangeContent(entry);
                    }
                    eleview.initData(entry.entryid);
                    //solidview.initData(entry.entryid);
                    //longview.initData(entry.entryid);
                }
            });
        } /*else {
            var filingyearField = form.getForm().findField('filingyear');
            var descriptiondateField = form.getForm().findField('descriptiondate');
            var descriptionuserField = form.getForm().findField('descriptionuser');
            if(filingyearField){
                filingyearField.setValue(new Date().getFullYear());
            }
            if(descriptiondateField){
                descriptiondateField.setValue(Ext.util.Format.date(new Date(),'Y-m-d H:i:s'));
            }
            if(descriptionuserField){
                Ext.Ajax.request({
                    async:false,
                    url: '/user/getUserRealname',
                    success:function (response) {
                        descriptionuserField.setValue(Ext.decode(response.responseText).data);
                    }
                });
            }
            if (state == '卷内') {
                //初始化加载卷内文件信息(案卷号/全宗号/属类号/责任者)
                var filecodeField = form.getForm().findField('filecode');//案卷号
                var fundsField = form.getForm().findField('funds');//全宗号
                var catalogField = form.getForm().findField('catalog');//属类号
                var responsibleField = form.getForm().findField('responsible');//责任者

                Ext.Ajax.request({
                    scope:this,
                    async:false,
                    method: 'GET',
                    url: '/management/entries/' + entryid,//通过上级表单选择的数据的id获取所有信息
                    success:function (response) {
                        //填充数据
                        filecodeField.setValue(Ext.decode(response.responseText).filecode);
                        fundsField.setValue(Ext.decode(response.responseText).funds);
                        catalogField.setValue(Ext.decode(response.responseText).catalog);
                        responsibleField.setValue(Ext.decode(response.responseText).responsible);
                    }
                });
            }
            if (entryid == '' && state == '') {
                var refid;
                var organField = form.getForm().findField('organ');//机构
                var fundsField = form.getForm().findField('funds');//全宗号
                Ext.Ajax.request({
                    async:false,
                    method: 'post',
                    url: '/nodesetting/getRefid/' + form.nodeid,//通过节点id获取机构id
                    success:function (response) {
                        var msg = Ext.decode(response.responseText).msg;
                        var data = Ext.decode(response.responseText).data;
                        if (msg == 'success') {
                            var info = data.split(",");
                            if (organField) {
                                organField.setValue(info[0]);//填充机构名称
                            }
                            if (fundsField) {
                                fundsField.setValue(info[1]);//填充全宗号
                            }
                        } else if (msg == 'success-organ') {
                            organField.setValue(data);
                        } else {
                            fundsField.setValue(data);
                        }
                    },
                    failure:function (response) {
                        XD.msg("请检查数据信息是否正确");
                    }
                });
            }
            eleview.initData();
            solidview.initData();
            longview.initData();
        }*/
//        form.formStateChange(operate);
        form.fileLabelStateChange(eleview,operate);
        /*form.fileLabelStateChange(solidview,operate);
        form.fileLabelStateChange(longview,operate);
        this.changeBtnStatus(form,operate);*/
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

    ifCodesettingCorrect:function (nodeid) {
        var codesetting = [];
        Ext.Ajax.request({
            url: '/codesetting/getCodeSettingFields',
            async:false,
            params:{
                nodeid:nodeid
            },
            success: function (response) {
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

    //切换到表单界面视图
    activeForm:function(btn){
        var view = this.findView(btn);
        var formview = this.findFormView(btn);
        view.setActiveItem(formview);
        formview.items.get(0).enable();
        formview.setActiveTab(0);
        return formview;
    },

    //获取数据管理应用视图
    findView:function(btn){
        return btn.findParentByType('qrcodeView');
    },

    //获取表单界面视图
    findFormView:function(btn){
        return this.findView(btn).down('managementformView');
    },

})