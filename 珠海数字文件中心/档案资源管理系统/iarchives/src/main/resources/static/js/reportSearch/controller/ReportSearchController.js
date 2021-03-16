/**
 * Created by RonJiang on 2018/2/27 0027
 */
Ext.define('ReportSearch.controller.ReportSearchController', {
    extend: 'Ext.app.Controller',
    views: ['ReportSearchView','TreeComboboxView'],
    stores: ['ReportFundsStore'],
    models: [],
    init: function () {
        this.control({
            'ReportSearchView':{
                afterrender:function (view) {
                    if (reportname == '统计报表') {
                        var reportTypebtn =  view.down('[itemId=reporttypeId]').show();
                        var tip = view.down('[itemId=tipId]').show();
                        view.down('form').setHeight('20%');
                    }
                    else if(reportname == '档案分类数量统计表'){
                        var organbtn = view.down('[itemId=organId]').show();
                        var classbtn = view.down('[itemId=classifyId]').show();
                        var startbtn = view.down('[itemId=startdateid]').show();
                        var endbtn =  view.down('[itemId=enddateid]').show();
                        var classclearId = view.down('[itemId = classClearId]').show();
                        var organclearId = view.down('[itemId = organClearId]').show();
                        var flagopen = view.down('[itemId = flagopen]').show();
                        var filingyear  =view.down('[itemId = filingyeartype]').show();
                        var tip = view.down('[itemId=tipId]').show();
                        var entryretention = view.down('[itemId=entryretentionId]').show();

                        //获取版本判断是否显示数据源下拉框
                        Ext.Ajax.request({
                            method: "post",
                            url: '/getProductMsg',
                            params:{},
                            success: function (result) {
                                var rssultObj = Ext.decode(result.responseText);
                                if(rssultObj.success){
                                    var allowVersion =['增强版','网络版','高级版'];
                                    var data = rssultObj.data;
                                    if(allowVersion.indexOf(data[0])!=-1){
                                        var datasource = view.down('[itemId = datasource]').show();
                                    }
                                }else{
                                    console.log('版本信息获取失败');
                                }
                            },
                            error:function(){
                                console.log('版本信息获取失败');
                            }
                        });
                    }else if (reportname == '前台利用统计') {
                        var startbtn = view.down('[itemId=startdateid]').show();
                        var endbtn =  view.down('[itemId=enddateid]').show();
                        view.down('form').setHeight('20%');
                    }
                    else{
                        var organbtn = view.down('[itemId=organId]').show();
                        var classbtn = view.down('[itemId=classifyId]').show();
                        var startbtn = view.down('[itemId=startdateid]').show();
                        var endbtn =  view.down('[itemId=enddateid]').show();
                        var classclearId = view.down('[itemId = classClearId]').show();
                        var organclearId = view.down('[itemId = organClearId]').show();
                        var tip = view.down('[itemId=tipId]').show();
                    }
                }
            },
            'ReportSearchView [itemId = bottomSearchBtn]':{
                click:function (btn) {
                    var showReportName;
                    var reportSearchView = btn.up('ReportSearchView');
                    var form = reportSearchView.getForm();
                    var formValues = form.getValues();//此处可以获取form对象的所有值

                    if(form.isValid() == false){
                        XD.msg('请输入正确的数据!');
                        return;
                    }
                    if(formValues.startdate > formValues.enddate){
                        XD.msg('开始时间不能大于结束时间！');
                        return;
                    }

                    if (reportname == '统计报表') {
                        var select = reportSearchView.down('[itemId=reporttypeId]');
                        showReportName =  select.lastValue
                    }
                    else if(reportname == '档案分类数量统计表'){
                        showReportName=reportname+'_'+formValues.datasource;
                        // showReportName=reportname
                    }
                    else {
                        showReportName=reportname;
                    }

                    var params = {};
                    if (formValues.className != '' && formValues.className != undefined) {
                        params['className'] = formValues.className;
                    }
                    if (formValues.startdate != '' && formValues.startdate != undefined) {
                        params['starttime'] = formValues.startdate;
                    }
                    if (formValues.enddate != ''&& formValues.enddate != undefined) {
                        params['endtime'] = formValues.enddate;
                    }
                    if (formValues.classifyId != '' && formValues.classifyId != undefined) {
                        params['classifyId'] = formValues.classifyId.join(',');
                    }
                    if (formValues.organId != '' && formValues.organId != undefined) {
                        params['organId'] = formValues.organId.join(",");
                    }
                    if (formValues.filingyear != '' && formValues.filingyear != undefined) {
                        params['filingyear'] = formValues.filingyear.join(",");
                    }
                    if (formValues.flagopen != '' && formValues.flagopen != undefined ) {
                        params['flagopen'] = formValues.flagopen;
                    }
                    if (formValues.funds != '' && formValues.funds != undefined) {
                        params['funds'] = formValues.funds;
                    }
                    if (formValues.datasource != '' && formValues.datasource != undefined) {
                        params['datasource'] = formValues.datasource;
                    }
                    if (formValues.entryretention != '' && formValues.entryretention != undefined) {
                        params['entryretention'] = formValues.entryretention.join(",");
                    }
                    this.showReport(showReportName,btn,params);
                }
            }
        });
    },

    showReport:function (reportname,btn,params) {
        var jsonString = JSON.stringify(params);
        jsonString = jsonString.replace(/\":\"/g, "=");
        jsonString = jsonString.replace(/\",\"/g, "&");
        jsonString = jsonString.replace(/\"/g, "");
        jsonString = jsonString.replace("{", "");
        jsonString = jsonString.replace("}", "");
        var fileName = reportname + '.ureport.xml';

        var url = '/ureport/preview?' + jsonString + '&_u=file:' + fileName + '&_t=1,4,5,6,7' + '&_i=1';
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
})

function changeHtml(src){
    $("#iframeId").attr('src' , src)
}