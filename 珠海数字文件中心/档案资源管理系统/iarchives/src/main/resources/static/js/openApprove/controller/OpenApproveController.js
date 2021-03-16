/**
 * Created by tanly on 2017/12/5.
 */
Ext.define('OpenApprove.controller.OpenApproveController', {
    extend: 'Ext.app.Controller',

    views: ['OpenApproveView', 'OpenApproveGridView', 'OpenApproveFormView', 'ApproveAddView',
        'OpenApproveEntryFormView'],
    stores: ['OpenApproveGridStore', 'NextNodeStore', 'NextSpmanStore','ApproveOrganStore'],
    models: ['OpenApproveGridModel'],
    init: function (view) {
        var isAddPostil = false;//判断是否已经添加过批注
        var openApproveGridView;
        this.control({
            'openApproveFormView': {
                render: function (view) {
                    window.wform = view;
                    view.load({
                        url: '/openApprove/getOpenDoc',
                        params: {taskid: taskid},
                        success: function () {
                            window.wapprove = view.getValues()['approve'];
                            window.batchnum = view.getValues()['batchnum'];
                            window.docid = view.getValues()['id'];
                        },
                        failure: function () {
                            XD.msg('操作中断');
                        }
                    });
                    var nextNode = view.down('[itemId=nextNodeId]');
                    var nextSpman = view.down('[itemId=nextSpmanId]');
                    var spmanOrgan = view.down("[itemId=approveOrgan]");
                    nextNode.on('change', function (val) {
                        nextSpman.getStore().proxy.extraParams.nodeId = val.value;
                        spmanOrgan.getStore().proxy.extraParams.type = "approve"; //审批时获取审批单位
                        spmanOrgan.getStore().proxy.extraParams.taskid = taskid;
                        spmanOrgan.getStore().proxy.extraParams.nodeid = val.value;
                        spmanOrgan.getStore().proxy.extraParams.worktext = null;
                        spmanOrgan.getStore().proxy.extraParams.approveType = "dataOpen"; //审批类型
                        spmanOrgan.getStore().reload(); //刷新审批单位
                    });
                }
            },
            'openApproveGridView': {
                afterrender: function (view) {
                    view.initGrid({taskid: taskid});
                    if (systemLoginType == '1') {//政务网
                        view.columns[9].hide();
                        view.columns[10].hide();
                        view.columns[12].hide();
                        view.columns[13].hide();
                        view.columns[14].hide();
                        view.columns[15].hide();
                        view.columns[16].hide();
                    } else {
                        view.columns[11].hide();//局域网隐藏开放状态
                    }
                    if (type == '完成') {//当类型为'完成'时,界面只显示查看按钮
                        var buttons = view.down("toolbar").query('button');
                        var tbseparator = view.down("toolbar").query('tbseparator');
                        //隐藏设置开放权限按钮
                        hideToolbarBtnTbsByItemId('setkfqx', buttons, tbseparator);

                        var formView = view.findParentByType('openApproveView').down('openApproveFormView');
                        var store = formView.down("toolbar").items.items;
                        for (var i = 0; i < store.length; i++) {
                            //隐藏下一环节&审批人&添加批示&完成&退回
                            if (i < store.length - 1) {
                                store[i].hide();
                            }
                        }
                    }
                    Ext.Ajax.request({
                        url:'/electronApprove/approvesort',
                        params: {
                            taskid:taskid
                        },
                        scope:this,
                        success: function (response) {
                            var responseText = Ext.decode(response.responseText);
                            if(responseText.success){
                                var btn = view.findParentByType('openApproveView').down('openApproveFormView').down('[itemId=openApproveBackPre]');
                                btn.hide();
                            }
                        }
                    });
                },
                eleview: function (obj) {
                    var view = Ext.create("Ext.window.Window", {
                        width: '100%',
                        height: '100%',
                        plain: true,
                        header: false,
                        border: false,
                        closable: false,
                        frame: false,
                        draggable: false,//禁止拖动
                        resizable: false,//禁止缩放
                        modal: true,
                        closeToolText: '关闭',
                        layout: 'fit',
                        items: [{
                            xtype: 'EntryFormView'
                        }]
                    });
                    var formview = this.findFormView(view);
                    // view.setActiveItem(formview);
                    formview.items.get(0).disable();
                    var eleview = formview.down('electronic');
                    var solidview = formview.down('solid');
                    eleview.operateFlag = "look"; //电子文件查看标识符
                    solidview.operateFlag = "look";//利用文件查看标识符
                    eleview.initData(obj.entryid);
                    solidview.initData(obj.entryid);
                    var from = formview.down('dynamicform');
                    //电子文件按钮权限
                    var elebtns = eleview.down('toolbar').query('button');
                    from.getELetopBtn(elebtns, eleview.operateFlag);
                    var soildbtns = solidview.down('toolbar').query('button');
                    from.getELetopBtn(soildbtns, solidview.operateFlag);
                    //电子文件按钮权限
                    formview.setActiveTab(1);
                    view.show();
                    window.approves = view;
                    return formview;
                },
            },
            'openApproveFormView button[itemId=approveAdd]': {
                click: function () {
                    Ext.create('OpenApprove.view.ApproveAddView').show();
                }
            },
            'openApproveFormView button[itemId=openApproveFormClose]': {
                click: function () {
                    if (iflag == '1') {
                        parent.approve.close();
                    } else {
                        parent.closeObj.close(parent.layer.getFrameIndex(window.name));
                    }
                }
            },
            'openApproveGridView [itemId=printOpen]': {//开放审批界面- 打印开放审查登记表
                click: this.printDocHandler
            },
            'openApproveGridView [itemId=printNotOpen]': {//开放审批界面- 打印不开放审查登记表
                click: this.printDocHandler
            },
            'openApproveGridView [itemId=printOpenAppraisal]': {//开放审批界面- 打印档案开放鉴定汇报表
                click: this.printDocHandler
            },
            'openApproveGridView button[itemId=setkfqx]': {
                click: function (view) {
                    if (systemLoginType == '1') {
                        var grid = view.findParentByType('openApproveGridView');
                        window.wgrid = grid;
                        var select = grid.getSelectionModel();
                        if (select.getCount() < 1) {
                            XD.msg("请选择一条数据");
                            return;
                        }
                        Ext.create('OpenApprove.view.SetQxView').show();
                    } else {
                        var grid = view.findParentByType('openApproveGridView');
                        window.wgrid = grid;
                        var record = grid.getSelectionModel().getSelection();
                        var selectAll = grid.down('[itemId=selectAll]').checked;
                        var approve = Ext.create("Ext.window.Window", {
                            width: '100%',
                            height: '100%',
                            plain: true,
                            header: false,
                            border: false,
                            closable: false,
                            frame: false,
                            draggable: false,//禁止拖动
                            resizable: false,//禁止缩放
                            modal: true,
                            closeToolText: '关闭',
                            layout: 'fit',
                            items: [{
                                xtype: 'openApproveEntryFormView'
                            }]
                        });
                        var entryids = [];
                        var nodeids = [];
                        var msgids = [];
                        if (selectAll) {
                            var deRecord = grid.acrossDeSelections;
                            var deEntryIds = [];
                            for (var i = 0; i < deRecord.length; i++) {
                                deEntryIds.push(deRecord[i].get('entryid'));
                            }
                            var openmsgs = this.getSelectAllEntry(deEntryIds);
                            for (var i = 0; i < openmsgs.length; i++) {
                                entryids.push(openmsgs[i].entryid);
                                nodeids.push(openmsgs[i].nodeid);
                                msgids.push(openmsgs[i].eleid);
                            }
                        } else {
                            for (var i = 0; i < record.length; i++) {
                                entryids.push(record[i].get('entryid'));
                                nodeids.push(record[i].get('nodeid'));
                                msgids.push(record[i].get('msgid'));
                            }
                        }
                        if (entryids.length < 1) {
                            XD.msg("请至少选择一条数据");
                            return;
                        }
                        var entryid = entryids[0];
                        var form = approve.down('dynamicform');
                        form.nodeid = nodeids[0];
                        form.operate = 'look';
                        form.entryids = entryids;
                        form.nodeids = nodeids;
                        form.entryid = entryids[0];
                        form.msgids = msgids;
                        form.msgid = msgids[0];
                        this.initFormField(form, 'hide', nodeids[0]);
                        this.initFormData('look', form, entryid);
                        approve.show();
                    }
                }
            },
            'setQxView button[itemId=setQxAddSubmit]': {
                click: function (view) {

                    var select = window.wgrid.getSelectionModel();
                    var datas = select.getSelection();
                    var dataids = new Array();
                    for (var i = 0; i < datas.length; i++) {
                        dataids.push(datas[i].get('entryid'));
                    }

                    var kfqx = view.findParentByType('setQxView').down('[itemId=setQxId]').value;
                    Ext.Ajax.request({
                        params: {
                            kfqx: kfqx,
                            dataids: dataids,
                            taskid: taskid
                        },
                        url: '/openApprove/setZWWQxAddSubmit',
                        method: 'POST',
                        sync: true,
                        success: function () {
                            XD.msg("设置成功");
                            view.findParentByType('setQxView').close();
                            window.wgrid.initGrid({taskid: taskid});
                        },
                        failure: function () {
                            XD.msg("设置失败");
                        }
                    });
                }
            },
            'setQxView button[itemId=setQxAddClose]': {
                click: function (view) {
                    view.findParentByType('setQxView').close();
                }
            },
            'openApproveGridView button[itemId=look]': {
                click: function (view) {
                    var approve = Ext.create("Ext.window.Window", {
                        width: '100%',
                        height: '100%',
                        plain: true,
                        header: false,
                        border: false,
                        closable: false,
                        frame: false,
                        draggable: false,//禁止拖动
                        resizable: false,//禁止缩放
                        modal: true,
                        closeToolText: '关闭',
                        layout: 'fit',
                        items: [{
                            xtype: 'EntryFormView'
                        }]
                    });
                    var grid = view.findParentByType('openApproveGridView');
                    openApproveGridView = view.findParentByType('openApproveGridView');
                    var selectAll = grid.down('[itemId=selectAll]').checked;
                    var entryids = [];
                    var nodeids = [];
                    var msgids = [];
                    if (selectAll) {
                        var deRecord = grid.acrossDeSelections;
                        var deEntryIds = [];
                        for (var i = 0; i < deRecord.length; i++) {
                            deEntryIds.push(deRecord[i].get('entryid'));
                        }
                        var openmsgs = this.getSelectAllEntry(deEntryIds);
                        for (var i = 0; i < openmsgs.length; i++) {
                            entryids.push(openmsgs[i].entryid);
                            nodeids.push(openmsgs[i].nodeid);
                            msgids.push(openmsgs[i].eleid);
                        }
                    } else {
                        var record = grid.selModel.getSelection();
                        for (var i = 0; i < record.length; i++) {
                            entryids.push(record[i].get('entryid'));
                            nodeids.push(record[i].get('nodeid'));
                            msgids.push(record[i].get('msgid'));
                        }
                    }
                    if (entryids.length == 0) {
                        XD.msg("请至少选择一条需要查看的数据");
                        return;
                    }
                    var entryid = entryids[0];
                    var form = approve.down('dynamicform');
                    form.nodeid = nodeids[0];
                    form.operate = 'look';
                    form.entryids = entryids;
                    form.nodeids = nodeids;
                    form.entryid = entryids[0];
                    form.msgids = msgids;
                    form.msgid = msgids[0];
                    this.initFormField(form, 'hide', nodeids[0]);
                    this.initFormData('look', form, entryid);
                    approve.show();
                    window.approves = approve;
                    Ext.on('resize', function (a, b) {
                        window.approves.setPosition(0, 0);
                        window.approves.fitContainer();
                    });
                }
            },
            'openApproveGridView button[itemId=startApprove]': {
                click: function (view) {
                    if (systemLoginType == '1') {

                    } else {
                        Ext.Ajax.request({
                            params: {
                                batchnum: window.batchnum,
                                type:approveText
                            },
                            url: '/openApprove/getApproveEntryId',
                            method: 'POST',
                            sync: true,
                            scope:this,
                            success: function (resp) {
                                var data = Ext.decode(resp.responseText);
                                if(data.success){
                                    var grid = view.findParentByType('openApproveGridView');
                                    window.wgrid = grid;
                                    var approve = Ext.create("Ext.window.Window", {
                                        width: '100%',
                                        height: '100%',
                                        plain: true,
                                        header: false,
                                        border: false,
                                        closable: false,
                                        frame: false,
                                        draggable: false,//禁止拖动
                                        resizable: false,//禁止缩放
                                        modal: true,
                                        closeToolText: '关闭',
                                        layout: 'fit',
                                        items: [{
                                            xtype: 'openApproveEntryFormView'
                                        }]
                                    });
                                    var entryids = [];
                                    var nodeids = [];
                                    var msgids = [];
                                    var record=data.data;
                                    for (var i = 0; i < record.length; i++) {
                                        entryids.push(record[i].entryid);
                                        nodeids.push(record[i].nodeid);
                                        msgids.push(record[i].msgid);
                                    }
                                    if (entryids.length < 1) {
                                        XD.msg("请至少选择一条数据");
                                        return;
                                    }
                                    var entryid = entryids[0];
                                    var form = approve.down('dynamicform');
                                    form.nodeid = nodeids[0];
                                    form.operate = 'look';
                                    form.entryids = entryids;
                                    form.nodeids = nodeids;
                                    form.entryid = entryids[0];
                                    form.msgids = msgids;
                                    form.msgid = msgids[0];
                                    this.initFormField(form, 'hide', nodeids[0]);
                                    this.initFormData('look', form, entryid);
                                    approve.show();
                                }else{
                                    XD.msg("没有可以审核的条目数据");
                                }
                            }
                        });
                    }
                }
            },
            'EntryFormView button[itemId=back]': {
                click: function () {
                    window.approves.close();
                    if(openApproveGridView!=undefined){
                        openApproveGridView.getStore().reload();
                    }
                }
            },
            'openApproveEntryFormView button[itemId=back]': {
                click: function (view) {
                    window.wgrid.getStore().reload();
                    view.findParentByType('window').close();
                }
            },

            'EntryFormView [itemId=preBtn],openApproveEntryFormView [itemId=preBtn]':{
                click:this.preHandler
            },
            'EntryFormView [itemId=nextBtn],openApproveEntryFormView [itemId=nextBtn]':{
                click:this.nextHandler
            },
            'openApproveEntryFormView button[itemId=saveId]': {  //保存
                click: function (view) {
                    var openApproveEntryFormView = view.findParentByType('openApproveEntryFormView');
                    var form = openApproveEntryFormView.down('dynamicform');
                    var entryunit = openApproveEntryFormView.down('[name=entryunit]').getValue();
                    var appraisedata = openApproveEntryFormView.down('[name=appraisedata]').getValue();
                    var appraisetext = openApproveEntryFormView.down('[name=appraisetext]').getValue();
                    // var updatetitle = openApproveEntryFormView.down('[name=updatetitle]').getValue();
                    var updatetitle = openApproveEntryFormView.down('[itemId=updatetitleId2]').getValue();
                    var firstresult = openApproveEntryFormView.down('[name=firstresult]').getValue();
                    var lastresult = openApproveEntryFormView.down('[name=lastresult]').getValue();
                    var firstappraiser = openApproveEntryFormView.down('[name=firstappraiser]').getValue();
                    var lastappraisetext = openApproveEntryFormView.down('[name=lastappraisetext]').getValue();
                    var lastappraiser = openApproveEntryFormView.down('[name=lastappraiser]').getValue();
                    var finalresult = openApproveEntryFormView.down('[name=finalresult]').getValue();
                    if(approveText=='Fs'){  //初审
                        // if(firstresult==''||firstresult==undefined){
                        //     XD.msg('请填写拟开放状态');
                        //     return;
                        // }
                        if(firstresult!=''&&firstresult=='不开放'){
                            if(appraisedata==''||appraisedata==undefined){
                                XD.msg('请填写鉴定依据');
                                return;
                            }
                        }else{
                            if(firstresult!=''&&(entryunit==''||entryunit==undefined)){
                                XD.msg('请填写档案所属单位');
                                return;
                            }
                        }
                        if(firstresult!=''&&(appraisetext==''||appraisetext==undefined)){
                            XD.msg('请填写初审鉴定意见');
                            return;
                        }
                    }
                    if(approveText=='Ls'){  //复审
                        // if(lastresult==''||lastresult==undefined){
                        //     XD.msg('请填写复审开放状态');
                        //     return;
                        // }
                        if(lastresult!=''&&lastresult=='不开放'){
                            if(appraisedata==''||appraisedata==undefined){
                                XD.msg('请填写鉴定依据');
                                return;
                            }
                        }else{
                            if(lastresult!=''&&(entryunit==''||entryunit==undefined)){
                                XD.msg('请填写档案所属单位');
                                return;
                            }
                        }
                        if(lastresult!=''&&(lastappraisetext==''||lastappraisetext==undefined)){
                            XD.msg('请填写复审鉴定意见');
                            return;
                        }
                    }
                    var that = this;
                    Ext.Ajax.request({
                        params: {
                            msgid:form.msgid,
                            finalresult:finalresult,
                            entryunit: entryunit,
                            appraisedata: appraisedata,
                            appraisetext:appraisetext,
                            updatetitle:updatetitle,
                            firstresult:firstresult,
                            lastresult:lastresult,
                            firstappraiser:firstappraiser,
                            lastappraisetext:lastappraisetext,
                            lastappraiser:lastappraiser
                        },
                        url: '/openApprove/setQxAddSubmit',
                        method: 'POST',
                        sync: true,
                        success: function () {
                            XD.msg("保存成功");
                            if(form.entryids && form.entryids.length > 1 && form.entryid != form.entryids[form.entryids.length-1]){
                                that.refreshFormData(form, 'next');
                            }else {
                                view.findParentByType('window').close();
                                window.wgrid.initGrid({taskid:taskid });
                            }
                        },
                        failure: function () {
                            XD.msg("保存失败");
                        }
                    });
                }
            },

            'approveAddView': {
                render: function (field) {
                    field.down('[itemId=selectApproveId]').on('change', function (val) {
                        field.down('[itemId=approveId]').setValue(val.value);
                    });
                },
                afterrender: function (field) {
                    if (typeof window.wareatext != "undefined") {
                        field.down('[itemId=approveId]').setValue(window.wareatext);
                    }
                }
            },
            'approveAddView button[itemId=approveAddSubmit]': {
                click: function (view) {
                    var areaText = view.findParentByType('approveAddView').down('[itemId=approveId]').value;
                    if ('' == areaText) {
                        XD.msg("请输入批示");
                        return;
                    }

                    if(isAddPostil){
                        XD.msg('您已添加过批示');
                        return;
                    }

                    window.wareatext = areaText;
                    var curdate = getNowFormatDate();
                    var rname = window.parent.realname ? window.parent.realname : window.parent.parent.realname;
                    var text = '意见：'+areaText+'\n'+flowsText+'：' + rname +'\n'+curdate;
                    if (typeof(window.wapprove) != 'undefined' && window.wapprove != '') {
                        text = window.wapprove + '\n\n' + text;
                    }
                    window.wform.getComponent('approveId').setValue(text);
                    view.findParentByType('approveAddView').close();
                    isAddPostil = true;
                }
            },

            'approveAddView button[itemId=approveAddClose]': {
                click: function (view) {
                    view.findParentByType("approveAddView").close();
                }
            },
            'openApproveFormView button[itemId=openApproveFormSubmit]': {
                click: function (view) {
                    XD.confirm("是否确认完成审批？", function () {
                        var textArea = window.wform.getComponent('approveId').value;
                        var nextNode = view.up('openApproveFormView').down('[itemId=nextNodeId]').getValue();
                        var nextSpman = view.up('openApproveFormView').down('[itemId=nextSpmanId]').getValue();
                        var sendMsg = view.up('openApproveFormView').down('[itemId=sendmsgId]').getValue();

                        if (nextNode == null) {
                            XD.msg("下一环节不能为空");
                            return;
                        }

                        if(view.up('openApproveFormView').down('[itemId=nextNodeId]').rawValue!='结束'
                            &&(view.up('openApproveFormView').down('[itemId=nextSpmanId]').rawValue=='')){
                            XD.msg('下一环节审批人不能为空');
                            return ;
                        }
                        var grid = view.findParentByType('openApproveView').down('openApproveGridView');
                        var count =0;
                        var gridcount = grid.getStore().getCount();
                        for(var i =0;i<gridcount;i++){
                            if(grid.getStore().getAt(i).data.result=='拒绝'){
                                count++;
                            }
                        }

                        if(''==textArea||!isAddPostil){
                            var curdate=getNowFormatDate();
                            var rname = window.parent.realname ? window.parent.realname : window.parent.parent.realname;
                    		if (textArea != '') {
                                if(count==gridcount){
                                    textArea += '\n\n意见：不通过\n'+flowsText+'：' + rname +'\n'+curdate;
                                }else{
                                    textArea += '\n\n意见：通过\n'+flowsText+'：' + rname +'\n'+curdate;
                                }
                    		} else {
                                if(count==gridcount){
                                    textArea += '意见：不通过\n'+flowsText+'：' + rname +'\n'+curdate;
                                }else{
                                    textArea += '意见：通过\n'+flowsText+'：' + rname +'\n'+curdate;
                                }
                    		}
                        }
                        Ext.Ajax.request({
                            params: {
                                textArea: textArea,
                                nextNode: nextNode,
                                nextSpman: nextSpman,
                                taskid: taskid,
                                nodeId: nodeId,
                                sendMsg:sendMsg
                            },
                            url: '/openApprove/approveSubmit',
                            method: 'post',
                            async: false,
                            success: function (resp, opts) {
                                var data = Ext.decode(resp.responseText);
                                XD.msg(data.msg);
                                setTimeout(function () {
                                    if (iflag == '1') {
                                        parent.wgridView.getStore().reload();
                                        parent.approve.close();
                                    } else {
                                        parent.closeObj.close(parent.layer.getFrameIndex(window.name));
                                    }
                                }, 1000);
                            },
                            failure: function () {
                                XD.msg("审批失败");
                            }
                        });
                    });
                }
            },
            'openApproveFormView button[itemId=openApproveFormZz]': {
                click: function () {
                    XD.confirm("<span style=\"color:red\">注：退回后这次审批做的鉴定内容将会全部取消！</span><br><br>" +
                        "<span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
                        "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
                        "是否确认退回？</span>", function () {
                    	var textArea = window.wform.getComponent('approveId').value;
	                    var curdate = getNowFormatDate();
	                    var rname = window.parent.realname ? window.parent.realname : window.parent.parent.realname;
            			if (textArea == '') {
            				textArea += '意见：不通过\n'+flowsText+'：' + rname +'\n'+curdate;
                		} else if (textArea.indexOf('驳回') < 0) {
                			textArea += '\n\n意见：不通过\n'+flowsText+'：' + rname +'\n'+curdate;
                		}
                        Ext.Ajax.request({
                            params: {
                                textArea: textArea,
                                taskid: taskid,
                                nodeId: nodeId
                            },
                            url: '/openApprove/returnOpen',
                            method: 'POST',
                            sync: true,
                            success: function () {
                                XD.msg("审批完成");
                                setTimeout(function () {
                                    if (iflag == '1') {
                                        parent.wgridView.getStore().reload();
                                        parent.approve.close();
                                    } else {
                                        parent.closeObj.close(parent.layer.getFrameIndex(window.name));
                                    }
                                }, 1000);
                            },
                            failure: function () {
                                XD.msg("审批失败");
                            }
                        });
                    });
                }
            },

            'openApproveFormView button[itemId=openApproveBackPre]':{
                click:function(view){
                    XD.confirm("<span style=\"color:red\">注：将此条单据退回到上一个审批环节重新审批。确定后，审批单据会退回到审核员审批环节，让审核员重新审核！</span><br><br>" +
                        "<span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
                        "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
                        "是否确定退回上一环节？</span>", function () {
                        var textArea = window.wform.getComponent('approveId').value;
                        var curdate = getNowFormatDate();
                        var rname = window.parent.realname ? window.parent.realname : window.parent.parent.realname;

                        if (textArea == '') {
                            textArea += '意见：不通过\n'+flowsText+'：' + rname +'\n'+curdate;
                        } else if (textArea.indexOf('驳回') < 0) {
                            textArea += '\n\n意见：不通过\n'+flowsText+'：' + rname +'\n'+curdate;
                        }

                        Ext.Ajax.request({
                            params: {
                                taskid:taskid,
                                textArea:textArea
                            },
                            url: '/openApprove/openapprovebackpre',
                            method: 'POST',
                            sync: true,
                            success: function (resp) {
                                XD.msg('审批完成');
                                Ext.defer(function () {
                                    if(iflag=='1'){
                                        // parent.wgridView.initGrid({state:'待处理',type:'开放'});
                                        parent.wgridView.getStore().reload();
                                        parent.approve.close();
                                    }else{
                                        parent.closeObj.close(parent.layer.getFrameIndex(window.name));
                                    }
                                },1000);
                            },
                            failure : function() {
                                XD.msg('操作失败');
                            }
                        });
                    },this);
                }
            }
        });
    },
    //获取数据采集应用视图
    findView: function (btn) {
        return btn.findParentByType('openApproveGridView');
    },

    findFormView: function (btn) {
        return btn.down('EntryFormView');
    },

    printDocHandler:function (btn) {
        var params= {};
        var grid = btn.findParentByType('openApproveView').down('openApproveFormView');
        var batchnum = grid.getForm().getValues().batchnum;
        var docid=grid.getForm().getValues().id;
        var reportName;
        if(btn.itemId=='printOpen'){//打印开放审查登记表
            reportName='开放审批-开放审查登记表';
        }else if(btn.itemId=='printNotOpen'){//打印不开放审查登记表
            reportName='开放审批-不开放审查登记表';
        }else if(btn.itemId=='printOpenAppraisal'){//打印不开放审查登记表
            reportName='档案开放鉴定汇报';
            params['date'] = grid.getForm().getValues().submitdate;
        }
        var type="初审",approve="通过";
        Ext.Ajax.request({
            params: {opendocid: docid},
            url: '/dataopen/getDealDetails',
            method: 'post',
            sync: false,
            success: function (resp) {
                var respText = Ext.decode(resp.responseText);
                var data = respText.content;
                for (var i = 0; i < data.length; i++) {
                    if (data[i].node == "复审") {
                        if (data[i].status != "处理中") {
                            type = "复审";
                            if (data[i].approve.indexOf("不通过") != -1) {
                                approve = "不通过"
                            }
                        } else {
                            approve = "不通过"
                        }
                    }
                }
                if (reportServer == 'UReport') {
                    params['batchnum'] = batchnum;
                    params['type'] = type;
                    params['approve'] = approve;
                    XD.UReportPrint(null, reportName, params);
                } else if (reportServer == 'FReport') {
                    XD.FRprint(null, reportName, batchnum.length > 0 ? "'batchnum':'" + batchnum + "'" : '');
                }
            }
        })
    },

    initFormField:function(form, operate, nodeid){
//        if(form.nodeid!=nodeid){
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
    getCurrentOpenApproveform:function(btn) {
        if(btn.up('EntryFormView')){
            return btn.up('EntryFormView');
        }else{
            return btn.up('openApproveEntryFormView');
        }
    },

//点击上一条
    preHandler:function(btn){
        var currentOpenApproveform = this.getCurrentOpenApproveform(btn);
        var form = currentOpenApproveform.down('dynamicform');
        this.refreshFormData(form, 'pre');
    },

//点击下一条
    nextHandler:function(btn){
        var currentOpenApproveform = this.getCurrentOpenApproveform(btn);
        var form = currentOpenApproveform.down('dynamicform');
        this.refreshFormData(form, 'next');
    },

    refreshFormData:function(form, type){
        var entryids = form.entryids;
        var nodeids = form.nodeids;
        var currentEntryid = form.entryid;
        var msgids = form.msgids;
        var msgid;
        var entryid;
        var nodeid;
        for(var i=0;i<entryids.length;i++){
            if(type == 'pre' && entryids[i] == currentEntryid){
                if(i==0){
                    i=entryids.length;
                }
                entryid = entryids[i-1];
                nodeid = nodeids[i-1];
                msgid = msgids[i-1];
                break;
            }else if(type == 'next' && entryids[i] == currentEntryid){
                if(i==entryids.length-1){
                    i=-1;
                }
                entryid = entryids[i+1];
                nodeid = nodeids[i+1];
                msgid = msgids[i+1];
                break;
            }
        }
        form.entryid = entryid;
        form.msgid = msgid;
        if(form.operate != 'undefined'){
            this.initFormField(form, 'hide', nodeid);//上下条时切换模板
            this.initFormData(form.operate, form, entryid);
            return;
        }
        this.initFormField(form, 'hide', nodeid);
        this.initFormData('look', form, entryid);
    },

    initFormData:function (operate, form, entryid) {
        var formview;
        if(form.up('EntryFormView')){
            formview = form.up('EntryFormView');
        }else{
            formview = form.up('openApproveEntryFormView');
        }
        var nullvalue = new Ext.data.Model();
        var fields = form.getForm().getFields().items;
        for(var i = 0; i < fields.length; i++){
            nullvalue.set(fields[i].name, null);
        }
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
        form.loadRecord(nullvalue);
        var etips = formview.down('[itemId=etips]');
        etips.show();
        form.reset();
        if(formview.down('[itemId=fieldsetFormId]')){
            Ext.Ajax.request({
                params: {
                    id:form.msgid
                },
                url: '/openApprove/getOpenApproveMsg',
                method: 'POST',
                scope:this,
                success:function(response){
                    var msg = Ext.decode(response.responseText).data;
                    formview.down('[name=appraisetext]').setValue(msg.firstresult);
                    formview.down('[name=entryunit]').setValue(msg.entryunit);
                    formview.down('[name=appraisedata]').setValue(msg.appraisedata);
                    formview.down('[name=appraisetext]').setValue(msg.appraisetext);
                    formview.down('[name=updatetitle]').setValue(msg.updatetitle);
                    formview.down('[itemId=updatetitleId2]').setValue(msg.updatetitle);
                    formview.down('[name=firstresult]').setValue(msg.firstresult);
                    if(approveText=='Fs'){  //初审
                        formview.down('[name=firstappraiser]').setValue(realName);
                    }else{
                        formview.down('[name=firstappraiser]').setValue(msg.firstappraiser);
                    }
                    formview.down('[name=lastappraisetext]').setValue(msg.lastappraisetext);
                    var lastresult = formview.down('[name=lastresult]');
                    if(approveText=='Ls'){  //复审
                        formview.down('[name=lastappraiser]').setValue(realName);
                        var store = formview.down('[name=lastresult]').getStore();
                        var select;
                        var selectR;
                        if(msg.lastresult!=''&&msg.lastresult!=null){
                            selectR = msg.lastresult;
                        }else{
                            selectR = msg.firstresult;
                        }
                        for(var i=0;i<store.getCount();i++){
                            var record = store.getAt(i);
                            if(selectR==record.get('Value')){
                                select = record;
                                break;
                            }
                        }
                        lastresult.select(select);
                        lastresult.fireEvent("select",lastresult,select);
                    }else{
                        formview.down('[name=lastappraiser]').setValue(msg.lastappraiser);
                        lastresult.setValue(msg.lastresult);
                    }
                    if(approveText=='Jd'){
                        if(msg.finalresult!=''&&msg.finalresult!=null){
                            formview.down('[name=finalresult]').setValue(msg.finalresult);
                        }else{
                            formview.down('[name=finalresult]').setValue(msg.lastresult);
                        }
                    }
                }
            });
        }
        Ext.Ajax.request({
            method:'GET',
            scope:this,
            url:'/management/entries/'+entryid,
            success:function(response){
                var entry = Ext.decode(response.responseText);
                form.loadRecord({getData:function(){return entry;}});
                //字段编号，用于特殊的自定义字段(范围型日期)
                var fieldCode = form.getRangeDateForCode();
                if(fieldCode!=null){
                    //动态解析数据库日期范围数据并加载至两个datefield中
                    form.initDaterangeContent(entry);
                }
                //初始化原文数据
                var eleview = formview.down('electronic');
                eleview.initData(entryid);
                var solidview = formview.down('solid');
                solidview.initData(entryid);
                // var longview = formview.down('long');
                // longview.initData(entryid);
//                form.formStateChange(operate);
                form.fileLabelStateChange(eleview,operate);
                form.fileLabelStateChange(solidview,operate);
                // form.fileLabelStateChange(longview,operate);
            }
        });
    },

    //选择所有页
    getSelectAllEntry:function (deEntryIds) {
        var openMsgs;
        Ext.Ajax.request({
            url: '/openApprove/getSelectAllEntry',
            async:false,
            params:{
                taskid:taskid,
                deEntryIds:deEntryIds
            },
            success: function (response) {
                openMsgs = Ext.decode(response.responseText).data;
            }
        });
        return openMsgs;
    }
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
function getNowFormatDate() {
    var date = new Date();
    var seperator1 = "";
    var seperator2 = ":";
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
    // + " " + date.getHours() + seperator2 + date.getMinutes();
    // + seperator2 + date.getSeconds();
    return currentdate;
}