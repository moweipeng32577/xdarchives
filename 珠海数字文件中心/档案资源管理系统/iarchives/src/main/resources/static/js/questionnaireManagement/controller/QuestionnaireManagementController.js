var clickCount = 1;//问题的序号
Ext.define('QuestionnaireManagement.controller.QuestionnaireManagementController', {
    extend: 'Ext.app.Controller',
    views: ['QuestionnaireManagementGridView', 'QuestionnaireAddFormView', 'StickView', 'AnswerFormView', 'VerificationCodeView',
        'QuestionnaireLookFormView'],
    stores: ['QuestionnaireManagementGridStore'],
    models: ['QuestionnaireManagementGridModel'],
    init: function () {

        this.control({
            'questionnaireManagementGridView': {
                afterrender: function (view) {
                    if (type == 1) {//利用平台
                        view.initGrid({type: 1});
                        view.down('[itemId=questionnaireAdd]').hide();
                        view.down('[itemId=questionnaireEdit]').hide();
                        view.down('[itemId=publishBtnID]').hide();
                        view.down('[itemId=questionnaireLook]').hide();
                        view.down('[itemId=questionnaireDel]').hide();
                        view.down('[itemId=canclePublishBtnID]').hide();
                        view.down('[itemId=questionnaireStick]').hide();
                        view.down('[itemId=statistics]').hide();
                        // view.down('[itemId=lookAnswer]').hide();
                        view.down('[itemId=cancelStick]').hide();//隐藏取消置顶
                        view.columns[view.columns.length - 1].setHidden(false);//显示问卷填写状态
                    } else if (type == 2) {
                        view.initGrid({type: 2});
                        view.down('[itemId=questionnaireAdd]').hide();
                        view.down('[itemId=questionnaireEdit]').hide();
                        view.down('[itemId=publishBtnID]').hide();
                        view.down('[itemId=questionnaireLook]').hide();
                        view.down('[itemId=questionnaireDel]').hide();
                        view.down('[itemId=canclePublishBtnID]').hide();
                        view.down('[itemId=questionnaireStick]').hide();
                        view.down('[itemId=statistics]').hide();
                        view.down('[itemId=answerQuestionnaire]').hide();
                        view.down('[itemId=cancelStick]').hide();
                    } else {
                        view.initGrid();
                        view.down('[itemId=lookAnswer]').hide();
                        view.down('[itemId=answerQuestionnaire]').hide();
                    }
                    if (flag == 0) {
                        view.down('[itemId=answerQuestionnaire]').click();
                        view.close();
                    }
                }
            },
            'questionnaireManagementGridView button[itemId=questionnaireAdd]': {//增加
                click: function (btn) {
                    var view = btn.findParentByType('questionnaireManagementGridView');
                    window.cardobj = {grid: view};
                    var addView = Ext.create('QuestionnaireManagement.view.QuestionnaireAddFormView', {
                        title: '新增问卷',
                        operate: 'add',
                        questionnaireManagementGridView: view
                    });
                    var questionAddBtn = addView.down('[itemId=questionAdd]');
                    clickCount = 0;
                    this.addQuestionnaire(questionAddBtn);
                    addView.show();
                }
            },
            'questionnaireManagementGridView button[itemId=answerQuestionnaire]': {//填写问卷
                click: function (btn) {
                    this.answerQuestionnaire(btn);
                }
            },
            'questionnaireManagementGridView button[itemId=questionnaireDel]': {//删除问卷
                click: this.delQuestionn
            },
            'questionnaireAddFormView button[itemId=questionnaireAddSubmit]': {//新增问卷提交
                click: this.submitQuestionnaire
            },
            'questionnaireAddFormView button[itemId=questionAdd]': {//新增问题
                click: this.addQuestionnaire
            },
            'questionnaireAddFormView button[itemId=questionDel]': {//删除问题
                click: this.delQuestionnaire
            },
            'questionnaireAddFormView button[itemId=questionnaireAddClose]': {//新增、修改与查看问卷页面的关闭
                click: this.questionnaireAddClose
            },
            'questionnaireManagementGridView button[itemId=questionnaireLook]': {//查看
                click: function (btn) {
                    this.lookQuestionnaires(btn);
                }
            },
            'questionnaireManagementGridView button[itemId=questionnaireEdit]': {//修改
                click: function (btn) {
                    this.showQuestionnaire(btn, 'edit');
                }
            },
            'questionnaireManagementGridView button[itemId=publishBtnID]': {//发布
                click: function (btn) {
                    this.updatePublishQuestionnaire(btn, '1');
                }
            },
            'questionnaireManagementGridView button[itemId=canclePublishBtnID]': {//取消发布
                click: function (btn) {
                    this.updatePublishQuestionnaire(btn, '0');
                }
            },
            'questionnaireManagementGridView button[itemId=questionnaireStick]': {//置顶
                click: function (view) {
                    var select = view.findParentByType('questionnaireManagementGridView').getSelectionModel();
                    var questionnaires = select.getSelection();
                    if (select.getCount() == 0) {
                        XD.msg('至少选择一条数据');
                        return;
                    }
                    var ids = [];
                    for (var i = 0; i < questionnaires.length; i++) {
                        ids.push(questionnaires[i].get('questionnaireID'));
                    }
                    var stickWin = Ext.create('QuestionnaireManagement.view.StickView', {
                        questionnaireIDs: ids,
                        informGrid: view.up('questionnaireManagementGridView')
                    });
                    stickWin.show();
                }
            },
            'stickView button[itemId=stickSubmit]': {//置顶页面提交
                click: function (view) {
                    var form = view.findParentByType('stickView').down('form');
                    Ext.Ajax.request({
                        params: {ids: view.up('stickView').questionnaireIDs, level: form.down('combobox').value},
                        url: '/questionnaireManagement/setStick',
                        method: 'POST',
                        success: function (resp) {
                            var respText = Ext.decode(resp.responseText);
                            XD.msg(respText.msg);
                            view.up('stickView').informGrid.notResetInitGrid();
                            view.up('stickView').close();
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },
            'stickView button[itemId=stickClose]': {//置顶页面关闭
                click: function (view) {
                    view.up('stickView').close();
                }
            },
            'questionnaireManagementGridView button[itemId=statistics]': {//统计
                click: function (btn) {
                    var view = btn.findParentByType('questionnaireManagementGridView');
                    var ids = [];
                    var params = {};
                    Ext.each(view.getSelectionModel().getSelection(), function () {
                        ids.push(this.get('questionnaireID').trim());
                    });
                    if (reportServer == 'UReport') {
                        params['docid'] = ids.join(",");
                        XD.UReportPrint(null, '问卷数量统计表', params);
                    } else if (reportServer == 'FReport') {
                        XD.FRprint(null, '问卷数量统计表', params);
                    }

                }
            },
            'answerFormView button[itemId=submit]': {//答卷页面的提交
                click: function (btn) {
                    //校验必答题是否已经回答
                    var view = btn.up('answerFormView');
                    var formItems = view.items.items;
                    for (var i = 1; i < formItems.length; i++) {
                        var isNecessary = formItems[i].down('[itemId=isNecessary]').getValue();
                        var type = formItems[i].down('[itemId=type]').getValue();
                        if (isNecessary == '1') {
                            var value = formItems[i].getValues();
                            if (type == '1' && value['optional'] == "") {//填空題未填
                                XD.msg('有必答题未答！');
                                return;
                            } else if (type != '1' && value.hasOwnProperty('optional') == false) {//选择题未选
                                XD.msg('有必答题未答！');
                                return;
                            } else if (type != '1' && value['optional'] == '其他' && value.hasOwnProperty('other') && value['other'] == '') {//选择题选择其他但是未填
                                XD.msg('有必答题未答！');
                                return;
                            }
                        }
                    }
                    var questionnaireID = formItems[0].down('[itemId=questionnaireID]').getValue();
                    var verificationCodeView = Ext.create('QuestionnaireManagement.view.VerificationCodeView', {
                        answerFormView: view, questionnaireID: questionnaireID
                    });
                    verificationCodeView.show();
                }
            },
            'verificationCodeView button[itemId=verificationCodeSubmit]': {//验证码页面的提交
                click: function (btn) {
                    var view = btn.findParentByType('verificationCodeView');
                    var answerFormView = view.answerFormView;
                    var form = view.down('form');
                    var value = form.getValues();
                    var verificationCode = form.down('[itemId=verificationCode]');
                    if (value['verificationCode'] == '' || value['verificationCode'] == 'undefined') {
                        XD.msg('您还没有填写验证码！');
                        return;
                    }
                    Ext.Ajax.request({
                        url: '/questionnaireManagement/checkVerificationCode',
                        method: 'POST',
                        params: {
                            verificationCode: value['verificationCode']
                        },
                        success: function (resp) {
                            var responseText = Ext.decode(resp.responseText);
                            var verificationCodeImg = form.down('[itemId=verificationCodeImg]');
                            if (responseText.msg == '验证码有误！') {
                                XD.msg(responseText.msg);
                                verificationCodeImg.setSrc('/verificationCode/getVerificationCode?_=' + (new Date()).getTime());
                                verificationCode.reset();
                                return;
                            } else {
                                var fromItems = [];
                                fromItems = answerFormView.items.items;
                                var answers = [];
                                for (var i = 1; i < fromItems.length; i++) {
                                    var formValue = fromItems[i].getValues();
                                    var answer = new Object();
                                    answer.questionID = formValue['questionID'];
                                    answer.type = formValue['type'];
                                    if (formValue.hasOwnProperty('other') && formValue['other'] != "") {
                                        var other = formValue['optional'] + ':' + formValue['other'];
                                        answer.answer = other;
                                    } else {
                                        if (typeof (formValue['optional']) == 'object') {//多选
                                            answer.answer = '';
                                            for (var j = 0; j < formValue['optional'].length; j++) {
                                                answer.answer += formValue['optional'][j] + ',';
                                            }
                                            answer.answer = answer.answer.substring(0, answer.answer.length - 1);
                                        } else {
                                            answer.answer = formValue['optional'];
                                        }
                                    }
                                    answers.push(answer);
                                }
                                var json = JSON.stringify(answers);
                                var store = fromItems[0].getValues();
                                //提交表单信息
                                Ext.Ajax.request({
                                    url: '/questionnaireManagement/submitAnswer',
                                    method: 'POST',
                                    params: {
                                        questionnaireID: store['questionnaireID'],
                                        data: json
                                    },
                                    success: function (resp) {
                                        var responseText = Ext.decode(resp.responseText);
                                        XD.msg(responseText.msg);
                                        if (responseText.msg == '提交成功') {
                                            view.close();
                                            answerFormView.close();
                                            answerFormView.questionnaireManagementGridView.getStore().reload();
                                        } else {
                                            return;
                                        }

                                    },
                                    failure: function () {
                                        XD.msg('提交失败！');
                                        return;
                                    }
                                });
                            }
                        },
                        failure: function () {

                        }
                    });
                }
            },
            'verificationCodeView button[itemId=verificationCodeClose]': {//验证码页面的关闭
                click: function (btn) {
                    var view = btn.up('verificationCodeView');
                    view.close();
                }
            },
            'questionnaireManagementGridView button[itemId=lookAnswer]': {
                click: function (btn) {
                    var view = btn.findParentByType('questionnaireManagementGridView');
                    var select = view.getSelectionModel().getSelection();
                    if (select.length != 1) {
                        XD.msg('请选择一条数据');
                        return;
                    }
                    window.cardobj = {grid: view};
                    var answerView = Ext.create('QuestionnaireManagement.view.AnswerFormView', {
                        title: '答卷信息',
                        // operate:'add',
                        questionnaireManagementGridView: view
                    });
                    var questionnaireID = select[0].get('questionnaireID');
                    var answerSheetID;
                    var flag=true;//作答标志
                    //先获取答卷信息
                    Ext.Ajax.request({
                        url:'/questionnaireManagement/getState',
                        method:'POST',
                        async: false,
                        params:{
                            questionnaireID: questionnaireID
                        },
                        success:function (resp) {
                            var responseText = Ext.decode(resp.responseText);
                            if(!responseText.success){
                                flag = false;
                            }else {
                                answerSheetID = responseText.data['answerSheetID'];
                            }
                        },
                        failure:function () {}
                    });
                    if(!flag){
                        XD.msg('该问卷未作答!');
                        return;
                    }

                    Ext.Ajax.request({
                        url: '/questionnaireManagement/findQuestions',
                        method: 'POST',
                        async: false,
                        params: {
                            questionnaireID: questionnaireID
                        },
                        success: function (resp) {
                            var responseText = Ext.decode(resp.responseText);
                            var formItems = answerView.items.items;
                            var items = [];
                            for (var i = 1; i <= responseText.length; i++) {
                                var questionID = responseText[i - 1].questionID;
                                var isnecessary = responseText[i - 1].isnecessary;
                                var necessary = isnecessary == '1' ? " (必答题)" : ""; //判断是否为必答题
                                var type = responseText[i - 1].type;//题型

                                var response;
                                Ext.Ajax.request({
                                    url:'/questionnaireManagement/getAnswer',
                                    method:'POST',
                                    async: false,
                                    params:{
                                        questionID:questionID,
                                        answerSheetID:answerSheetID
                                    },
                                    success:function (res) {
                                       response = Ext.decode(res.responseText);
                                    },
                                    failure:function () {}
                                });

                                items = [
                                    {
                                        xtype: 'form',
                                        modelValidation: true,
                                        layout: 'column',
                                        itemId: 'form' + i,
                                        margin: '10',
                                        minWidth: 870,
                                        bodyPadding: 10,
                                        items:[
                                            {
                                                columnWidth: 1,
                                                fieldLabel: '',
                                                name: 'questionID',
                                                itemId: 'questionID',
                                                value: questionID,
                                                hidden: true
                                            },
                                            {
                                                columnWidth: 1,
                                                xtype: 'displayfield',
                                                margin: '10 0 0 0',
                                                fieldLabel: '第' + i + '题',
                                                itemId: 'NO-' + i,
                                                name: 'NO',
                                                value: responseText[i - 1].content + necessary,
                                                style: {
                                                    'font-size': '16px'
                                                }
                                            },
                                            {
                                                columnWidth: 1,
                                                xtype: 'displayfield',
                                                fieldLabel: '答',
                                                minWidth: 870,
                                                margin: '20 0 0 0',
                                                width: '80%',
                                                name: 'optional',
                                                itemId: 'answer',
                                                value:response.data['answer']
                                            }
                                        ]
                                    }
                                ];
                                answerView.add(items);
                            }
                        },
                        failure: function () {
                            XD.msg('获取问卷信息失败!');
                            return;
                        }
                    });
                    answerView.down('[itemId=title]').setText(select[0].get('title'));
                    answerView.down('[itemId=submit]').hide();
                    answerView.show();
                }
            },
            'questionnaireManagementGridView button[itemId=cancelStick]':{
                click:function (btn) {
                    var select = btn.findParentByType('questionnaireManagementGridView').getSelectionModel();
                    var questionnaires = select.getSelection();
                    if (select.getCount() < 1) {
                        XD.msg('至少选择一条数据');
                        return;
                    }
                    var ids = [];
                    for(var i=0;i<questionnaires.length;i++){
                        ids.push(questionnaires[i].get('questionnaireID'));
                    }
                    Ext.Ajax.request({
                        params: {'ids':ids },
                        url: '/questionnaireManagement/cancelStick',
                        method: 'POST',
                        success: function (resp) {
                            var respText = Ext.decode(resp.responseText);
                            XD.msg(respText.msg);
                            btn.findParentByType('questionnaireManagementGridView').getStore().reload();
                        },
                        failure: function() {
                            XD.msg('操作失败');
                        }
                    });
                }
            }
        })
    },

    answerQuestionnaire:function(btn){
        var view = btn.findParentByType('questionnaireManagementGridView');
        var select = view.getSelectionModel().getSelection();
        var questionnaireID;
        var endtime;
        if (flag == 0) {
            questionnaireID = quesid;
        } else {
            if (select.length != 1) {
                XD.msg('请选择一条数据');
                return;
            }
            questionnaireID = select[0].get('questionnaireID');
            endtime = select[0].get('endtime');
            // if (operate == '填写问卷'){
                var stateText;
                var state = Ext.Ajax.request({
                    url: '/questionnaireManagement/getState',
                    method: 'POST',
                    async: false,
                    params: {
                        questionnaireID: questionnaireID
                    },
                    success: function (resp) {
                        var responseText = Ext.decode(resp.responseText);
                        stateText = responseText.msg;
                        return stateText;
                    },
                    failure: function () {
                        XD.msg("获取信息失败！");
                    }
                });
                if (stateText == '已答题') {
                    XD.msg("该问卷已作答！");
                    return;
                }
                if (Date.parse(endtime) < new Date()) {
                    XD.msg('该问卷调查已结束！');
                    return;
                }
            // }
        }

        window.cardobj = {grid: view};
        var answerView = Ext.create('QuestionnaireManagement.view.AnswerFormView', {
            title: '填写问卷',
            // operate:'add',
            questionnaireManagementGridView: view
        });

        Ext.Ajax.request({
            url: '/questionnaireManagement/findQuestions',
            method: 'POST',
            async: false,
            params: {
                questionnaireID: questionnaireID
            },
            success: function (resp) {
                var responseText = Ext.decode(resp.responseText);
                var formItems = answerView.items.items;
                var items = [];
                for (var i = 1; i <= responseText.length; i++) {
                    var questionID = responseText[i - 1].questionID;
                    var isnecessary = responseText[i - 1].isnecessary;
                    var necessary = isnecessary == '1' ? " (必答题)" : ""; //判断是否为必答题
                    var type = responseText[i - 1].type;//题型
                    if (type == '1') {//填空题
                        items = [
                            {
                                xtype: 'form',
                                modelValidation: true,
                                layout: 'column',
                                itemId: 'form' + i,
                                margin: '10',
                                minWidth: 870,
                                bodyPadding: 10,
                                items: [
                                    {
                                        columnWidth: 1,
                                        fieldLabel: '',
                                        name: 'questionID',
                                        itemId: 'questionID',
                                        value: questionID,
                                        hidden: true
                                    },
                                    {
                                        columnWidth: 1,
                                        fieldLabel: '',
                                        name: 'isNecessary',
                                        itemId: 'isNecessary',
                                        value: isnecessary,
                                        hidden: true
                                    },
                                    {
                                        columnWidth: 1,
                                        fieldLabel: '',
                                        name: 'type',
                                        itemId: 'type',
                                        value: type,
                                        hidden: true
                                    },
                                    {
                                        columnWidth: 1,
                                        xtype: 'displayfield',
                                        margin: '10 0 0 0',
                                        fieldLabel: '第' + i + '题',
                                        itemId: 'NO-' + i,
                                        name: 'NO',
                                        value: responseText[i - 1].content + necessary,
                                        style: {
                                            'font-size': '16px'
                                        }
                                    },
                                    {
                                        columnWidth: 1,
                                        xtype: 'textarea',
                                        fieldLabel: '答',
                                        minWidth: 870,
                                        margin: '20 0 0 0',
                                        width: '80%',
                                        name: 'optional',
                                        itemId: 'answer'
                                    }
                                ]
                            }
                        ];
                        answerView.add(items);
                    } else if (type == '2') {//单选题
                        var optional = responseText[i - 1].optional;
                        var optionals = [];
                        optionals = optional.split(",");
                        var optionItems = [];

                        items = [
                            {
                                xtype: 'form',
                                modelValidation: true,
                                layout: 'column',
                                itemId: 'form' + i,
                                margin: '10',
                                minWidth: 870,
                                bodyPadding: 10,
                                items: [
                                    {
                                        columnWidth: 1,
                                        fieldLabel: '',
                                        name: 'questionID',
                                        itemId: 'questionID',
                                        value: questionID,
                                        hidden: true
                                    },
                                    {
                                        columnWidth: 1,
                                        fieldLabel: '',
                                        name: 'isNecessary',
                                        itemId: 'isNecessary',
                                        value: isnecessary,
                                        hidden: true
                                    },
                                    {
                                        columnWidth: 1,
                                        fieldLabel: '',
                                        name: 'type',
                                        itemId: 'type',
                                        value: type,
                                        hidden: true
                                    },
                                    {
                                        columnWidth: 1,
                                        xtype: 'displayfield',
                                        margin: '10 0 0 0',
                                        fieldLabel: '第' + i + '题',
                                        itemId: 'NO-' + i,
                                        name: 'NO',
                                        value: responseText[i - 1].content + necessary,
                                        style: {
                                            // 'text-align':'center',
                                            'font-size': '16px'
                                        }
                                    },
                                    {
                                        columnWidth: 1,
                                        xtype: 'form',
                                        minWidth: 870,
                                        width: '100%',
                                        layout: 'column',
                                        labelAlign: "right",
                                        itemId: 'answer',
                                        items: []
                                    }
                                ]
                            }
                        ];
                        answerView.add(items);
                        var form = answerView.down('[itemId=form' + i + ']');
                        for (var j = 1; j <= optionals.length; j++) {
                            if (optionals[j - 1] == '其他') {
                                var other = [{
                                    columnWidth: 0.2,
                                    fieldLabel: "选项" + j,
                                    xtype: "radio",
                                    name: "optional",
                                    inputValue: "其他",
                                    boxLabel: "其他"
                                }, {
                                    columnWidth: 0.7,
                                    xtype: "textfield",
                                    style: 'width: 90%',
                                    name: 'other'
                                }];
                                form.add(other);
                            } else {
                                var optionalItem = [
                                    {
                                        columnWidth: 1,
                                        xtype: "radio",
                                        fieldLabel: "选项" + j,
                                        name: "optional",
                                        inputValue: optionals[j - 1],
                                        boxLabel: optionals[j - 1]
                                    }
                                ];
                                form.add(optionalItem);
                            }
                        }
                    } else if (type == '3') {//多选题
                        var optional = responseText[i - 1].optional;
                        var optionals = [];
                        optionals = optional.split(",");
                        items = [
                            {
                                xtype: 'form',
                                modelValidation: true,
                                layout: 'column',
                                itemId: 'form' + i,
                                margin: '10',
                                minWidth: 870,
                                bodyPadding: 10,
                                items: [
                                    {
                                        columnWidth: 1,
                                        fieldLabel: '',
                                        name: 'questionID',
                                        itemId: 'questionID',
                                        value: questionID,
                                        hidden: true
                                    },
                                    {
                                        columnWidth: 1,
                                        fieldLabel: '',
                                        name: 'isNecessary',
                                        itemId: 'isNecessary',
                                        value: isnecessary,
                                        hidden: true
                                    },
                                    {
                                        columnWidth: 1,
                                        fieldLabel: '',
                                        name: 'type',
                                        itemId: 'type',
                                        value: type,
                                        hidden: true
                                    },
                                    {
                                        columnWidth: 1,
                                        xtype: 'displayfield',
                                        margin: '10 0 0 0',
                                        fieldLabel: '第' + i + '题',
                                        itemId: 'NO-' + i,
                                        name: 'NO',
                                        value: responseText[i - 1].content + necessary,
                                        style: {
                                            // 'text-align':'center',
                                            'font-size': '16px'
                                        }
                                    },
                                    {
                                        columnWidth: 1,
                                        xtype: 'form',
                                        minWidth: 870,
                                        width: '100%',
                                        layout: 'column',
                                        labelAlign: "right",
                                        itemId: 'answer',
                                        items: []
                                    }
                                ]
                            }
                        ];
                        answerView.add(items);
                        var form = answerView.down('[itemId=form' + i + ']');
                        for (var j = 1; j <= optionals.length; j++) {
                            if (optionals[j - 1] == '其他') {
                                var other = [
                                    {
                                        columnWidth: 0.2,
                                        fieldLabel: "选项" + j,
                                        xtype: "checkbox",
                                        name: "optional",
                                        inputValue: "其他",
                                        boxLabel: "其他"
                                    }, {
                                        columnWidth: 0.7,
                                        xtype: "textfield",
                                        name: 'other'
                                    }
                                ];
                                form.add(other);
                            } else {
                                var optionalItem = [{
                                    columnWidth: 1,
                                    xtype: "checkbox",
                                    fieldLabel: "选项" + j,
                                    name: "optional",
                                    inputValue: optionals[j - 1],
                                    boxLabel: optionals[j - 1]
                                }];
                                form.add(optionalItem);
                            }
                        }
                    }
                }
            },
            failure: function () {
                XD.msg('获取问卷信息失败!');
                return;
            }
        });
        if (flag == 0) {
            view.close();
            answerView.down('[itemId=title]').setText(title);
        } else {
            answerView.down('[itemId=title]').setText(select[0].get('title'));
        }
        answerView.down('[itemId=questionnaireID]').setValue(questionnaireID);
        answerView.show();
    },

    lookQuestionnaires: function (btn) {
        var view = btn.findParentByType('questionnaireManagementGridView');
        var select = view.getSelectionModel().getSelection();
        if (select.length != 1) {
            XD.msg('请选择一条数据');
            return;
        }
        window.cardobj = {grid: view};
        var lookView= Ext.create('QuestionnaireManagement.view.QuestionnaireLookFormView', {
            title: '查看问卷',
            operate: 'show',
            questionnaireManagementGridView: view
        });

        var questionnaireID = select[0].get('questionnaireID');

        Ext.Ajax.request({
            url: '/questionnaireManagement/findQuestions',
            method: 'POST',
            async: false,
            params: {
                questionnaireID: questionnaireID
            },
            success: function (resp) {
                var responseText = Ext.decode(resp.responseText);
                var items = [];
                var optionals = [];
                for (var i = 1; i <= responseText.length; i++) {
                    var questionID = responseText[i - 1].questionID;
                    var isnecessary = responseText[i - 1].isnecessary;
                    var necessary = isnecessary == '1' ? " (必答题)" : ""; //判断是否为必答题
                    var optional = responseText[i - 1].optional;//选项
                    if (optional !== undefined) {
                        optional = optional.split(',');
                    }
                    var type = responseText[i - 1].type;//题型
                    if (type == '1') {//填空题
                        items = [
                            {
                                xtype: 'form',
                                modelValidation: true,
                                layout: 'column',
                                itemId: 'form' + i,
                                margin: '10',
                                minWidth: 870,
                                bodyPadding: 10,
                                items: [
                                    {
                                        columnWidth: 1,
                                        fieldLabel: '',
                                        name: 'questionID',
                                        itemId: 'questionID',
                                        value: questionID,
                                        hidden: true
                                    },
                                    {
                                        columnWidth: 1,
                                        fieldLabel: '',
                                        name: 'isNecessary',
                                        itemId: 'isNecessary',
                                        value: isnecessary,
                                        hidden: true
                                    },
                                    {
                                        columnWidth: 1,
                                        fieldLabel: '',
                                        name: 'type',
                                        itemId: 'type',
                                        value: type,
                                        hidden: true
                                    },
                                    {
                                        columnWidth: 1,
                                        xtype: 'displayfield',
                                        margin: '10 0 0 0',
                                        fieldLabel: '第' + i + '题',
                                        itemId: 'NO-' + i,
                                        name: 'NO',
                                        value: responseText[i - 1].content + necessary,
                                        style: {
                                            'font-size': '16px'
                                        }
                                    },
                                    {
                                        columnWidth: 1,
                                        xtype: 'textarea',
                                        fieldLabel: '答',
                                        readOnly:true,
                                        minWidth: 870,
                                        margin: '20 0 0 0',
                                        width: '80%',
                                        name: 'optional',
                                        itemId: 'answer'
                                    }
                                ]
                            }
                        ];
                        lookView.add(items);
                    } else if (type == '2') {//单选题
                        var optional = responseText[i - 1].optional;
                        var optionals = [];
                        optionals = optional.split(",");
                        var optionItems = [];

                        items = [
                            {
                                xtype: 'form',
                                modelValidation: true,
                                layout: 'column',
                                itemId: 'form' + i,
                                margin: '10',
                                minWidth: 870,
                                bodyPadding: 10,
                                items: [
                                    {
                                        columnWidth: 1,
                                        fieldLabel: '',
                                        name: 'questionID',
                                        itemId: 'questionID',
                                        value: questionID,
                                        hidden: true
                                    },
                                    {
                                        columnWidth: 1,
                                        fieldLabel: '',
                                        name: 'isNecessary',
                                        itemId: 'isNecessary',
                                        value: isnecessary,
                                        hidden: true
                                    },
                                    {
                                        columnWidth: 1,
                                        fieldLabel: '',
                                        name: 'type',
                                        itemId: 'type',
                                        value: type,
                                        hidden: true
                                    },
                                    {
                                        columnWidth: 1,
                                        xtype: 'displayfield',
                                        margin: '10 0 0 0',
                                        fieldLabel: '第' + i + '题',
                                        itemId: 'NO-' + i,
                                        name: 'NO',
                                        value: responseText[i - 1].content + necessary,
                                        style: {
                                            // 'text-align':'center',
                                            'font-size': '16px'
                                        }
                                    },
                                    {
                                        columnWidth: 1,
                                        xtype: 'form',
                                        minWidth: 870,
                                        width: '100%',
                                        layout: 'column',
                                        labelAlign: "right",
                                        itemId: 'answer',
                                        items: []
                                    }
                                ]
                            }
                        ];
                        lookView.add(items);
                        var form = lookView.down('[itemId=form' + i + ']');
                        for (var j = 1; j <= optionals.length; j++) {
                            if (optionals[j - 1] == '其他') {
                                var other = [{
                                    columnWidth: 0.2,
                                    fieldLabel: "选项" + j,
                                    xtype: "radio",
                                    name: "optional",
                                    inputValue: "其他",
                                    readOnly:true,
                                    boxLabel: "其他"
                                }, {
                                    columnWidth: 0.7,
                                    xtype: "textfield",
                                    style: 'width: 90%',
                                    name: 'other'
                                }];
                                form.add(other);
                            } else {
                                var optionalItem = [
                                    {
                                        columnWidth: 1,
                                        xtype: "radio",
                                        fieldLabel: "选项" + j,
                                        name: "optional",
                                        readOnly:true,
                                        inputValue: optionals[j - 1],
                                        boxLabel: optionals[j - 1]
                                    }
                                ];
                                form.add(optionalItem);
                            }
                        }
                    } else if (type == '3') {//多选题
                        var optional = responseText[i - 1].optional;
                        var optionals = [];
                        optionals = optional.split(",");
                        items = [
                            {
                                xtype: 'form',
                                modelValidation: true,
                                layout: 'column',
                                itemId: 'form' + i,
                                margin: '10',
                                minWidth: 870,
                                bodyPadding: 10,
                                items: [
                                    {
                                        columnWidth: 1,
                                        fieldLabel: '',
                                        name: 'questionID',
                                        itemId: 'questionID',
                                        value: questionID,
                                        hidden: true
                                    },
                                    {
                                        columnWidth: 1,
                                        fieldLabel: '',
                                        name: 'isNecessary',
                                        itemId: 'isNecessary',
                                        value: isnecessary,
                                        hidden: true
                                    },
                                    {
                                        columnWidth: 1,
                                        fieldLabel: '',
                                        name: 'type',
                                        itemId: 'type',
                                        value: type,
                                        hidden: true
                                    },
                                    {
                                        columnWidth: 1,
                                        xtype: 'displayfield',
                                        margin: '10 0 0 0',
                                        fieldLabel: '第' + i + '题',
                                        itemId: 'NO-' + i,
                                        name: 'NO',
                                        value: responseText[i - 1].content + necessary,
                                        style: {
                                            // 'text-align':'center',
                                            'font-size': '16px'
                                        }
                                    },
                                    {
                                        columnWidth: 1,
                                        xtype: 'form',
                                        minWidth: 870,
                                        width: '100%',
                                        layout: 'column',
                                        labelAlign: "right",
                                        itemId: 'answer',
                                        items: []
                                    }
                                ]
                            }
                        ];
                        lookView.add(items);
                        var form = lookView.down('[itemId=form' + i + ']');
                        for (var j = 1; j <= optionals.length; j++) {
                            if (optionals[j - 1] == '其他') {
                                var other = [
                                    {
                                        columnWidth: 0.2,
                                        fieldLabel: "选项" + j,
                                        xtype: "checkbox",
                                        readOnly:true,
                                        name: "optional",
                                        inputValue: "其他",
                                        boxLabel: "其他"
                                    }, {
                                        columnWidth: 0.7,
                                        xtype: "textfield",
                                        name: 'other'
                                    }
                                ];
                                form.add(other);
                            } else {
                                var optionalItem = [{
                                    columnWidth: 1,
                                    xtype: "checkbox",
                                    readOnly:true,
                                    fieldLabel: "选项" + j,
                                    name: "optional",
                                    inputValue: optionals[j - 1],
                                    boxLabel: optionals[j - 1]
                                }];
                                form.add(optionalItem);
                            }
                        }
                    }
                }
            },
            failure: function () {
                XD.msg('获取问卷信息失败!');
                return;
            }
        });
        //获取调研份数
        Ext.Ajax.request({
            url:'/questionnaireManagement/getCountByquestionnaireID',
            method:'POST',
            async: false,
            params:{
                questionnaireID: questionnaireID
            },
            success:function (resp) {
                var responseText = Ext.decode(resp.responseText);
                lookView.down('[itemId=ancount]').setText("调研份数："+responseText+" 份");
            },
            failure:function () {}
        });
        lookView.down('[itemId=title]').setText(select[0].get('title'));
        lookView.show();
    },

    //查看问卷信息
    showQuestionnaire: function (btn, operate) {
        var view = btn.findParentByType('questionnaireManagementGridView');
        var select = view.getSelectionModel().getSelection();
        if (select.length != 1) {
            XD.msg('请选择一条数据');
            return;
        }
        window.cardobj = {grid: view};
        var editView;
        if (operate == 'edit') {//修改页面
            editView = Ext.create('QuestionnaireManagement.view.QuestionnaireAddFormView', {
                title: '修改问卷',
                operate: 'edit',
                questionnaireManagementGridView: view
            });
        } else {//查看页面
            editView = Ext.create('QuestionnaireManagement.view.QuestionnaireAddFormView', {
                title: '查看问卷',
                operate: 'show',
                questionnaireManagementGridView: view
            });
        }
        //将数据传送到页面
        var questionnaireAddForm = editView.down('[itemId=questionnaireAddForm]');
        var questionnaireID = questionnaireAddForm.getForm().findField("questionnaireID");
        questionnaireID.setValue(select[0].get('questionnaireID'));
        var title = questionnaireAddForm.getForm().findField("title");
        title.setValue(select[0].get('title'));
        var createtime = questionnaireAddForm.getForm().findField("createtime");
        createtime.setValue(select[0].get('createtime'));
        var starttime = questionnaireAddForm.getForm().findField("starttime");
        starttime.setValue(select[0].get('starttime'));
        var endtime = questionnaireAddForm.getForm().findField("endtime");
        endtime.setValue(select[0].get('endtime'));
        var publishstate = questionnaireAddForm.getForm().findField("publishstate");
        publishstate.setValue(select[0].get('publishstate'));
        var stick = questionnaireAddForm.getForm().findField("stick");
        stick.setValue(select[0].get('stick'));

        Ext.Ajax.request({
            url: '/questionnaireManagement/findQuestions',
            method: 'POST',
            async: false,
            params: {
                questionnaireID: select[0].get('questionnaireID')
            },
            success: function (resp) {
                var responseText = Ext.decode(resp.responseText);
                var questionAddBtn = editView.down('[itemId=questionAdd]');
                clickCount = 0;
                for (var i = 0; i < responseText.length; i++) {
                    questionAddBtn.click();
                    var formItems = editView.items.items;
                    var type = responseText[i]['type'];
                    formItems[i + 1].getForm().findField("type").setValue(type);
                    formItems[i + 1].getForm().findField("isNecessary").setValue(responseText[i]['isnecessary']);
                    formItems[i + 1].getForm().findField("content").setValue(responseText[i]['content']);
                    if (type != "1") {//当题型是选项题时
                        var optionals = [];
                        optionals = responseText[i]['optional'].split(",");
                        var addBtn = formItems[i + 1].down("[itemId=add]");
                        if (optionals.length > 2) {//初始选择只有两个，当该选择题的选项数超过2个时要添加选项
                            for (var k = 3; k <= optionals.length; k++) {
                                addBtn.click();
                            }
                        }
                        for (var j = 0; j < optionals.length; j++) {
                            formItems[i + 1].down("[itemId=optionContent-" + (j + 1) + "]").setValue(optionals[j]);
                            if (operate != 'edit') {
                                // formItems[i+1].getForm().findField("edit").hide();
                                formItems[i + 1].down("[itemId=option-" + (j + 1) + "]").setReadOnly(true);
                                formItems[i + 1].down("[itemId=optionContent-" + (j + 1) + "]").setReadOnly(true);
                            }
                        }
                    }
                    if (operate != 'edit') {//非编辑页面时设置控件为只读
                        editView.down('[itemId=questionDel]').hide();
                        editView.down('[itemId=questionAdd]').hide();
                        editView.down('[itemId=questionnaireAddSubmit]').hide();
                        title.setReadOnly(true);
                        createtime.setReadOnly(true);
                        starttime.setReadOnly(true);
                        endtime.setReadOnly(true);
                        publishstate.setReadOnly(true);
                        stick.setReadOnly(true);
                        formItems[i + 1].getForm().findField("type").setReadOnly(true);
                        formItems[i + 1].getForm().findField("isNecessary").setReadOnly(true);
                        formItems[i + 1].getForm().findField("content").setReadOnly(true);
                        formItems[i + 1].down("[itemId=checkboxgroup-" + (i + 1) + "]").setReadOnly(true);
                        formItems[i + 1].down("[itemId=add]").disable(true);
                        formItems[i + 1].down("[itemId=del]").disable(true);
                        formItems[i + 1].down("[itemId=addSelf]").disable(true);
                    }
                }

            },
            failure: function (resp) {
                XD.msg('操作失败!');
            }
        });
        editView.show();
    },

    //发布问卷
    updatePublishQuestionnaire: function (btn, state) {
        var view = btn.findParentByType('questionnaireManagementGridView');
        var select = view.getSelectionModel();
        if (select.getCount() < 1) {
            XD.msg('请选择要操作的数据');
            return;
        }
        var store = [];
        store = select.selected.items;
        var questionnaireIDs = [];
        for (var i = 0; i < store.length; i++) {
            questionnaireIDs.push(store[i].get('questionnaireID'));
        }
        Ext.Ajax.request({
            url: '/questionnaireManagement/updatePublishQuestionnaire',
            method: 'POST',
            params: {
                questionnaireIDs: questionnaireIDs,
                state: state
            },
            success: function (resp) {
                var responseText = Ext.decode(resp.responseText);
                if (responseText.success == true) {
                    XD.msg(responseText.msg);
                    view.delReload(select.getCount());
                }
            },
            failure: function () {
                XD.msg('操作失败！');
            }
        });
    },

    //删除问卷
    delQuestionn: function (btn) {
        var view = btn.findParentByType('questionnaireManagementGridView');
        var select = view.getSelectionModel();
        if (select.getCount() < 1) {
            XD.msg('请选择要删除的问卷');
            return;
        } else {
            XD.confirm('该问卷下的答卷也会全部删除，确定要删除问卷吗？', function () {
                var store = [];
                store = select.selected.items;
                var questionnaireIDs = [];
                for (var i = 0; i < store.length; i++) {
                    questionnaireIDs.push(store[i].get('questionnaireID'));
                }

                Ext.Ajax.request({
                    url: '/questionnaireManagement/delQuestionnaire',
                    method: 'POST',
                    params: {
                        questionnaireIDs: questionnaireIDs
                    },
                    success: function (resp) {
                        var responseText = Ext.decode(resp.responseText);
                        if (responseText.success == true) {
                            XD.msg(responseText.msg);
                            view.delReload(select.getCount());
                        }
                    },
                    failure: function () {
                        XD.msg('操作失败！');
                    }
                });
            });
        }

    },

    //删除问题
    delQuestionnaire: function (btn) {
        var window = btn.up('window');
        // var form = btn.up('form');
        var beforeForm = [];
        beforeForm = window.items.items;
        var delForm = [];
        var notDelForm = [];
        // //先找到已勾选删除的问题
        for (var i = beforeForm.length; i > 1; i--) {
            var itemId = '[itemId=checkboxgroup-' + (i - 1) + ']';
            var value = window.down(itemId).checked;
            if (value) {
                delForm.push(beforeForm[i - 1]);
                // window.remove(delForm[delForm.length-1]);
            } else {
                notDelForm.push(beforeForm[i - 1]);
            }
        }

        if (delForm.length == 0) {
            XD.msg("至少选择一个问题删除！");
            return;
        }

        for (var i = 0; i < delForm.length; i++) {//
            if (beforeForm[beforeForm.length - 1 - i].itemId == 'form1') {
                XD.msg('每份问卷至少保留一个问题！');
                return;
            }
        }

        //获取到不用删除的问题的数据，之后将数据往前挪，再删除后面的问题
        for (var i = 1; i <= notDelForm.length; i++) {
            beforeForm[notDelForm.length - i + 1].getForm().findField("NO").reset();
            var name = notDelForm[i - 1].getForm().findField("type").getValue();
            name == null ? beforeForm[notDelForm.length - i + 1].getForm().findField("type").reset() :
                beforeForm[notDelForm.length - i + 1].getForm().findField("type").setValue(name);
            var isNecessary = notDelForm[i - 1].getForm().findField("isNecessary").getValue();
            isNecessary == null ? beforeForm[notDelForm.length - i + 1].getForm().findField("isNecessary").reset() :
                beforeForm[notDelForm.length - i + 1].getForm().findField("isNecessary").setValue(isNecessary);
            var content = notDelForm[i - 1].getForm().findField("content").getValue();
            content == null ? beforeForm[notDelForm.length - i + 1].getForm().findField("content").reset() :
                beforeForm[notDelForm.length - i + 1].getForm().findField("content").setValue(content);
            if (name == "2" || name == "3") {
                beforeForm[notDelForm.length - i + 1].getForm().findField("edit").show();
                beforeForm[notDelForm.length - i + 1].down("[itemId=add]").show();
                beforeForm[notDelForm.length - i + 1].down("[itemId=del]").show();
                var newIndex = (notDelForm[i - 1].items.items.length - 8) / 2;//新选项数
                var oldIndex = (beforeForm[notDelForm.length - i + 1].items.items.length - 8) / 2;//原本的选项数
                if (oldIndex < newIndex) {
                    for (var k = newIndex - oldIndex; k > 0; k--) {
                        beforeForm[notDelForm.length - i + 1].down("[itemId=add]").click();
                    }
                } else if (oldIndex > newIndex) {
                    for (var k = oldIndex; k > newIndex; k--) {
                        var optionContentId = beforeForm[notDelForm.length - i + 1].down("[itemId=optionContent-" + k + "]");
                        beforeForm[notDelForm.length - i + 1].remove(optionContentId);
                        var optionId = beforeForm[notDelForm.length - i + 1].down("[itemId=option-" + k + "]");
                        beforeForm[notDelForm.length - i + 1].remove(optionId);
                    }
                }
                for (var j = 1; j < newIndex; j++) {
                    var option = notDelForm[i - 1].down("[itemId=optionContent-" + j + "]").getValue();
                    option == null ? beforeForm[notDelForm.length - i + 1].down("[itemId=optionContent-" + j + "]").reset() :
                        beforeForm[notDelForm.length - i + 1].down("[itemId=optionContent-" + j + "]").setValue(option);
                }
            }
        }
        for (var i = 0; i < delForm.length; i++) {//每次删除最后一个
            window.remove(beforeForm[beforeForm.length - 1]);
            clickCount--;
        }
    },

    //增加问题
    addQuestionnaire: function (btn, type) {
        var window = btn.up('window');
        // var form = window.down('[itemId=questionnaireAddForm]');
        clickCount += 1;
        var option = 2;//选项号数
        var items = [
            {
                xtype: 'form',
                modelValidation: true,
                layout: 'column',
                itemId: 'form' + clickCount,
                // scrollable:true,//可滚动
                bodyPadding: 10,
                items: [
                    {
                        columnWidth: 1,
                        xtype: 'checkbox',
                        margin: '10 0 0 0',
                        boxLabel: '第' + clickCount + '题',
                        itemId: 'checkboxgroup-' + clickCount,
                        name: 'NO',
                        items: [{
                            name: 'number',
                            inputValue: clickCount
                        }]
                    },
                    {
                        columnWidth: 0.5,
                        xtype: 'combobox',
                        fieldLabel: '题型',
                        margin: '10 0 0 0',
                        name: 'type',
                        store: typeStore,
                        editable: false,
                        allowBlank: false,
                        displayField: 'Name',
                        valueField: 'Value',
                        queryMode: 'local',
                        afterLabelTextTpl: ['<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'],
                        listeners: {
                            change: function (combo) {
                                var value = combo.getValue();
                                var form = combo.up('form');
                                if (("2" == value) || ("3" == value)) {
                                    for (var i = 0; i < form.items.items.length; i++) {
                                        form.items.items[i].show();
                                    }
                                } else {
                                    for (var i = 4; i < form.items.items.length; i++) {
                                        form.items.items[i].hide();
                                    }
                                }
                            }
                        }
                    }, {
                        columnWidth: 0.5,
                        xtype: 'combobox',
                        fieldLabel: '是否必答',
                        margin: '10 0 0 20',
                        name: 'isNecessary',
                        store: isNecessaryStore,
                        editable: false,
                        allowBlank: false,
                        displayField: 'Name',
                        valueField: 'Value',
                        queryMode: 'local',
                        afterLabelTextTpl: ['<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>']
                    }, {
                        columnWidth: 1,
                        fieldLabel: '问题描述',
                        name: 'content',
                        margin: '10 0 0 0',
                        allowBlank: false,
                        afterLabelTextTpl: ['<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'],
                    }, {
                        columnWidth: 0.1,
                        xtype: 'checkboxgroup',
                        margin: '10 0 0 100',
                        name: 'edit',
                        fieldLabel: '选项编辑',
                        hidden: true
                    }, {
                        columnWidth: 0.1,
                        xtype: 'button',
                        text: '增加选项',
                        itemId: 'add',
                        margin: '10 0 0 0',
                        hidden: true,
                        listeners: {
                            click: function (btn) {
                                option += 1;
                                var form = btn.up('form');
                                var optionItems = [
                                    {
                                        columnWidth: 0.9,
                                        xtype: 'checkbox',
                                        boxLabel: '选项' + option,
                                        itemId: 'option-' + option,
                                        margin: '10 0 0 100',
                                        items: [{
                                            name: 'option',
                                            inputValue: option
                                        }]
                                    }, {
                                        columnWidth: 0.75,
                                        fieldLabel: '选项内容',
                                        name: 'optionContent',
                                        itemId: 'optionContent-' + option,
                                        margin: '0 0 5 100',
                                        allowBlank: false,
                                        afterLabelTextTpl: ['<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>']
                                    }
                                ];
                                form.add(optionItems);
                            }
                        }
                    }, {
                        columnWidth: 0.05,
                        xtype: 'tbtext',
                        margin: '10 0 0 0',
                        text: ' '
                    }, {
                        columnWidth: 0.1,
                        xtype: 'button',
                        text: '删除选项',
                        itemId: 'del',
                        margin: '10 0 0 0',
                        hidden: true,
                        listeners: {
                            click: function (btn) {
                                var form = btn.up('form');
                                var delItems = [];
                                var notDelItems = [];
                                //先确定哪些选项被勾选删除
                                for (i = option; i > 0; i--) {
                                    var optionId = '[itemId=option-' + (i) + ']';
                                    var checked = form.down(optionId);
                                    var value = form.down(optionId).checked;
                                    var delContent;
                                    var notDelContent;
                                    if (value) {
                                        delItems.push(checked);//删除的选项
                                        delContent = form.down('[itemId=optionContent-' + (i) + ']');//删除的选项内容
                                        delItems.push(delContent);
                                    } else {
                                        // notDelItems.push(checked);//不删除的选项
                                        notDelContent = form.down('[itemId=optionContent-' + (i) + ']').getValue();
                                        notDelItems.push(notDelContent);
                                    }
                                }

                                if (delItems.length == 0) {
                                    XD.msg("至少选择一个选项删除！");
                                    return;
                                }

                                var formItems = [];//未删除前的所有控件
                                for (var i = 0; i < form.items.items.length; i++) {
                                    formItems.push(form.items.items[i]);
                                }

                                for (var i = 1; i <= delItems.length; i++) {
                                    if (formItems[formItems.length - i].itemId == "optionContent-1" || formItems[formItems.length - i].itemId == "optionContent-2" ||
                                        formItems[formItems.length - i].itemId == "option-1" || formItems[formItems.length - i].itemId == "option-2") {
                                        XD.msg("选择题至少保留两个选项！");
                                        return;
                                    }
                                }

                                for (var i = 1; i <= notDelItems.length; i++) {
                                    form.down("[itemId=option-" + (i) + "]").reset();
                                    var optionContent = notDelItems[notDelItems.length - i];//找到不删除的内容
                                    optionContent == null ? form.down("[itemId=optionContent-" + (i) + "]").reset() :
                                        form.down("[itemId=optionContent-" + (i) + "]").setValue(optionContent);
                                }

                                for (var i = 1; i <= delItems.length; i++) {
                                    form.remove(formItems[formItems.length - i]);
                                }

                                option = notDelItems.length;
                            }
                        }
                    }, {
                        columnWidth: 0.05,
                        xtype: 'tbtext',
                        margin: '10 0 0 0',
                        text: ' '
                    }, {
                        columnWidth: 0.1,
                        xtype: 'button',
                        text: '增加自填项',
                        itemId: 'addSelf',
                        margin: '10 0 0 0',
                        hidden: true,
                        listeners: {
                            click: function (btn) {

                                option += 1;
                                var form = btn.up('form');
                                var optionItems = [
                                    {
                                        columnWidth: 0.9,
                                        xtype: 'checkbox',
                                        boxLabel: '选项' + option,
                                        itemId: 'option-' + option,
                                        margin: '10 0 0 100',
                                        items: [{
                                            name: 'option',
                                            inputValue: option
                                        }]
                                    }, {
                                        columnWidth: 0.75,
                                        fieldLabel: '选项文本',
                                        name: 'optionContent',
                                        itemId: 'optionContent-' + option,
                                        margin: '0 0 5 100',
                                        value: '其他',
                                        readOnly: true,
                                        allowBlank: false
                                    }
                                ];
                                form.add(optionItems);
                            }
                        }
                    }, {
                        columnWidth: 0.9,
                        xtype: 'checkbox',
                        hidden: true,
                        boxLabel: '选项' + (option - 1),
                        itemId: 'option-' + (option - 1),//
                        margin: '10 0 0 100',
                        items: [{
                            name: 'option',
                            inputValue: (option - 1)
                        }]
                    }, {
                        columnWidth: 0.75,
                        fieldLabel: '选项内容',
                        hidden: true,
                        name: 'optionContent',
                        itemId: 'optionContent-' + (option - 1),
                        margin: '0 0 5 100',
                        allowBlank: false,
                        afterLabelTextTpl: ['<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>']
                    }, {
                        columnWidth: 0.9,
                        xtype: 'checkbox',
                        hidden: true,
                        boxLabel: '选项' + option,
                        itemId: 'option-' + option,
                        margin: '10 0 0 100',
                        items: [{
                            name: 'option',
                            inputValue: option
                        }]
                    }, {
                        columnWidth: 0.75,
                        fieldLabel: '选项内容',
                        hidden: true,
                        name: 'optionContent',
                        itemId: 'optionContent-' + (option),
                        margin: '0 0 5 100',
                        allowBlank: false,
                        afterLabelTextTpl: ['<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>']
                    }
                ]
            }
        ];
        window.add(items);
    },

    //增加问卷的提交
    submitQuestionnaire: function (btn) {
        var window = btn.up('window');
        var form = window.down('[itemId=questionnaireAddForm]');
        var questionnaireData = form.getValues();
        if (questionnaireData['title'] == '' || questionnaireData['createtime'] == '') {
            XD.msg('有必填项未填写');
            return;
        }
        if (questionnaireData['starttime'] > questionnaireData['endtime']) {
            XD.msg('开始时间不能大于结束时间！');
            return;
        }
        var sort = 1;
        var data = [];
        var forms = [];
        forms = window.items.items;
        for (var i = 1; i < forms.length; i++) {
            var eachForm = forms[i];
            var questionData = eachForm.getValues();
            var type = questionData['type'];
            var isNecessary = questionData['isNecessary'];
            var content = questionData['content'];
            var optionContents = [];
            if (type == '' || isNecessary == '' || content == '') {
                XD.msg('有必填项未填写');
                return;
            } else if (questionData['type'] == "2" || questionData['type'] == "3") {
                var formItem = eachForm.items.items;
                var index = parseInt(formItem[formItem.length - 1].getItemId().substring(14));
                for (var j = 1; j <= index; j++) {
                    var optionContent = eachForm.down("[itemId=optionContent-" + j + "]").getValue();
                    if (optionContent == '' || optionContent == null) {
                        XD.msg('有必填项未填写');
                        return;
                    } else {
                        optionContents.push(optionContent);
                    }
                }
            }

            var question = new Object();
            question.type = type;
            question.isNecessary = isNecessary;
            question.content = content;
            // question.sort = sort;
            question.optionContents = optionContents;

            data.push(question);
            sort++;
        }
        var json = JSON.stringify(data);
        var count = sort - 1;
        Ext.Ajax.request({
            url: '/questionnaireManagement/addQuestionnaires',
            method: 'POST',
            sync: true,
            params: {
                questionnaireID: questionnaireData['questionnaireID'],
                createtime: questionnaireData['createtime'],
                endtime: questionnaireData['endtime'],
                publishstate: questionnaireData['publishstate'],
                starttime: questionnaireData['starttime'],
                stick: questionnaireData['stick'],
                title: questionnaireData['title'],
                count: count,
                data: json
            },
            success: function (resp) {
                var responseText = Ext.decode(resp.responseText);
                if (responseText.success) {
                    XD.msg(responseText.msg);
                }
                if ("操作成功" == responseText.msg) {
                    var questionnaireManagementGridView = window.questionnaireManagementGridView;
                    questionnaireManagementGridView.getStore().reload();
                    window.close();
                }
            },
            failure: function () {
                XD.msg('操作失败!');
            }
        });
    },

    //问卷页面的关闭
    questionnaireAddClose: function (btn) {
        var view = btn.up('questionnaireAddFormView');
        view.close();
    }

});