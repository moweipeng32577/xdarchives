/**
 * Created by Administrator on 2020/3/23.
 */


Ext.define('AppraiseManage.controller.AppraiseManageController', {
    extend: 'Ext.app.Controller',

    views: ['AppraiseManageGridView','AppraiseLookFormView'],//加载view
    stores: ['AppraiseManageGridStore'],//加载store
    models: ['AppraiseManageGridModel'],//加载model

    init: function () {
        this.control({
            'appraiseManageGridView':{
                afterrender:function (view) {
                    view.initGrid();
                }
            },

            'appraiseManageGridView button[itemId=look]':{  //查看
                click:function (view) {
                    var grid = view.findParentByType('appraiseManageGridView');
                    var record = grid.getSelectionModel().getSelection();
                    if (record.length != 1) {
                        XD.msg('请选择一条需要查看的评价信息');
                        return;
                    }
                    var feedbackid = record[0].get('feedbackid');
                    var appraiseLookView = Ext.create('Ext.window.Window', {
                        height: '60%',
                        width: '70%',
                        draggable: false,//禁止拖动
                        resizable: false,//禁止缩放
                        modal: true,
                        closeToolText:'关闭',
                        title: '查看评价',
                        closeAction: 'hide',
                        layout: 'fit',
                        items: [{xtype: 'appraiseLookFormView'}]
                    });
                    var form = appraiseLookView.down('appraiseLookFormView');
                    form.reset();
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
                        }
                    });
                    appraiseLookView.show();
                }
            },

            'appraiseLookFormView button[itemId=back]':{ //返回
                click:function (view) {
                    view.findParentByType('window').close();
                }
            },

            'appraiseManageGridView button[itemId=export]':{  //导出
                click:function (view) {
                    var grid = view.findParentByType('appraiseManageGridView');
                    var record = grid.getSelectionModel().getSelection();
                    if (record.length < 1) {
                        XD.msg('请至少选择一条数据');
                        return;
                    }
                    var feedbackids = [];
                    for(var i=0;i<record.length;i++){
                        feedbackids.push(record[i].get('feedbackid'));
                    }
                   var exportAppraiseView = Ext.create("AppraiseManage.view.ExportAppraiseView");
                    exportAppraiseView.feedbackids = feedbackids;
                    exportAppraiseView.show();
                }
            },

            'exportAppraiseView button[itemId="SaveExport"]': {//导出 提交
                click: function (view) {
                    var exportAppraiseView = view.up('exportAppraiseView');
                    var fileName = exportAppraiseView.down('[itemId=userFileName]').getValue();
                    var zipPassword = exportAppraiseView.down('[itemId=zipPassword]').getValue();
                    var b = exportAppraiseView.down('[itemId=addZipKey]').checked;
                    var form = exportAppraiseView.down('[itemId=form]');

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
                        Ext.MessageBox.wait('正在处理请稍后...');
                        Ext.Ajax.request({
                            method: 'post',
                            url:'/export/exportAppraise',
                            timeout:XD.timeout,
                            scope: this,
                            async:true,
                            params: {
                                fileName:fileName,
                                zipPassword:zipPassword,
                                feedbackids:exportAppraiseView.feedbackids
                            },
                            success:function(res){
                                var filePath = Ext.decode(res.responseText).data;
                                window.location.href="/export/downloadZipFile?fpath="+encodeURIComponent(filePath);
                                Ext.MessageBox.hide();
                                XD.msg('文件生成成功，正在准备下载');
                                exportAppraiseView.close()
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

            'exportAppraiseView button[itemId="cancelExport"]': {//导出 取消
                click: function (view) {
                    view.up('exportAppraiseView').close();
                }
            },

            'appraiseManageGridView button[itemId=census]':{  //统计
                click:function (view) {
                    var grid = view.findParentByType('appraiseManageGridView');
                    var record = grid.getSelectionModel().getSelection();
                    if (record.length < 1) {
                        XD.msg('请至少选择一条数据');
                        return;
                    }
                    var feedbackids = [];
                    var params = {};
                    for(var i=0;i<record.length;i++){
                        feedbackids.push(record[i].get('feedbackid').trim());
                    }
                    if(reportServer == 'UReport') {
                        params['feedbackid'] = feedbackids.join(",");
                        XD.UReportPrint(null, '评价统计表', params);
                    }
                    else if(reportServer == 'FReport') {
                        XD.FRprint(null, '评价统计表', feedbackids.length > 0 ? "'feedbackid':'" + feedbackids.join(",") + "'" : '')  ;
                    }
                }
            }
        });
    },

    //获取反馈管理应用视图
    findView: function (btn) {
        return btn.up('feedback');
    }
});
