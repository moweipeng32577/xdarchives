var userFieldCode = "";
var tempParams;
var userFieldName=[];
Ext.define('Reservation.controller.ReservationAdminsController', {
    extend: 'Ext.app.Controller',

    views: ['ReservationAdminsView','ReservationFormItemView','ReservationCdView',
            'RestitutionReplyFormView','RestitutionCancelFormView','ReservationView',
            'ExhibitionFormItemView','PlaceOrderManageGridView','PlaceOrderManageView',
        'PlaceOrderLookView','PlaceOrderLookGridView','PlaceOrderLookFromView',
        'PlaceOrderFormView','PlaceOrderCancelFormView','MyOrderGridView','ShowroomLookView',
        'LookEvidenceTextView'],//加载view
    stores: ['ReservationAdminsStore','ShowroomGridStore','PlaceOrderManageGridStore',
        'PlaceOrderGridStore','ApproveManStore','PlaceOrderNodeStore','PlaceOrderLookGridStore',
        'MyOrderGridStore'],//加载store
    models: ['ReservationAdminsModel','ShowroomGridModel','PlaceOrderManageGridModel',
        'PlaceOrderGridModel','PlaceOrderLookGridModel','MyOrderGridModel'],//加载model
    init: function () {
        var reserGrid;
        this.control({
            'reservationView [itemId=treepanelId]':{
                render: function (view) {
                    view.getRootNode().on('expand', function (node) {
                        if (node.getOwnerTree().getSelectionModel().selected.length == 0) {
                            var gridview = view.view.findParentByType('reservationView').down('reservationAdminsView');
                            if(view.getRootNode().childNodes.length==0){
                                XD.msg('当前用户没有该权限，请联系管理员');
                            }else {
                                gridview.lymode = view.getRootNode().childNodes[0].data.text;
                            }
                            if(yytype){
                                gridview.lymode = yytype;
                            }
                            view.getSelectionModel().select(view.getRootNode().childNodes[0]);
                            //gridview.down('toolbar').query('tbseparator')[0].hide();
                            if(taskid){
                                var gridview = view.view.findParentByType('reservationView').down('reservationAdminsView');
                                gridview.initGrid({ taskid: taskid });
                            }
                        }
                    })
                },
                select:function (view,record) {
                    var gridcard = view.view.findParentByType('reservationView').down('[itemId=gridcard]');
                    var gridview = view.view.findParentByType('reservationView').down('reservationAdminsView');
                    var placeOrderManageView = view.view.findParentByType('reservationView').down('placeOrderManageView');
                    var myOrderGridView = view.view.findParentByType('reservationView').down('myOrderGridView');
                    if(record.get('text')=='场地预约'){
                        gridcard.setActiveItem(placeOrderManageView);
                        var placeOrderManageGridView = placeOrderManageView.down('placeOrderManageGridView');
                        placeOrderManageGridView.initGrid();
                        var southgrid = gridcard.down('[itemId=southgrid]');
                        southgrid.initGrid();
                        if(iflag!='1') {
                            this.getFunction("k72", placeOrderManageView);
                        }
                        Ext.Ajax.request({//根据审批id判断是否可以催办
                            url: '/placeOrder/findByWorkId',
                            method: 'GET',
                            success: function (resp) {
                                var respDate = Ext.decode(resp.responseText).data;
                                if(respDate.urgingstate=="1"){
                                    placeOrderManageView.down('[itemId=urging]').show();
                                    placeOrderManageView.down('[itemId=message]').show();
                                }
                            }
                        });
                    }else if(record.get('text')=='我的预约'){
                        if(iflag!='1') {
                            this.getFunction("k73", myOrderGridView);
                        }
                        gridcard.setActiveItem(myOrderGridView);
                        myOrderGridView.initGrid();
                    }else{
                        gridcard.setActiveItem(gridview);
                        gridview.lymode = record.get('text');
                        if(iflag!='1'){
                            this.judgeDisplay(gridview,"cdReservate","hide");
                            this.judgeDisplay(gridview,"exhibition","hide");
                            this.judgeDisplay(gridview,"hfReservate","hide");
                            this.judgeDisplay(gridview,"qxReservate","hide");
                            this.judgeDisplay(gridview,"lookReservate","hide");
                            this.judgeDisplay(gridview,"printId","hide");
                            this.judgeDisplay(gridview,"excel","hide");
                            if(gridview.lymode=="查档预约"){
                                this.getFunction("k70",gridview);
                            }else {
                                this.getFunction("k71",gridview);
                            }
                        }else{
                            this.judgeDisplay(gridview,"cdReservate","show");
                            this.judgeDisplay(gridview,"qxReservate","show");
                            this.judgeDisplay(gridview,"lookReservate","show");
                            this.judgeDisplay(gridview,"printId","show");
                            this.judgeDisplay(gridview,"excel","show");
                        }
                        var type = iflag=='1'? "Ly":"Gl";
                        gridview.initGrid({ lymode: record.get('text'),type:type});
                    }
                }
            },
            'reservationAdminsView button[itemId=cdReservate]':{//查档预约
                click:function(btn){
                    var win = Ext.create('Ext.window.Window', {
                        height: '100%',
                        width: '100%',
                        draggable: false,//禁止拖动
                        resizable: false,//禁止缩放
                        modal: true,
                        closeToolText:'关闭',
                        title: '查档预约',
                        closeAction: 'hide',
                        layout: 'fit',
                        closable:false,
                        items: [{xtype: 'reservationFormItemView'}]
                    });
                    window.wmedia = [];
                    reserGrid = btn.findParentByType('reservationAdminsView');
                    var select = reserGrid.getSelectionModel();
                    if(select.getSelection().length==1){
                        win.down('reservationFormItemView').loadRecord(select.getSelection()[0]);
                    }
                    win.down('reservationFormItemView').docid = '';
                    win.down('[itemId=djtimeId]').setValue(new Date().format('yyyy-MM-dd hh:mm:ss'));
                    win.down('[itemId=lymodeId]').setValue(reserGrid.lymode);
                    win.show();
                }
            },
            'reservationAdminsView button[itemId=exhibition]':{//展厅参观
                click:function(btn){
                    var win = Ext.create('Ext.window.Window', {
                        height: '100%',
                        width: '100%',
                        draggable: false,//禁止拖动
                        resizable: false,//禁止缩放
                        modal: true,
                        closeToolText:'关闭',
                        title: '展厅参观预约',
                        closeAction: 'hide',
                        layout: 'fit',
                        closable:false,
                        items: [{xtype: 'exhibitionFormItemView'}]
                    });
                    reserGrid = btn.findParentByType('reservationAdminsView');
                    var select = reserGrid.getSelectionModel();
                    if(select.getSelection().length>1){
                        XD.msg('只能选中一条记录');
                        return;
                    }
                    if(select.getSelection().length==1){
                        // win.down('reservationFormItemView').loadRecord(select.getSelection()[0]);
                    }
                    win.down('[itemId=djtimeId]').setValue(new Date().format('yyyy-MM-dd hh:mm:ss'));
                    win.down('[itemId=lymodeId]').setValue(reserGrid.lymode);
                    win.down('[itemId=yystateId]').setValue('未回复');
                    var showroomGrid=win.down('[itemId=showroomGrid]');
                    showroomGrid.getStore().load();
                    win.show();
                }
            },
            'exhibitionFormItemView button[itemId=formClose]': {//展厅参观-关闭
                click:function(btn){
                    btn.findParentByType('window').close();
                }
            },
            'exhibitionFormItemView [itemId=lookShowroomid]': {//展厅参观-查看展厅
                click:function(btn){
                    var exhibitionFormItemView = btn.findParentByType('exhibitionFormItemView');
                    var showroomGrid = exhibitionFormItemView.down('[itemId=showroomGrid]');
                    var record = showroomGrid.getSelectionModel().getSelection();
                    if (record.length !== 1) {
                        XD.msg('请选择一个想要参观的展厅');
                        return;
                    }
                    var win = Ext.create('Reservation.view.ShowroomLookView',{});
                    win.show();
                    setTimeout(function () {
                        Ext.Ajax.request({
                            method: 'GET',
                            url: '/showroom/showrooms/'+record[0].get('showroomid'),
                            scope: this,
                            success: function (response, opts) {
                                var data = Ext.decode(response.responseText).data;
                                win.down('[itemId=title]').setText(data['title']);
                                document.getElementById('editFrame').contentWindow.setHtml(data['content']);
                                document.getElementById('editFrame').contentWindow.hideButton();
                            }
                        });
                        Ext.Ajax.request({
                            method: 'POST',
                            url: '/showroom/electronicsFile/'+record[0].get('showroomid') + '/',
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
                                                        window.open("/inform/openFile?eleid=" + view.eleid +'&fileName=' +view.text);
                                                    }, null, {element: 'el'});
                                                },
                                                scope: this
                                            }
                                        });
                                    }
                                }
                                win.add(lable);
                            }
                        });
                    }, 100);
                }
            },
            'exhibitionFormItemView button[itemId=formSubmit]': {//展厅参观-提交
                click:function(btn){
                    var exhibitionFormItemView = btn.findParentByType('exhibitionFormItemView');
                    var showroomGrid = exhibitionFormItemView.down('[itemId=showroomGrid]');
                    var form = exhibitionFormItemView.down('[itemId=exhibitionFormId]');
                    var borrowmantime = form.getComponent('borrowmantimeId').getValue();
                    if (borrowmantime == '' || borrowmantime == null||String(borrowmantime).indexOf(".")>-1||isNaN(borrowmantime)||parseInt(borrowmantime)<1 ) {XD.msg('来馆人数不合法');return;}
                    var yyPersons=parseInt(borrowmantime);
                    if(!form.isValid()) {
                        XD.msg('有必填项未填写，请处理后再提交');
                        return;
                    }
                    var yytime = form.getComponent('yytimeId').lastValue;
                    if(!yytime){
                        XD.msg('请选择预约时间');
                        return;
                    }
                    //获取当前时间
                    var date = new Date();
                    var year = date.getFullYear();
                    var month = date.getMonth() + 1;
                    var day = date.getDate();
                    if (month < 10) {
                        month = "0" + month;
                    }
                    if (day < 10) {
                        day = "0" + day;
                    }
                    var nowDate = year + "-" + month + "-" + day;

                    //选中的日期
                    var chooseDate=yytime.substring(0,10);
                    if(chooseDate<nowDate){//不能选择当前时间之前的日期
                        XD.msg('不能选择过去的时间预约');
                        return;
                    }

                    var record = showroomGrid.getSelectionModel().getSelection();
                    if (record.length !== 1) {
                        XD.msg('请选择一个想要参观的展厅');
                        return;
                    }
                    if(record[0].get('flag')==1){
                        XD.msg('展厅参观人数已满，请选择一个未预约的展厅');
                        return;
                    }else if(record[0].get('flag')==2){
                        XD.msg('展厅维护中，请选择一个未预约的展厅');
                        return;
                    }else if(record[0].get('audiences')<yyPersons || record[0].get('audiences')<(yyPersons+parseInt(record[0].get('yyAudiences')))){//总预约人数要小于等于参观人数限制
                        XD.msg('预约人数超过限制，请修改人数或者重新选择预约时间');
                        return;
                    }
                    form.submit({
                        url: '/jyAdmins/reservationAddForm',
                        method: 'POST',
                        params: {
                            showroomid: record[0].get('showroomid')
                        },
                        success: function (form, action) {
                            var respText = Ext.decode(action.response.responseText);
                            XD.msg(respText.msg);
                            if(respText.msg.indexOf('成功')>0){//预约成功时更新表格，预约超出时不做处理
                                reserGrid.getStore().reload();
                                btn.findParentByType('window').close();
                            }
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },
            'reservationFormItemView button[itemId=formClose]': {//查档预约-关闭
                click:function(btn){
                    btn.findParentByType('window').close();
                }
            },
            'reservationFormItemView button[itemId=formSubmit]': {//查档预约-提交
                click:function(btn){
                    var form = btn.findParentByType('reservationFormItemView');
                    var borrowmantime = form.getComponent('borrowmantimeId').getValue();
                    if (borrowmantime == '' || borrowmantime == null||String(borrowmantime).indexOf(".")>-1||isNaN(borrowmantime)||parseInt(borrowmantime)<1 ) {XD.msg('来馆人数不合法');return;}
                    if(!form.isValid()) {
                        XD.msg('有必填项未填写，请处理后再提交');
                        return;
                    }
                    var yytime = form.getComponent('yytimeId').lastValue;
                    if(!yytime){
                        XD.msg('请选择预约时间');
                        return;
                    }
                    //获取当前时间
                    var date = new Date();
                    var year = date.getFullYear();
                    var month = date.getMonth() + 1;
                    var day = date.getDate();
                    if (month < 10) {
                        month = "0" + month;
                    }
                    if (day < 10) {
                        day = "0" + day;
                    }
                    var nowDate = year + "-" + month + "-" + day;

                    //选中的日期
                    var chooseDate=yytime.substring(0,10);
                    if(chooseDate<nowDate){//不能选择当前时间之前的日期
                        XD.msg('不能选择过去的时间预约');
                        return;
                    }
                    form.submit({
                        url: '/jyAdmins/reservationAddForm',
                        method: 'POST',
                        params: {
                            eleids:window.wmedia
                        },
                        success: function (form, action) {
                            var respText = Ext.decode(action.response.responseText);
                            XD.msg(respText.msg);
                            reserGrid.getStore().reload();
                            btn.findParentByType('window').close();
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },
            'reservationAdminsView button[itemId=hfReservate]':{//预约回复
                click:function(btn){
                    var win = Ext.create('Ext.window.Window', {
                        height: '70%',
                        width: '65%',
                        draggable: false,//禁止拖动
                        resizable: false,//禁止缩放
                        modal: true,
                        closeToolText:'关闭',
                        title: '预约回复',
                        closeAction: 'hide',
                        layout: 'fit',
                        closable:false,
                        items: [{xtype: 'restitutionReplyFormView'}]
                    });
                    reserGrid = btn.findParentByType('reservationAdminsView');
                    var select = reserGrid.getSelectionModel();
                    if(select.getSelection().length!=1){
                        XD.msg('请选择一条记录');
                        return;
                    }
                    var form = win.down('restitutionReplyFormView');
                    var replycontent = select.getSelection()[0].get('replycontent');
                    form.load({
                        url: '/jyAdmins/getReplyMsgForm',
                        params:{replycontent:replycontent},
                        success: function (form, action) {
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
                    win.show();
                }
            },'restitutionReplyFormView button[itemId=replyformClose]': {//预约回复-返回
                click:function(btn){
                    btn.findParentByType('window').close();
                }
            }, 'restitutionReplyFormView button[itemId=replyformSubmit]': {//预约回复-提交
                click:function(btn){
                    var form = btn.findParentByType('restitutionReplyFormView');
                    var yystate = reserGrid.getSelectionModel().getSelection()[0].get('yystate');
                    if(yystate=='已回复'){
                        XD.msg('已回复，请刷新！');
                        return;
                    }
                    var id = reserGrid.getSelectionModel().getSelection()[0].data.docid;
                    if(!form.isValid()) {
                        XD.msg('有必填项未填写，请处理后再提交');
                        return;
                    }
                    form.submit({
                        url: '/jyAdmins/reservationReplyAddForm',
                        method: 'POST',
                        params:{docid:id,taskid:taskid},
                        success: function (form, action) {
                            var respText = Ext.decode(action.response.responseText);
                            XD.msg(respText.msg);
                            reserGrid.getStore().reload();
                            btn.findParentByType('window').close();
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },
            'restitutionCancelFormView button[itemId=CancelformClose]': {//查看预约详情-返回
                click:function(btn){
                    btn.findParentByType('window').close();
                }
            },
            /*'reservationAdminsView button[itemId=qxReservate]':{//取消预约
                click:function(btn){
                    var win = Ext.create('Ext.window.Window', {
                        height: '70%',
                        width: '65%',
                        draggable: false,//禁止拖动
                        resizable: false,//禁止缩放
                        modal: true,
                        closeToolText:'关闭',
                        title: '取消预约',
                        closeAction: 'hide',
                        layout: 'fit',
                        closable:false,
                        items: [{xtype: 'restitutionCancelFormView'}]
                    });
                    reserGrid = btn.findParentByType('reservationAdminsView');
                    var select = reserGrid.getSelectionModel();
                    if(select.getSelection().length!=1){
                        XD.msg('请选择一条记录');
                        return;
                    }
                    var form = win.down('restitutionCancelFormView');
                    form.load({
                        url: '/jyAdmins/getCancelMsgForm',
                        success: function (form, action) {
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
                    win.show();
                }
            },'restitutionCancelFormView button[itemId=CancelformClose]': {//取消预约-返回
                click:function(btn){
                    btn.findParentByType('window').close();
                }
            }, 'restitutionCancelFormView button[itemId=CancelformSubmit]': {//取消预约-提交
                click:function(btn){
                    var form = btn.findParentByType('restitutionCancelFormView');
                    if (iflag==1){
                        var yytime = reserGrid.getSelectionModel().getSelection()[0].get('yytime');
                        var currentime = Ext.util.Format.date(new Date(),'Y-m-d H:i:s');
                        if(yytime<currentime){//预约时间小于当天则不可取消
                            XD.msg('取消失败，请在工作时间内电话联系工作人员去取消预约');
                            return;
                        }
                    }
                    if(reserGrid.getSelectionModel().getSelection()[0].get('canceltime')!=null){
                        XD.msg('取消失败，不能重复取消');
                        btn.findParentByType('window').close();
                        return;
                    }
                    var id = reserGrid.getSelectionModel().getSelection()[0].data.docid;
                    form.submit({
                        url: '/jyAdmins/reservationCancelAddForm',
                        method: 'POST',
                        params:{docid:id},
                        success: function (form, action) {
                            var respText = Ext.decode(action.response.responseText);
                            XD.msg(respText.msg);
                            reserGrid.getStore().reload();
                            btn.findParentByType('window').close();
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },*/
            'reservationAdminsView button[itemId=qxReservate]': {//直接取消预约 而不弹出提示框
                click:function(btn){
                    var form = btn.findParentByType('reservationAdminsView');
                    if (iflag==1){
                        var yytime = form.getSelectionModel().getSelection()[0].get('yytime');
                        var currentime = Ext.util.Format.date(new Date(),'Y-m-d H:i:s');
                        if(yytime<currentime){//预约时间小于当天则不可取消
                            XD.msg('取消失败，请在工作时间内电话联系工作人员去取消预约');
                            return;
                        }
                    }
                    if(form.getSelectionModel().getSelection()[0].get('yystate') == '已取消'){
                        XD.msg('不能重复取消');
                        return;
                    }
                    var id = form.getSelectionModel().getSelection()[0].data.docid;
                    /*Ext.Ajax.request({
                        url: '/jyAdmins/reservationCancelAddForm',
                        method: 'POST',
                        params:{docid:id},
                        success: function (response) {
                            var respText = Ext.decode(response.responseText);
                            XD.msg(respText.msg);
                            btn.findParentByType('window').close();
                            form.getStore().reload();
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });*/

                    XD.confirm('是否确定取消',function(){
                        Ext.Ajax.request({
                            url: '/jyAdmins/reservationCancelAddForm',
                            params: {docid:id},
                            method: 'POST',
                            //sync: true,
                            success: function (response) {
                                var respText = Ext.decode(response.responseText);
                                XD.msg(respText.msg);
                                form.getStore().reload();
                                btn.findParentByType('window').close();
                            },
                            failure: function() {
                                XD.msg('操作失败');
                            }
                        });
                    },this);

                }
            },'reservationAdminsView button[itemId=lookReservate]': {//查看预约详情
                click:function (btn) {
                    var win = Ext.create('Reservation.view.ReservationCdView');
                    reserGrid = btn.findParentByType('reservationAdminsView');
                    var select = reserGrid.getSelectionModel();
                    if(select.getSelection().length!=1){
                        XD.msg('请选择一条记录');
                        return;
                    }
                    win.down('reservationFormItemView').loadRecord(select.getSelection()[0]);
                    win.down('restitutionReplyFormView').loadRecord(select.getSelection()[0]);
                    win.down('restitutionCancelFormView').loadRecord(select.getSelection()[0]);
                    win.down('restitutionReplyFormView').down('[itemId=yystateId]').hide();
                    win.down('restitutionReplyFormView').down('[itemId=yystateDisId]').hide();
                    win.down('restitutionCancelFormView').down('[itemId=yystateId]').hide();
                    win.down('reservationFormItemView').docid = select.getSelection()[0].get('docid');
                    var count = this.getEvidencetextCount(select.getSelection()[0].get('docid'));
                    win.down('reservationFormItemView').down('[itemId=mediacount]').setText('共'+count+'份');
                    win.down('reservationFormItemView').down('[itemId=electronId]').setText('查看');
                    var buttons = win.query('button');
                    for(var i=0;i<buttons.length-1;i++){
                        if(buttons[i].itemId=='electronId'){
                            continue;
                        }
                        buttons[i].hide();
                    }
                    // 设置只读
                    win.down('reservationFormItemView').getForm().getFields().each(function (field) {
                        field.setReadOnly(true);
                    })
                    win.show();
                }
            },
            'placeOrderManageView button[itemId=urging]': {//催办
                click: function (view) {
                    var placeOrderManageView = view.findParentByType('placeOrderManageView');
                    var southgrid = placeOrderManageView.down('[itemId=southgrid]');
                    var details = southgrid.getSelectionModel().getSelection();
                    if(details.length!=1){
                        XD.msg('请选择一条数据!');
                        return;
                    }
                    if(details[0].get("state")!="提交预约"){
                        XD.msg('请选择正确的数据催办!');
                        return;
                    }
                    Ext.MessageBox.wait('正在处理请稍后...');
                    Ext.Ajax.request({
                        params: {ordercode: details[0].get("ordercode"),sendMsg:placeOrderManageView.down("[itemId=message]").checked},
                        url: '/placeOrder/manualUrging',
                        method: 'POST',
                        sync: true,
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
                }
            },
            'placeOrderManageView [itemId=southgrid] [itemId=add]':{  //新增场地预约
                click:function (view) {
                    var placeOrderManageView = view.findParentByType('placeOrderManageView');
                    var placeOrderManageGridView = placeOrderManageView.down('placeOrderManageGridView');
                    var select = placeOrderManageGridView.getSelectionModel().getSelection();
                    var southgrid = placeOrderManageView.down('[itemId=southgrid]');
                    if(select.length != 1){
                        XD.msg('只能选择一个场地');
                        return;
                    }
                    if(select[0].get('state')!='使用中'){
                        XD.msg('场地不是处于使用中状态');
                        return;
                    }
                    var title = select[0].get('floor')+'场地预约';
                    var placeid = select[0].get('id');
                    var placeOrderFormView = Ext.create('Reservation.view.PlaceOrderFormView');
                    placeOrderFormView.title = title;
                    placeOrderFormView.down('[itemId=auditlinkId]').getStore().reload();
                    var form = placeOrderFormView.down('form');
                    form.load({
                        url:'/placeOrder/placeOrderFormLoad',
                        method:'GET',
                        success:function () {
                        },
                        failure:function () {
                            XD.msg('获取表单信息失败');
                        }
                    });
                    placeOrderFormView.placeid = placeid;
                    placeOrderFormView.southgrid = southgrid;
                    placeOrderFormView.show();
                }
            },
            'placeOrderFormView button[itemId=placeOrderSubmit]':{   //提交预约
                click:function (view) {
                    var placeOrderFormView = view.findParentByType('placeOrderFormView');
                    var form = placeOrderFormView.down('form');
                    var starttime = placeOrderFormView.down('[name=starttime]').getValue();
                    var endtime = placeOrderFormView.down('[name=endtime]').getValue();
                    var spnodeid = form.down('[itemId=auditlinkId]').getValue();
                    var spmanid = form.down('[itemId=spmanId]').getValue();
                    if(starttime>endtime){
                        XD.msg('开始时间不能大于结束时间');
                        return;
                    }
                    if(!form.isValid()){
                        XD.msg('存在必填项没有填写');
                        return;
                    }
                    Ext.MessageBox.wait('正在提交请稍后...','提示');
                    form.submit({
                        url:'/placeOrder/placeOrderFormSubmit',
                        method:'POST',
                        params:{
                            spnodeid:spnodeid,
                            spmanid:spmanid,
                            placeid:placeOrderFormView.placeid
                        },
                        success:function (form,action) {
                            Ext.MessageBox.hide();
                            var respText = Ext.decode(action.response.responseText);
                            if(respText.data){
                                var carorder = respText.data;
                                var text;
                                if(carorder.length>=2){
                                    text =  '提交失败！'+carorder[0].starttime+' 至 '+carorder[0].endtime+' 已被 '+carorder[0].placeuser+' 预约、'+
                                        carorder[1].starttime+' 至 '+carorder[1].endtime+' 已被 '+carorder[0].placeuser+' 预约，请另选使用场地时间段！';
                                }else{
                                    text =  '提交失败！'+carorder[0].starttime+' 至 '+carorder[0].endtime+' 已被 '+carorder[0].placeuser+' 预约，请另选使用场地时间段！';
                                }
                                XD.msg(text);
                            }else{
                                XD.msg('预约成功');
                                placeOrderFormView.southgrid.getStore().reload();
                                placeOrderFormView.close();
                            }
                        },
                        failure:function () {
                            Ext.MessageBox.hide();
                            XD.msg('操作失败')
                        }
                    });
                }
            },

            'placeOrderFormView button[itemId=placeOrderClose]':{
                click:function (view) {
                    view.findParentByType('placeOrderFormView').close();
                }
            },

            'placeOrderManageView [itemId=southgrid] [itemId=cancel]':{   //取消预约
                click:function (view) {
                    var placeOrderManageView = view.findParentByType('placeOrderManageView');
                    var southgrid = placeOrderManageView.down('[itemId=southgrid]');
                    var select = southgrid.getSelectionModel().getSelection();
                    if(select.length != 1){
                        XD.msg('只能选择一条数据');
                        return;
                    }
                    var str = select[0].get['state'];
                    var state = str.contain('预约成功');//是否包含‘预约成功’
                    if(select[0].get('state')!='预约成功'){
                        XD.msg('只能取消预约成功的预约记录！其它预约状态的可直接删除预约！');
                        return;
                    }
                    var placeOrderCancelFormView = Ext.create('Reservation.view.PlaceOrderCancelFormView');
                    var form = placeOrderCancelFormView.down('form');
                    form.load({
                        url:'/placeOrder/placeOrderCancelFormLoad',
                        method:'GET',
                        success:function () {
                        },
                        failure:function () {
                            XD.msg('获取表单信息失败');
                        }
                    });
                    placeOrderCancelFormView.orderid = select[0].get('id');
                    placeOrderCancelFormView.southgrid = southgrid;
                    placeOrderCancelFormView.show();
                }
            },

            'placeOrderCancelFormView button[itemId=cancelSubmit]':{ //取消预约 提交
                click:function (view) {
                    var placeOrderCancelFormView = view.findParentByType('placeOrderCancelFormView');
                    var form = placeOrderCancelFormView.down('form');
                    var canceluser = placeOrderCancelFormView.down('[name=canceluser]').getValue();
                    var canceltime = placeOrderCancelFormView.down('[name=canceltime]').getValue();
                    var cancelreason = placeOrderCancelFormView.down('[name=cancelreason]').getValue();
                    if(!form.isValid()){
                        XD.msg('存在必填项没有填写');
                        return;
                    }
                    Ext.MessageBox.wait('正在提交请稍后...','提示');
                    form.submit({
                        url:'/placeOrder/placeOrderCancelFormSubmit',
                        method:'POST',
                        params:{
                            orderid:placeOrderCancelFormView.orderid,
                            canceluser:canceluser,
                            canceltime:canceltime,
                            cancelreason:cancelreason
                        },
                        success:function (form,action) {
                            Ext.MessageBox.hide();
                            var respText = Ext.decode(action.response.responseText);
                            if(respText.data){
                                XD.msg('取消预约成功');
                                placeOrderCancelFormView.southgrid.getStore().reload();
                                placeOrderCancelFormView.close();
                            }else{
                                XD.msg('取消预约失败');
                            }
                        },
                        failure:function () {
                            Ext.MessageBox.hide();
                            XD.msg('操作失败')
                        }
                    });
                }
            },

            'placeOrderCancelFormView button[itemId=cancelClose]':{ //取消预约 关闭
                click:function (view) {
                    view.findParentByType('placeOrderCancelFormView').close();
                }
            },

            'placeOrderManageView [itemId=southgrid] [itemId=del]':{   //删除预约
                click:function (view) {
                    var placeOrderManageView = view.findParentByType('placeOrderManageView');
                    var southgrid = placeOrderManageView.down('[itemId=southgrid]');
                    var select = southgrid.getSelectionModel().getSelection();
                    if(select.length < 1){
                        XD.msg('请至少选择一条数据');
                        return;
                    }
                    var orderids = [];
                    for(var i=0;i<select.length;i++){
                        if(select[0].get('state')=='预约成功'){
                            XD.msg('不能删除预约成功的单据，请先取消预约！');
                            return;
                        }else if(select[0].get('state')=='提交预约'){
                            XD.msg('不能删除正在预约审批的单据，请先取消预约！');
                            return;
                        }
                        orderids.push(select[i].get('id'));
                    }
                    XD.confirm('是否删除这'+orderids.length+'条数据',function () {
                        Ext.Ajax.request({
                            url:'/placeOrder/placeOrderDelete',
                            method:'POST',
                            params:{
                                orderids:orderids
                            },
                            success:function (rep) {
                                var respText = Ext.decode(rep.responseText);
                                if(!respText.success){
                                    XD.msg('删除失败');
                                }else{
                                    XD.msg('删除成功');
                                    southgrid.getStore().reload();
                                }
                            },
                            failure:function () {
                                XD.msg('操作失败');
                            }
                        });
                    });
                }
            },
            'placeOrderManageView [itemId=southgrid] [itemId=look]':{   //查看预约
                click:function (view) {
                    var placeOrderManageView = view.findParentByType('placeOrderManageView');
                    var southgrid = placeOrderManageView.down('[itemId=southgrid]');
                    var select = southgrid.getSelectionModel().getSelection();
                    if(select.length != 1){
                        XD.msg('只能选择一条数据');
                        return;
                    }
                    var state = select[0].get('state');
                    var placeOrderLookView = Ext.create('Reservation.view.PlaceOrderLookView');
                    var form = placeOrderLookView.down('form');
                    if(state=='取消预约'){
                        form.down('[itemId=canceluserId]').show();
                        form.down('[itemId=cancelreasonId]').show();
                        form.down('[itemId=canceltimeId]').show();
                        form.down('[itemId=cancedisId]').show();
                    }
                    var placeOrderLookGridView = placeOrderLookView.down('placeOrderLookGridView');
                    placeOrderLookGridView.initGrid({orderid:select[0].get('id')});
                    form.load({
                        url:'/placeOrder/placeOrderLookFormLoad',
                        method:'GET',
                        params:{
                            orderid: select[0].get('id')
                        },
                        success:function () {
                        },
                        failure:function () {
                            XD.msg('获取表单信息失败');
                        }
                    });
                    placeOrderLookView.show();
                }
            },
            'placeOrderLookView button[itemId=lookOrderClose]':{  // 查看预约 关闭
                click:function (view) {
                    view.findParentByType('window').close();
                }
            },
            'myOrderGridView button[itemId=look]':{   //我的预约 查看
                click:function (view) {
                    var myOrderGridView = view.findParentByType('myOrderGridView');
                    var select = myOrderGridView.getSelectionModel().getSelection();
                    if(select.length != 1){
                        XD.msg('只能选择一条数据');
                        return;
                    }
                    var state = select[0].get('state');
                    var placeOrderLookView = Ext.create('Reservation.view.PlaceOrderLookView');
                    var form = placeOrderLookView.down('form');
                    if(state=='取消预约'){
                        form.down('[itemId=canceluserId]').show();
                        form.down('[itemId=cancelreasonId]').show();
                        form.down('[itemId=canceltimeId]').show();
                        form.down('[itemId=cancedisId]').show();
                    }
                    var placeOrderLookGridView = placeOrderLookView.down('placeOrderLookGridView');
                    placeOrderLookGridView.initGrid({orderid:select[0].get('id')});
                    form.load({
                        url:'/placeOrder/placeOrderLookFormLoad',
                        method:'GET',
                        params:{
                            orderid: select[0].get('id')
                        },
                        success:function () {
                        },
                        failure:function () {
                            XD.msg('获取表单信息失败');
                        }
                    });
                    placeOrderLookView.show();
                }
            },'myOrderGridView [itemId=printId]': {//我的预约 打印
                click: function (btn) {
                    var reportGrid = btn.findParentByType('myOrderGridView');
                    var entryId=[];
                    var record = reportGrid.getSelection();
                    if(record.length>1||record.length<=0){
                        XD.msg('请选择一条记录');
                        return;
                    }
                    for(var i = 0; i < record.length; i++){
                        entryId.push(record[i].get('id'));
                    }
                    var params = {};
                    params['id'] = entryId;
                    XD.UReportPrint("查档申请管理_我的预约", "查档申请管理_我的预约", params);
                }
            },'reservationAdminsView button[itemId=printId]': {//打印
                click: function (btn) {
                    var reportGrid = btn.findParentByType('reservationAdminsView');
                    var entryId=[];
                    var record = reportGrid.getSelection();
                    if(record.length>1||record.length<=0){
                        XD.msg('请选择一条记录');
                        return;
                    }
                    for(var i = 0; i < record.length; i++){
                        entryId.push(record[i].get('docid'));
                    }
                    var params = {};
                    params['docid'] = entryId;
                    XD.UReportPrint("查档申请管理_预约管理", "查档申请管理_预约管理", params);
                }
            },'reservationAdminsView button[itemId=excel]':{//导出excel
                click: this.chooseFieldExportExcel
            },'reservationGroupSetView button[itemId="addAllOrNotAll"]': {//全选
                click:function(view){
                    var itemSelector = view.findParentByType('reservationGroupSetView').down('itemselector');
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
            },'reservationGroupSetView button[itemId=save]':{//保存所选字段
                click: this.chooseSave
            },'reservationGroupSetView button[itemId="close"]': {
                click: function (view) {
                    view.findParentByType('reservationGroupSetView').close();
                }
            }
            ,'reservationMessage button[itemId="cancelExport"]': {//导出 关闭
                click:function (view) {
                    view.findParentByType('reservationMessage').close();
                }
            }
            ,'reservationMessage button[itemId="SaveExport"]': {//导出确认按钮
                click: function (view) {
                    var ReservationMessageView = view.up('reservationMessage');
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
                            url:'/export/reservationChooseFieldExport',
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

            'reservationFormItemView button[itemId=electronId]': {  //上传附件
                click: function (view) {
                    var eleView = Ext.create("Ext.window.Window", {
                        width: '100%',
                        height: '100%',
                        title: '添加附件',
                        modal: true,
                        header: false,
                        draggable: false,//禁止拖动
                        resizable: false,//禁止缩放
                        closeToolText: '关闭',
                        layout: 'fit',
                        items: [{xtype: 'electronicPro'}]
                    });
                    window.wform = view.findParentByType('reservationFormItemView');
                    var docid;
                    if(window.wform.docid==''){
                        docid = 'undefined';
                    }else{
                        docid = window.wform.docid;
                    }
                    eleView.down('electronicPro').initData(docid);
                    eleView.show();
                }
            }
        });

    },
    //根据节点获取相应的权限按钮
    getFunction:function(isp,view){
        Ext.Ajax.request({
            url:'/jyAdmins/getFunction',
            method:'GET',
            params:{
                isp:isp
            },
            success:function (rep) {
                var fnButton= Ext.decode(rep.responseText).data;
                for(var i=0;i<fnButton.length;i+=2){
                    if (view.down('[itemId=' + fnButton[i].itemId + ']') != null) {
                        if(iflag==1&&(fnButton[i].itemId=='hfReservate'||fnButton[i].itemId=='excel')){
                            continue;
                        }
                        view.down('[itemId=' + fnButton[i].itemId + ']').show();
                    }
                }
            },
            failure:function () {
                XD.msg('操作失败');
            }
        });
    },
    //判断按钮是否显示
    judgeDisplay:function(view,itemId,display) {
        if (view.down('[itemId=' + itemId + ']') != null) {
            if (display == "hide") {
                view.down('[itemId=' + itemId + ']').hide();
            } else {
                view.down('[itemId=' + itemId + ']').show();
            }
        }
    },
    //--------自选字段导出--------//
    exportFunction:function(view, state){
        var userGridView = view.findParentByType('reservationAdminsView');
        var columns=userGridView.columns;
        var record = userGridView.getSelection();
        var tmp = [];
        for(var i = 0; i < record.length; i++){
            tmp.push(record[i].get('docid'));
        }
        var entryids = tmp.join(',');
        tempParams = userGridView.getStore().proxy.extraParams;
        tempParams['entryids'] = entryids;
        //tempParams['isSelectAll'] = isSelectAll;
        tempParams['exportState'] = state;
        var gridStore=userGridView.getStore();
        tempParams['indexLength'] = gridStore.totalCount;
        if(entryids.length == 0){
            XD.msg('请至少选择一条需要导出的数据');
            return;
        }
        var selectItem = Ext.create("Reservation.view.ReservationGroupSetView");
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
        var selectView = view.findParentByType('reservationGroupSetView');
        userFieldCode = selectView.items.get(0).getValue();//选中字段值
        var items=selectView.items.items["0"].toField.store.data.items;//获取选择字段名称
        for(var i=0;i<items.length;i++){
            userFieldName[i]=items[i].data.text;
        }
        if (userFieldCode.length>0) {
            var win = Ext.create("Reservation.view.ReservationMessageView");
            win.show();
        }else {
            XD.msg("请选择需要导出的字段")
        }
    },

    //获取附件份数
    getEvidencetextCount:function (docid) {
        var count;
        Ext.Ajax.request({
            url: '/electronApprove/getEvidencetextCount',
            async:false,
            params:{
                borrowcode:docid
            },
            success: function (response) {
                count = Ext.decode(response.responseText).data;
            }
        });
        return count;
    }
});

function getNowFormatDate() {
    var date = new Date();
    var month = date.getMonth() + 1;
    var strDate = date.getDate();
    var hour= date.getHours();
    var minutes=date.getMinutes();
    var second = date.getSeconds();
    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
    }
    if (hour >= 0 && hour <= 9) {
        hour = "0" + hour;
    }
    if (minutes >= 0 && minutes <= 9) {
        minutes = "0" + minutes;
    }
    if (second >= 0 && second <= 9) {
        second = "0" + second;
    }
    var currentdate = date.getFullYear() + '年' + month + '月' + strDate + '日 '+hour+":"+minutes+":"+second;
    return currentdate;
}