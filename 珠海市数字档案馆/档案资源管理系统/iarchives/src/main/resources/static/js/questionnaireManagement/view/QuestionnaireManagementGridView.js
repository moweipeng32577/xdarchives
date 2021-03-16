Ext.define('QuestionnaireManagement.view.QuestionnaireManagementGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'questionnaireManagementGridView',
    itemId: 'questionnaireManagementGridViewID',
    bodyBorder: false,
    store: 'QuestionnaireManagementGridStore',
    hasCloseButton: false,
    head: false,
    searchstore: [
        {item: "title", name: "标题"},
        {item: "createtime", name: "创建时间"},
        {item: "starttime", name: "开始时间"},
        {item: "endtime", name: "结束时间"},
    ],
    tbar: [{
        itemId: 'lookAnswer',
        xtype: 'button',
        iconCls: 'fa fa-eye',
        text: '查看问卷'
    }, '-', {
        itemId: 'answerQuestionnaire',
        xtype: 'button',
        iconCls: 'fa fa-pencil-square-o',
        text: '开始调研'
    }, '-', {
        itemId: 'questionnaireAdd',
        xtype: 'button',
        iconCls: 'fa fa-plus-circle',
        text: '增加'
    }, '-', {
        itemId: 'questionnaireEdit',
        xtype: 'button',
        iconCls: 'fa fa-pencil-square-o',
        text: '修改'
    }, '-', {
        itemId: 'questionnaireDel',
        xtype: 'button',
        iconCls: 'fa fa-trash-o',
        text: '删除'
    }, '-', {
        itemId: 'questionnaireLook',
        xtype: 'button',
        iconCls: 'fa fa-eye',
        text: '查看'
    }, '-', {
        itemId: 'publishBtnID',
        xtype: 'button',
        iconCls: 'fa fa-check-square',
        text: '发布'
    }, '-', {
        itemId: 'canclePublishBtnID',
        xtype: 'button',
        iconCls: 'fa fa-minus-square',
        text: '取消发布'
    }, '-', {
        itemId: 'questionnaireStick',
        xtype: 'button',
        iconCls: 'fa fa-upload',
        text: '置顶'
    }, '-', {
        itemId: 'cancelStick',
        xtype: 'button',
        iconCls: 'fa fa-download',
        text: '取消置顶'
    }, '-', {
        itemId: 'statistics',
        xtype: 'button',
        iconCls: 'fa fa-table',
        text: '统计'
    }],
    columns: [
        {text: '标题', dataIndex: 'title', flex: 2, menuDisabled: true},
        // {text: '内容', dataIndex: 'text', flex: 4, menuDisabled: true,renderer: function(value, cellmeta, record) {
        //         var reTag = /<(?:.|\s)*?>/g;
        //         return value.replace(reTag,"");
        //     } },
        // {text: '创建人', dataIndex: 'postedman', flex: 1, menuDisabled: true},
        {text: '创建时间', dataIndex: 'createtime', flex: 1.5, menuDisabled: true},
        {text: '开始时间', dataIndex: 'starttime', flex: 1.5, menuDisabled: true},
        {text: '结束时间', dataIndex: 'endtime', flex: 1, menuDisabled: true},
        {text: '发布时间', dataIndex: 'publishtime', flex: 1, menuDisabled: true},
        {
            text: '发布状态', dataIndex: 'publishstate', flex: 1, menuDisabled: true,
            renderer: function (value, cellmeta, record) {
                if (value == '1') {
                    return '已发布';
                } else {
                    return '未发布';
                }
            }
        },
        {text: '置顶等级', dataIndex: 'stick', flex: 1, menuDisabled: true},
        {
            text: '答题状态', dataIndex: 'state', flex: 1, menuDisabled: true, hidden: true,
            renderer: function (value, cellmeta, record) {
                var responseText;
                var valueText =
                    Ext.Ajax.request({
                        url: '/questionnaireManagement/getState',
                        method: 'POST',
                        async: false,
                        params: {
                            questionnaireID: record.get('questionnaireID')
                        },
                        success: function (resp) {
                            responseText = Ext.decode(resp.responseText);
                        },
                        failure: function () {
                            XD.msg("获取信息失败！");
                        }
                    });
                return responseText.msg;
            }
        }
    ]
});