var userFieldCode = "";
var tempParams;
var userFieldName=[];
Ext.define('Equipment.controller.EquipmentController',{
    extend: 'Ext.app.Controller',
    views:['EquipmentGridView','EquipmentAddView','EquipmentDefendAddView','EquipmentExportSetView','EquipmentImportView',
        'EquipmentDefendGridView','EquipmentManageView','EquipmentExportWin'],
    stores:['EquipmentGridStore','EquipmentDefendGridStore'],
    models:['EquipmentGridModel','EquipmentDefendGridModel'],
    init:function () {
        this.control({
            'equipmentManageView': {
                afterrender: function (view) {
                    var equipmentGridView = view.down('equipmentGridView');
                    equipmentGridView.initGrid();
                }
            },
            // 'equipmentGridView':{
            //     afterrender:function (view) {
            //         view.initGrid();
            //     }
            // },
            'equipmentGridView button[itemId=add]':{//新增设备
                click:function (btn) {
                    this.showAddView(btn,'新增');
                }
            },
            'equipmentGridView button[itemId=edit]':{//设备信息修改
                click:function (btn) {
                    var view = btn.findParentByType('equipmentGridView');
                    var select = view.getSelectionModel();
                    if (select.getCount() != 1){
                        XD.msg('请选择一条数据！');
                        return ;
                    }
                    this.showAddView(btn,'修改');
                }
            },
            'equipmentGridView button[itemId=delete]':{//删除设备信息
                click:function (btn) {
                    var view = btn.findParentByType('equipmentGridView');
                    var select = view.getSelectionModel();
                    if (select.getCount() < 1){
                        XD.msg('请选择数据！');
                        return ;
                    }
                    var store = select.selected.items;
                    var equipmentIDs = [];
                    for (var i = 0; i < store.length; i++) {
                        equipmentIDs.push(store[i].get('equipmentID'));
                    }
                    Ext.Ajax.request({
                        url:'/equipment/deleteEquipment',
                        method:'POST',
                        params:{
                            equipmentIDs:equipmentIDs
                        },
                        success:function (resp) {
                            var responseText = Ext.decode(resp.responseText);
                            XD.msg(responseText.msg);
                            if (responseText.msg == '删除成功！'){
                                view.delReload(select.getCount());
                            }else{
                                return;
                            }
                        },
                        failure:function () {
                            XD.msg('操作失败！');
                        }
                    });
                }
            },
            'equipmentGridView button[itemId=look]':{//查看设备信息
                click:function (btn) {
                    var view = btn.findParentByType('equipmentGridView');
                    var select = view.getSelectionModel();
                    if (select.getCount() != 1){
                        XD.msg('请选择一条数据！');
                        return ;
                    }
                    this.showAddView(btn,'查看');
                }
            },
            'equipmentAddView button[itemId=close]':{//新增设备的关闭
                click:function (btn) {
                    var view = btn.findParentByType('equipmentAddView');
                    view.close();
                }
            },
            'equipmentAddView button[itemId=submit]':{//新增设备的提交
                click:function (btn) {
                    this.submitEquipment(btn);
                }
            },
            'equipmentGridView button[itemId=defendequipment]': {  //维护记录
                click: function (view) {
                    var equipmentGridView = view.findParentByType('equipmentGridView');
                    var equipmentManageView = equipmentGridView.findParentByType('equipmentManageView');
                    var equipmentDefendGridView = equipmentManageView.down('equipmentDefendGridView');
                    var select = equipmentGridView.getSelectionModel().getSelection();
                    if(select.length !=1 ){
                        XD.msg('只能选择一条记录');
                        return;
                    }
                    equipmentDefendGridView.initGrid({equipmentid:select[0].get('equipmentID')});
                    equipmentDefendGridView.equipmentId = select[0].get('equipmentID');
                    equipmentManageView.setActiveItem(equipmentDefendGridView);
                }
            },
            'equipmentDefendGridView button[itemId=addDefend]':{ // 新增维护记录
                click:function (view) {
                    var equipmentDefendGridView = view.findParentByType('equipmentDefendGridView');
                    var equipmentDefendAddView = Ext.create('Equipment.view.EquipmentDefendAddView');
                    equipmentDefendAddView.equipmentDefendGridView = equipmentDefendGridView;
                    var form = equipmentDefendAddView.down('form');
                    form.load({
                        url:'/equipment/loadEquipmentDefend',
                        method:'POST',
                        success:function (form,action) {
                        },
                        failure:function () {
                            XD.msg('操作失败');
                        }
                    });
                    equipmentDefendAddView.equipmentId = equipmentDefendGridView.equipmentId;
                    equipmentDefendAddView.show();
                }
            },
            'equipmentDefendAddView button[itemId=defendSubmit]':{ // 新增维护记录 提交
                click:function (view) {
                    var equipmentDefendAddView = view.findParentByType('equipmentDefendAddView');
                    var form = equipmentDefendAddView.down('form');
                    if(!form.isValid()){
                        XD.msg('存在必填项未填写');
                        return;
                    }
                    form.submit({
                        url:'/equipment/equipmentDefendSubmit',
                        method:'POST',
                        params:{
                            equipmentId:equipmentDefendAddView.equipmentId
                        },
                        scope: this,
                        success:function (form,action) {
                            if(equipmentDefendAddView.title=='修改维护记录'){
                                XD.msg('修改成功');
                            }else{
                                XD.msg('新增成功');
                            }
                            equipmentDefendAddView.equipmentDefendGridView.getStore().reload();
                            equipmentDefendAddView.close();
                        },
                        failure:function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },
            'equipmentDefendGridView button[itemId=editDefend]':{  //修改维护记录
                click:function (view) {
                    var equipmentDefendGridView = view.findParentByType('equipmentDefendGridView');
                    var select = equipmentDefendGridView.getSelectionModel().getSelection();
                    if(select.length!=1){
                        XD.msg('只能选择一条数据');
                        return;
                    }
                    var equipmentDefendAddView = Ext.create('Equipment.view.EquipmentDefendAddView');
                    var form = equipmentDefendAddView.down('form');
                    form.load({
                        url:'/equipment/getEquipmentDefendById',
                        method:'POST',
                        params:{
                            id:select[0].get('id')
                        },
                        success:function (form,action) {
                            var respText = Ext.decode(action.response.responseText);
                            if(!respText.success){
                                XD.msg('获取表单信息失败');
                            }
                            equipmentDefendAddView.setTitle('修改维护记录');
                            equipmentDefendAddView.equipmentId = equipmentDefendGridView.equipmentId;
                            equipmentDefendAddView.equipmentDefendGridView = equipmentDefendGridView;
                            equipmentDefendAddView.show();
                        },
                        failure:function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },
            'equipmentDefendAddView button[itemId=defendClose]':{  // 新增 关闭
                click:function (view) {
                    view.findParentByType('equipmentDefendAddView').close();
                }
            },
            'equipmentDefendGridView button[itemId=lookDefend]':{  //查看维护记录
                click:function (view) {
                    var equipmentDefendGridView = view.findParentByType('equipmentDefendGridView');
                    var select = equipmentDefendGridView.getSelectionModel().getSelection();
                    if(select.length!=1){
                        XD.msg('只能选择一条数据');
                        return;
                    }
                    var equipmentDefendAddView = Ext.create('Equipment.view.EquipmentDefendAddView');
                    var form = equipmentDefendAddView.down('form');
                    form.load({
                        url:'/equipment/getEquipmentDefendById',
                        method:'POST',
                        params:{
                            id:select[0].get('id')
                        },
                        success:function (form,action) {
                            var respText = Ext.decode(action.response.responseText);
                            if(!respText.success){
                                XD.msg('获取表单信息失败');
                            }
                            equipmentDefendAddView.setTitle('查看维护记录');
                            equipmentDefendAddView.down('[itemId=defendSubmit]').hide();
                            equipmentDefendAddView.show();
                        },
                        failure:function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },
            'equipmentDefendGridView button[itemId=deleteDefend]':{  //删除维护记录
                click:function (view) {
                    var equipmentDefendGridView = view.findParentByType('equipmentDefendGridView');
                    var select = equipmentDefendGridView.getSelectionModel().getSelection();
                    if(select.length<1){
                        XD.msg('请至少选择一条数据');
                        return;
                    }
                    var ids = [];
                    for(var i=0;i<select.length;i++){
                        ids.push(select[i].get('id'));
                    }
                    XD.confirm('是否删除这'+select.length+'条数据',function () {
                        Ext.Ajax.request({
                            url:'/equipment/deleteEquipmentDefendByid',
                            method:'POST',
                            params:{
                                ids:ids
                            },
                            success:function (rep) {
                                var respText = Ext.decode(rep.responseText);
                                if(!respText.success){
                                    XD.msg('删除失败');
                                }else{
                                    XD.msg('删除成功');
                                    equipmentDefendGridView.getStore().reload();
                                }
                            },
                            failure:function () {
                                XD.msg('操作失败');
                            }
                        });
                    });
                }
            },
            'equipmentDefendGridView button[itemId=back]':{  //返回
                click:function (view) {
                    var equipmentDefendGridView = view.findParentByType('equipmentDefendGridView');
                    var equipmentManageView = equipmentDefendGridView.findParentByType('equipmentManageView');
                    var equipmentGridView = equipmentManageView.down('equipmentGridView');
                    equipmentManageView.setActiveItem(equipmentGridView);
                }
            },
            'equipmentGridView [itemId=excelModel]': {//导出设备模板
                click: function (btn) {
                    var columnNames = ["设备名称",'设备类型', "品牌", "型号", "规格", "单价",  "数量", "备案时间","到货验收时间","所属部门","ip地址"];
                    var reqUrl = "/export/exportColumnNames?columnNames=" + columnNames;
                    window.location.href = encodeURI(reqUrl);
                }
            },
            'equipmentGridView [itemId=import]': {
                click: function (view) {
                    new Ext.create('Equipment.view.EquipmentImportView',{
                        organView:view.findParentByType('equipmentGridView')
                    }).show();
                }
            },
            'equipmentImportView button[itemId=import]': {
                click: function (btn) {
                    var equipmentImportView = btn.findParentByType('equipmentImportView');
                    var form = equipmentImportView.down('form');
                    if (!form.isValid()) {
                        XD.msg('请选择文件再导入！');
                        return;
                    }
                    form.submit({
                        waitTitle: '提示',
                        waitMsg: '正在处理请稍后...',
                        url: '/equipment/importEquipment',
                        method: 'POST',
                        timeout:10000000,
                        success: function (form, action) {
                            var respText = Ext.decode(action.response.responseText);
                            equipmentImportView.organView.getStore().reload();
                            equipmentImportView.close();
                            XD.msg(respText.msg);
                        },
                        failure: function () {
                            XD.msg('操作中断');
                        }
                    });
                }
            },
            'equipmentImportView button[itemId=cancel]': {
                click: function (btn) {
                    btn.findParentByType('equipmentImportView').close();
                }
            },
            'equipmentGridView button[itemId=excel]':{//导出excel
                click: this.chooseFieldExportExcel
            },
            'equipmentExportSetView button[itemId="addAllOrNotAll"]': {//全选
                click:function(view){
                    var itemSelector = view.findParentByType('equipmentExportSetView').down('itemselector');
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
            },'equipmentExportSetView button[itemId=save]':{//保存所选字段
                click: this.chooseSave
            },'equipmentExportSetView button[itemId="close"]': {
                click: function (view) {
                    view.findParentByType('equipmentExportSetView').close();
                }
            }
            ,'equipmentExportWin button[itemId="cancelExport"]': {//导出 关闭
                click:function (view) {
                    view.findParentByType('equipmentExportWin').close();
                }
            },
            'equipmentExportWin button[itemId=SaveExport]':{  //导出
                click: function (view) {
                    var ReservationMessageView = view.up('equipmentExportWin');
                    var fileName = ReservationMessageView.down('[itemId=userFileName]').getValue();
                    var zipPassword = ReservationMessageView.down('[itemId=zipPassword]').getValue();
                    var b = ReservationMessageView.down('[itemId=addZipKey]').checked;
                    var form = ReservationMessageView.down('[itemId=form]');
                    tempParams['fileName'] = fileName;
                    tempParams['zipPassword'] = zipPassword;
                    tempParams['userFieldCode'] = userFieldCode;
                    tempParams['userFieldName'] = userFieldName;
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
                            Ext.Msg.alert("提示", "提示：导出xml文件只支持导入1万条以内！");
                            return;
                        }
                        Ext.MessageBox.wait('正在处理请稍后...');
                        Ext.Ajax.request({
                            method: 'post',
                            url:'/export/equipmentChooseFieldExport',
                            timeout:XD.timeout,
                            scope: this,
                            async:true,
                            params: tempParams,
                            success:function(res){
                                var obj = Ext.decode(res.responseText).data;
                                if(obj.fileSizeMsg=="NO"){
                                    XD.msg('原文总大小超出限制，一次只支持导出10G内的原文！');
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
                                window.location.href="/export/downloadZipFile?fpath="+encodeURIComponent(obj.filePath)
                                Ext.MessageBox.hide();
                                XD.msg('文件生成成功，正在准备下载');
                                ReservationMessageView.close()
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
        });
    },

    //--------自选字段导出--------//
    exportFunction:function(view, state){
        var userGridView = view.findParentByType('equipmentGridView');
        var columns=userGridView.columns;
        var record = userGridView.getSelection();
        var tmp = [];
        for(var i = 0; i < record.length; i++){
            tmp.push(record[i].get('equipmentID'));
        }
        var entryids = tmp.join(',');
        tempParams = userGridView.getStore().proxy.extraParams;
        tempParams['equipmentID'] = entryids;
        //tempParams['isSelectAll'] = isSelectAll;
        tempParams['exportState'] = state;
        var gridStore=userGridView.getStore();
        tempParams['indexLength'] = gridStore.totalCount;
        if(entryids.length == 0){
            XD.msg('请至少选择一条需要导出的数据');
            return;
        }
        var selectItem = Ext.create("Equipment.view.EquipmentExportSetView");
        var stores;//保存字段
        stores={fields:["text","value"],data:[]};
        for(var i=1;i<columns.length;i++){
            stores.data.push({"text": columns[i].text,"value":columns[i].dataIndex});
        }
        selectItem.items.get(0).setStore(stores);//更新字段
        selectItem.show();
    },
    chooseFieldExportExcel: function (view) {
        this.exportFunction(view, "Excel");
    },
    chooseSave: function (view) {
        var selectView = view.findParentByType('equipmentExportSetView');
        userFieldCode = selectView.items.get(0).getValue();//选中字段值
        var items=selectView.items.items["0"].toField.store.data.items;//获取选择字段名称
        for(var i=0;i<items.length;i++){
            userFieldName[i]=items[i].data.text;
        }
        if (userFieldCode.length>0) {
            var win = Ext.create("Equipment.view.EquipmentExportWin");
            win.show();
        }else {
            XD.msg("请选择需要导出的字段")
        }
    },
    showAddView:function(btn,operate){
        var view = btn.findParentByType('equipmentGridView');
        window.cardobj = {grid: view};
        var addView = Ext.create('Equipment.view.EquipmentAddView',{
            title:operate,
            operate: 'add', equipmentGridView:view
        });
        var container=addView.down('[itemId=container]');
        container.eleids=[];
        container.fileName=[];
        window.wpostedview = addView;
        window.wpostedview.postedUserData = undefined;
        window.wpostedview.postedUsergroupData = undefined;
        var form = addView.down('form');
        if (operate != '新增') {
            var select = view.getSelectionModel().getSelection();

            var equipmentID = form.getForm().findField("equipmentID");
            equipmentID.setValue(select[0].data['equipmentID']);

            var name = form.getForm().findField("name");
            name.setValue(select[0].data['name']);

            var type = form.getForm().findField("type");
            type.setValue(select[0].data['type']);

            var brand = form.getForm().findField("brand");
            brand.setValue(select[0].data['brand']);

            var model = form.getForm().findField("model");
            model.setValue(select[0].data['model']);

            var specifications = form.getForm().findField("specifications");
            specifications.setValue(select[0].data['specifications']);

            var price = form.getForm().findField("price");
            price.setValue(select[0].data['price']);

            var amount = form.getForm().findField("amount");
            amount.setValue(select[0].data['amount']);

            var purchasetime = form.getForm().findField("purchasetime");
            purchasetime.setValue(select[0].data['purchasetime']);

            var remarks = form.getForm().findField("remarks");
            remarks.setValue(select[0].data['remarks']);

            var acceptancetime = form.getForm().findField("acceptancetime");
            acceptancetime.setValue(select[0].data['acceptancetime']);

            var organname = form.getForm().findField("organname");
            organname.setValue(select[0].data['organname']);

            var ipaddress = form.getForm().findField("ipaddress");
            ipaddress.setValue(select[0].data['ipaddress']);

            if (operate == '修改') {
                setTimeout(function () {
                    Ext.Ajax.request({
                        method: 'POST',
                        url: '/equipment/electronicsFile/'+select[0].get('equipmentID') + '/',
                        success: function (response, opts) {
                            var data = Ext.decode(response.responseText);
                            var lable = [];
                            for(var i=0;i<data.length;i++){
                                container.eleids.push(data[i].eleid);
                                container.fileName.push(data[i].filename);
                                lable.push({xtype: 'label',text: data[i].filename
                                    ,eleid:data[i].eleid});
                            }
                            container.add(lable);
                        }
                    });

                }, 100);
            }

            if (operate == '查看') {
                name.setReadOnly(true);
                type.setReadOnly(true);
                brand.setReadOnly(true);
                model.setReadOnly(true);
                specifications.setReadOnly(true);
                price.setReadOnly(true);
                amount.setReadOnly(true);
                purchasetime.setReadOnly(true);
                remarks.setReadOnly(true);
                acceptancetime.setReadOnly(true);
                organname.setReadOnly(true);
                ipaddress.setReadOnly(true);
                form.down('[itemId=upload]').hide();
                form.down('displayfield').hide();
                addView.down('[itemId=submit]').hide();

                setTimeout(function () {
                    Ext.Ajax.request({
                        method: 'POST',
                        url: '/equipment/electronicsFile/'+select[0].get('equipmentID')+'/',
                        success: function (response, opts) {
                            var data = Ext.decode(response.responseText);
                            if(data.length>0){
                                var lable = [];
                                lable.push({xtype: 'label',text: '相关附件：(双击文件可以下载)',margin: '0 0 5 20'});
                                for(var i=0;i<data.length;i++){
                                    lable.push({
                                        xtype: 'label',text: data[i].filename,eleid:data[i].eleid,margin: '0 0 5 35',listeners: {
                                            render: function (view) {//渲染后添加双击事件
                                                view.addListener("dblclick", function () {
                                                    //解决IE浏览器中文乱码
                                                    var url = encodeURI("/inform/openFile?eleid=" + view.eleid +'&fileName=' +view.text);
                                                    window.open(url);
                                                }, null, {element: 'el'});
                                            },
                                            scope: this
                                        }
                                    });
                                }
                            }
                            addView.add(lable);
                        }
                    });
                }, 100);
            }
        }

        addView.show();
    },

    submitEquipment:function (btn) {
        var view = btn.findParentByType('equipmentAddView');
        var form = view.down('form');
        var container=view .down('[itemId=container]');
        var values = form.getValues();
        if (!form.isValid()){
            XD.msg("有必填项未填！");
            return ;
        }
        Ext.Ajax.request({
            url:'/equipment/addEquipment',
            method:'POST',
            params:{
                equipmentID:values['equipmentID'],
                acceptancetime: values['acceptancetime'],
                amount: values['amount'],
                brand: values['brand'],
                model: values['model'],
                name: values['name'],
                price: values['price'],
                purchasetime: values['purchasetime'],
                remarks: values['remarks'],
                specifications: values['specifications'],
                type: values['type'],
                ipAddress: values['ipaddress'],
                organname: values['organname'],
                eleids:container.eleids
            },
            success:function (resp) {
                var respText = Ext.decode(resp.responseText);
                XD.msg(respText.msg);
                if ("操作成功！" == respText.msg){
                    var equipmentGridView = view.equipmentGridView;
                    equipmentGridView.getStore().reload();
                    view.close();
                }else {
                    return;
                }
            },
            failure:function () {
                XD.msg('操作失败！');
                return ;
            }
        });
    }
});