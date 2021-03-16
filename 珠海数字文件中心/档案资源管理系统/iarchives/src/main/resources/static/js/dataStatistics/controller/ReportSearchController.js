/**
 * Created by RonJiang on 2018/2/27 0027
 */
Ext.define('DataStatistics.controller.ReportSearchController', {
    extend: 'Ext.app.Controller',
    views: ['ReportSearchView'],
    stores: [],
    models: [],
    init: function () {
        this.control({
            'ReportSearchView':{
                afterrender:function (view) {

                }
            },'ReportSearchView [itemId = bottomSearchBtn]':{
                click:function (btn) {
                    var params = {};
                    var reportSearchView = btn.up('ReportSearchView');
                    var form = reportSearchView.getForm();
                    var formValues = form.getValues();//此处可以获取form对象的所有值
                    var reportname=formValues.reportType;
                    if(form.isValid() == false){
                        XD.msg('请输入正确的数据!');
                        return;
                    }
                    if(formValues.startdate > formValues.enddate){
                        XD.msg('开始时间不能大于结束时间！');
                        return;
                    }
                    if (reportname == '总数统计表') {
                        Ext.Ajax.request({
                            url: '/projectRate/getProjectOpenNodeId',
                            method: 'GET',
                            async:false,
                            success: function (response) {
                                var data = Ext.decode(response.responseText).data;
                                params['nodeIds'] = data.join(',');//项目管理节点
                            }
                        })
                    }
                    if (formValues.startdate != '' && formValues.startdate != undefined) {
                        params['starttime'] = formValues.startdate;
                    }
                    if (formValues.enddate != ''&& formValues.enddate != undefined) {
                        params['endtime'] = formValues.enddate;
                    }
                    this.showReport("综合事务_"+reportname,btn,params);
                }
            }
        })
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