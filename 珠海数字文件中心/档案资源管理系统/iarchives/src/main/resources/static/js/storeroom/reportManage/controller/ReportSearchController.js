/**
 * Created by RonJiang on 2018/2/27 0027
 */
Ext.define('ReportManage.controller.ReportSearchController', {
    extend: 'Ext.app.Controller',
    views: ['ReportSearchView','TreeComboboxView'],
    stores: [],
    models: [],
    init: function () {
        this.control({
            'ReportSearchView':{
                afterrender:function (view) {
                    if (reportname == '出库统计'){
                        view.down('[itemId=classifyId]').hide();
                        $('#hint').html('温馨提示：若没有选择日期或档案类型，则统计全部数据。');
                    }else if (reportname == '入库统计'){
                        view.down('[itemId=waretypeId]').hide();
                        $('#hint').html('温馨提示：若没有选择日期或分类，则统计全部数据。');
                    }else if (reportname == '消毒统计'){
                        view.down('[itemId=classifyId]').hide();
                        view.down('[itemId=waretypeId]').hide();
                        $('#hint').html('温馨提示：若没有选择日期，则统计全部数据。');
                    }
                }
            },
            'ReportSearchView [itemId = bottomSearchBtn]':{
                click:function (btn) {
                    var reportSearchView = btn.up('ReportSearchView');
                    var form = reportSearchView.getForm();
                    var formValues = form.getValues();//此处可以获取form对象的所有值
                    var classify = reportSearchView.down('comboboxtree');
                    var params = {};
                    var classify = '';
                    if (formValues.startdate != '') {
                        var start = formValues.startdate + ' 00:00:00';
                        params['starttime'] = start;
                    }
                    if (formValues.enddate != '') {
                        var end = formValues.enddate + ' 23:59:59';
                        params['endtime'] = end;
                    }
                    if (reportname == '出库统计') {
                        if (formValues.waretype != '' && formValues.waretype != undefined) {
                            params['waretype'] = formValues.waretype
                        }
                    }else if (reportname == '入库统计'){
                        if (formValues.classifyId != '' && formValues.classifyId != undefined) {
                            var classifyStr = formValues.classifyId.join();
                            Ext.Ajax.request({
                                url:'/nodesetting/getChildNodeId',
                                params:{classifyId:classifyStr},
                                method:'GET',
                                async: false,
                                success:function (response) {
                                    var data = Ext.decode(response.responseText);
                                    if (data.success){
                                        classify = data.data.join();
                                    }
                                }
                            });

                        }
                    }else if (reportname == '消毒统计'){

                    }
                    var jsonString = JSON.stringify(params);
                    var fileName = reportname + '.ureport.xml';
                    jsonString = jsonString.replace(/\":/g, "=");
                    jsonString = jsonString.replace(/\",\"/g, "&");
                    jsonString = jsonString.replace(/\"/g, "");
                    jsonString = jsonString.replace("{", "");
                    jsonString = jsonString.replace("}", "");
                    jsonString = jsonString.replace("[", "");
                    jsonString = jsonString.replace("]", "");
                    if (classify != "" && jsonString == ""){
                        jsonString = 'classifyId='+classify;
                    }else if (classify != "") {
                        jsonString += '&classifyId='+classify;
                    }
                    var fun = [];
                    Ext.each(functionButton,function (item) {
                        if (item.text == '打印'){
                            fun.push(1);//在线打印
                        }else if(item.text == '导出'){
                            fun.push(6);//全部导出excel
                            fun.push(7);//分页导出excel
                        }
                    });
                    var url = '/ureport/preview?' + jsonString + '&_u=file:' + fileName  + '&_t=1,4,5,6,7' + '&_i=1';
                    if (fun.length > 0){
                        var _t = '&_t='+ fun.join();
                        url = '/ureport/preview?' + jsonString + '&_u=file:' + fileName + '&_t=1,4,5,6,7' + '&_i=1';
                    }
                    var newurl = encodeURI(url); //解决IE浏览器的中文乱码问题
                    var southView = btn.findParentByType('ReportSearchView').down('[itemId=reportviewId]');
                    southView.title = reportname;
                    document.getElementById("loadingDiv").style.display = "block";
                    changeHtml(newurl);
                    var mediaFrame = document.querySelector('#iframeId');
                    //加载结束--隐藏loading.gif
                    if (mediaFrame.attachEvent) {
                        mediaFrame.attachEvent("onload", function () {
                            document.getElementById("loadingDiv").style.display = "none";
                        });
                    } else {
                        mediaFrame.onload = function () {
                            document.getElementById("loadingDiv").style.display = "none";
                        };
                    }
                }
            }
        })
    }
})

function changeHtml(src){
    $("#iframeId").attr('src' , src)
}