/**
 * Created by RonJiang on 2018/04/17.
 */
Ext.define('Feedback.controller.FeedbackController', {
    extend: 'Ext.app.Controller',

    views: [
        'FeedbackView','FeedbackGridView',
        'FeedbackAddFormView','FeedbackReplyFormView',
        'FeedbackLookFormView','AppraiseView','AppraiseGridView'
    ],//加载view
    stores: ['FeedbackGridStore','AppraiseGridStore'],//加载store
    models: ['FeedbackGridModel','AppraiseGridModel'],//加载model

    init: function () {
        this.control({
            'feedback':{
                afterrender:function (view) {
                    if(buttonflag=='2'){   //个人所有评价
                        var appraiseGridView = view.down('appraiseGridView');
                        view.setActiveItem(appraiseGridView);
                    }
                }
            },
            'appraiseGridView':{
                afterrender:function (view) {
                    view.initGrid();
                }
            },
            'feedbackGridView':{
                afterrender:function (view) {
                    var buttons = view.down("toolbar").query('button');
                    var tbseparator = view.down("toolbar").query('tbseparator');
                    var type;
                    if(buttonflag=='1'){//利用平台
                        if(buttons.length>0){
                            buttons[0].show();//增加按钮
                            buttons[1].hide();//回复按钮
                            buttons[2].hide();//删除按钮
                        }
                        if(tbseparator.length>0){
                            tbseparator[0].hide();
                            tbseparator[1].hide();
                        }
                        type = 'self';
                    }else{//管理平台
                        if(buttons.length>0){
                            buttons[0].hide();//增加按钮
                            buttons[1].show();//回复按钮
                            buttons[2].show();//删除按钮
                        }
                        if(tbseparator.length>0){
                            tbseparator[0].hide();
                            tbseparator[1].show();
                        }
                        type = 'all';
                    }
                    view.initGrid({type:type});
                }
            },
            'feedbackGridView button[itemId=feedbackAdd]':{//增加
                click:this.addHandler
            },
            'feedbackGridView button[itemId=feedbackReply]':{//回复
                click:this.replyHandler
            },
            'feedbackGridView button[itemId=feedbackDel]':{//删除
                click:this.delHandler
            },
            'feedbackGridView button[itemId=feedbackLook]':{//查看
                click:this.lookHandler
            },

            'feedbackReplyFormView':{//添加键盘监控
                afterrender:this.addKeyAction
            },
            'feedbackAddFormView':{//添加键盘监控
                afterrender:this.addKeyAction
            },

            'feedbackAddFormView button[itemId=save]':{//增加反馈　保存
                click:this.addSubmit
            },
            'feedbackAddFormView button[itemId=back]':{//增加反馈　返回
                click:function (btn) {
                    this.activeGrid(btn);
                }
            },
            'feedbackReplyFormView button[itemId=save]':{//回复反馈　保存
                click:this.replySubmit
            },
            'feedbackReplyFormView button[itemId=back]':{//回复反馈　返回
                click:function (btn) {
                    this.activeGrid(btn);
                }
            },
            'feedbackLookFormView button[itemId=back]':{//查看反馈　返回
                click:function (btn) {
                    this.activeGrid(btn);
                    var feedbackLookFormView = btn.findParentByType('feedbackLookFormView');
                    var feedback = feedbackLookFormView.feedback;
                    if(feedback.flag=='已回复'&&(typeof (feedback.appraise)== 'undefined'||feedback.appraise== '')&&buttonflag=='1'){  //已回复以及未进行评分的
                        var appraiseView = Ext.create("Feedback.view.AppraiseView");
                        appraiseView.feedback = feedback;
                        appraiseView.grid = feedbackLookFormView.grid;
                        appraiseView.show();
                    }
                }
            },

            'appraiseView button[itemId=setAppraiseSubmit]':{//评价 提交
                click:function (btn) {
                    var appraiseView = btn.findParentByType('appraiseView');
                    var feedback = appraiseView.feedback;
                    var labeltext = appraiseView.down('[itemId=labelId]').text;
                    var content = appraiseView.down('[itemId=contentId]').getValue();
                    if(!content){
                        content = '无评价内容';
                    }
                    if(labeltext.indexOf("5-无可挑剔")!=-1){
                        labeltext = "无可挑剔";
                    }else if(labeltext.indexOf("4-非常满意")!=-1){
                        labeltext = "非常满意";
                    }else if(labeltext.indexOf("3-满意")!=-1){
                        labeltext = "满意";
                    }else if(labeltext.indexOf("2-一般")!=-1){
                        labeltext = "一般";
                    }else if(labeltext.indexOf("1-很差")!=-1){
                        labeltext = "很差";
                    }
                    Ext.Ajax.request({
                        params: {
                            feedbackid: feedback.feedbackid,
                            labeltext: labeltext,
                            content:content
                        },
                        url: '/feedback/setAppraise',
                        method: 'POST',
                        success: function () {
                            XD.msg('评分成功');
                            appraiseView.close();
                            appraiseView.grid.getStore().reload();
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            'appraiseView button[itemId=setAppraiseClose]':{//评价 返回
                click:function (btn) {
                    btn.findParentByType('appraiseView').close();
                }
            }
        });
    },

    //获取反馈管理应用视图
    findView: function (btn) {
        return btn.up('feedback');
    },

    //获取增加反馈表单界面视图
    findAddformView: function (btn) {
        return this.findView(btn).down('feedbackAddFormView');
    },

    //获取回复反馈表单界面视图
    findReplyformView: function (btn) {
        return this.findView(btn).down('feedbackReplyFormView');
    },

    //获取查看反馈表单界面视图
    findLookformView: function (btn) {
        return this.findView(btn).down('feedbackLookFormView');
    },

    //获取列表界面视图
    findGridView: function (btn) {
        return this.findView(btn).getComponent('gridview');
    },

    //切换到回复反馈表单界面视图
    activeReplyform: function (btn) {
        var view = this.findView(btn);
        var replyformview = this.findReplyformView(btn);
        view.setActiveItem(replyformview);
        return replyformview;
    },

    //切换到增加反馈表单界面视图
    activeAddform: function (btn) {
        var view = this.findView(btn);
        var addformview = this.findAddformView(btn);
        view.setActiveItem(addformview);
        return addformview;
    },

    //切换到查看反馈表单界面视图
    activeLookform: function (btn) {
        var view = this.findView(btn);
        var lookformview = this.findLookformView(btn);
        view.setActiveItem(lookformview);
        return lookformview;
    },

    //切换到列表界面视图
    activeGrid: function (btn,flag) {
        var view = this.findView(btn);
        var replyform = this.findReplyformView(btn);
        var addform = this.findAddformView(btn);
        var grid = this.findGridView(btn);
        view.setActiveItem(grid);
        replyform.saveBtn = undefined;
        addform.saveBtn = undefined;
        if(flag){//根据参数确定是否需要刷新数据
            grid.notResetInitGrid();
        }
    },

    addHandler:function (btn) {//打开增加反馈表
        var addform = this.findAddformView(btn);
        addform.saveBtn = addform.down('[itemId=save]');
        addform.operateFlag = 'add';
        var grid = this.findGridView(btn);
        this.initAddformData(addform);
    },

    replyHandler:function (btn) {//打开回复反馈表单
        var replyform = this.findReplyformView(btn);
        replyform.saveBtn = replyform.down('[itemId=save]');
        replyform.operateFlag = 'reply';
        var grid = this.findGridView(btn);
        var record = grid.getSelectionModel().getSelection();
        if (record.length != 1) {
            XD.msg('请选择一条需要回复的反馈信息');
            return;
        }
        var feedbackid = record[0].get('feedbackid');
        var feedbackStatus = record[0].get('flag');
        if(feedbackStatus=='已回复'){
            XD.msg('该反馈信息已回复');
            return;
        }
        this.initReplyformData(replyform,record[0]);
    },

    delHandler:function (btn) {//删除反馈信息
        var grid = this.findGridView(btn);
        var record = grid.getSelectionModel().getSelection();
        if (record.length < 1) {
            XD.msg('请选择需要删除的反馈信息');
            return;
        }
        XD.confirm('确定要删除这' + record.length + '条数据吗',function(){
            var tmp = [];
            for (var i = 0; i < record.length; i++) {
                tmp.push(record[i].get('feedbackid'));
            }
            var feedbackids = tmp.join(',');
            Ext.Ajax.request({
                method: 'DELETE',
                url: '/feedback/feedbacks/' + feedbackids,
                success: function (response) {
                    XD.msg(Ext.decode(response.responseText).msg);
                    grid.delReload(record.length);
                }
            })
        },this);
    },

    lookHandler:function (btn) {//查看反馈信息
        var grid = this.findGridView(btn);
        var record = grid.getSelectionModel().getSelection();
        if (record.length != 1) {
            XD.msg('请选择一条需要查看的反馈信息');
            return;
        }
        var feedbackid = record[0].get('feedbackid');
        var lookform = this.findLookformView(btn);
        lookform.grid = grid;
        this.initLookformData(lookform,feedbackid);
    },

    addSubmit:function (btn) {//增加反馈信息保存
        var addform = this.findAddformView(btn);
        addform.submit({
            method: 'POST',
            url: '/feedback/feedbacks',
            scope: this,
            success: function (form, action) {
                //切换到列表界面,同时刷新列表数据
                this.activeGrid(btn,true);
                XD.msg(action.result.msg);
            },
            failure: function () {
                XD.msg('操作失败');
            }
        });
    },

    replySubmit:function (btn) {//回复反馈信息保存
        var replyform = this.findReplyformView(btn);
        replyform.submit({
            method: 'POST',
            url: '/feedback/feedbackReply',
            scope: this,
            success: function (form, action) {
                //切换到列表界面,同时刷新列表数据
                this.activeGrid(btn,true);
                XD.msg(action.result.msg);
            },
            failure: function () {
                XD.msg('操作失败');
            }
        });
    },

    initReplyformData:function(form,record){
        // form.reset();
        this.activeReplyform(form);
        Ext.Ajax.request({
            method: 'GET',
            scope: this,
            url: '/feedback/feedbacks/' + record.get('feedbackid'),
            success: function (response) {
                var feedback = Ext.decode(response.responseText);
                feedback.replytime = Ext.util.Format.date(new Date(),'Y-m-d H:i:s');//自动设置回复日期时间
                Ext.Ajax.request({//自动设置提交人真实姓名
                    async:false,
                    url: '/user/getUserRealname',
                    success:function (response) {
                        feedback.replyby = Ext.decode(response.responseText).data;
                    }
                });
                form.loadRecord({getData: function () {return feedback;}});
            }
        });
    },

    initAddformData:function(form){
        // form.reset();
        this.activeAddform(form);
        var askmanField = form.getForm().findField('askman');
        var asktimeField = form.getForm().findField('asktime');
        if(asktimeField){
            asktimeField.setValue(Ext.util.Format.date(new Date(),'Y-m-d H:i:s'));//自动设置反馈增加日期时间
        }
        if(askmanField){
            Ext.Ajax.request({//自动设置投件人真实姓名
                async:false,
                url: '/user/getUserRealname',
                success:function (response) {
                    askmanField.setValue(Ext.decode(response.responseText).data);
                }
            });
        }
    },

    initLookformData:function(form,feedbackid){
        form.reset();
        this.activeLookform(form);
        Ext.Ajax.request({
            method: 'GET',
            scope: this,
            url: '/feedback/feedbacks/' + feedbackid,
            success: function (response) {
                var feedback = Ext.decode(response.responseText);
                if(typeof(feedback.borrowdocid) != 'undefined'&&feedback.borrowdocid != ''){  //判断反馈是否为借阅评分
                    form.down('[name=appraise]').show();
                    form.down('[itemId=displayId]').show();
                    form.down('[name=appraisetext]').hide();
                    form.down('[itemId=appraisetextDisId]').hide();
                    form.down('[name=content]').setFieldLabel("反馈内容");
                }else{
                    if(feedback.flag=='已回复'&&typeof (feedback.appraise)!='undefined'&&feedback.appraise!=''){
                        form.down('[name=appraise]').show();
                        form.down('[itemId=displayId]').show();
                        form.down('[name=appraisetext]').show();
                    }else{
                        form.down('[name=appraise]').hide();
                        form.down('[itemId=displayId]').hide();
                        form.down('[name=appraisetext]').hide();
                    }
                }
                form.loadRecord({getData: function () {return feedback;}});
                form.feedback = feedback;
            }
        });
    },

    //监听键盘按下事件
    addKeyAction:function (view) {
        var controller = this;
        view.saveBtn = view.down('[itemId=save]');
        document.onkeydown = function () {
            var oEvent = window.event;
            if (oEvent.ctrlKey && !oEvent.shiftKey && !oEvent.altKey && oEvent.keyCode == 83) { //这里只能用alt，shift，ctrl等去组合其他键event.altKey、event.ctrlKey、event.shiftKey 属性
                // XD.msg('Ctrl+S');
                Ext.defer(function () {
                    if(view.saveBtn && view.operateFlag=='reply'){//此处若不增加operateFlag判断，点击树节点后初次渲染feedback表单时，按下ctrl+s会调用此方法
                        controller.replySubmit(view.saveBtn);//回复
                    }
                    if(view.saveBtn && view.operateFlag=='add'){//此处若不增加operateFlag判断，点击树节点后初次渲染feedback表单时，按下ctrl+s会调用此方法
                        controller.addSubmit(view.saveBtn);//增加
                    }
                },1);
                event.returnValue = false;//阻止event的默认行为
                // return false;//阻止event的默认行为
            }
        }
    }
});