/**
 * Created by Rong on 2017/12/6.
 */
Ext.namespace("XD");

XD.pageSize = 50;
XD.splitChar = ',';
XD.treeWidth = 300;
XD.serverURL='http://localhost:8081';
XD.reportUrl = 'http://localhost:8081/WebReport/ReportServer?reportlet=';
XD.timeout = 3600000;

XD.msg = function(text){
    Ext.toast({
        autoCloseDelay: 2000,
        minWidth: 400,
        maxWidth: 600,
        title:'提示信息',
        iconCls:'x-fa fa-exclamation-circle',
        html: "<font>" + text + "</font>"
    });
}

XD.confirm = function(text, callbackyes, scope, callbackno){
    var notes = '';
    var html = '<img class="x-box-icon" title = "帮助与支持" src="../img/icon/help_icon.png" onclick= XD.openHelp(' + notes + ')>' + text  ;
    Ext.MessageBox.confirm('确认信息', html, function (btn) {
        if (btn == 'yes') {
            if(typeof callbackyes=='undefined'){
                return;
            }
            var fn = callbackyes.bind(this);
            fn();
        }
        if (btn == 'no') {
            if(typeof callbackno=='undefined'){
                return;
            }
            var fn = callbackno.bind(this);
            fn();
        }
    }, scope);
   Ext.MessageBox.setIcon('');
}
//打开帮助与支持界面
XD.openHelp = function (notes) {
    if(notes == undefined) {
        var url = '../doc/用户操作手册.pdf';
        window.open(url);
    }
    else {
       console.log(notes);
    }
}
/**
 * 使用iframe内嵌URL时，当请求数据量太大，会导致请求头过长异常
 * 初步方案：引入报表FineReport.js,使用报表内置打印函数方法
 * 若在ajax里面调用该方法，需要在ajax外部新建标签页传进来，可以参考数据采集、数据管理的打印
 * 注意地方：参数为json格式
 *  如{'reportlet':'recordcatalog_index_new.cpt',
 *      'entryid':'242b2bf05ddc11e8be761c1b0dcbaf4c,2409cd005ddc11e88ca31c1b0dcbaf4c',
 *      'nodeid':'4028e681636cd73101636d3d2ae50304'}
 * @param window 新的标签页，可以为空
 * @param reportname 报表名称
 * @param args 数据集
 */
XD.FRprint = function (win, reportname, args) {
    var reportlet = "{'reportlet':'" + reportname + ".cpt'" + (args.length > 0 ? "," + args + "}" : '}');
    if(win==null){
        //如果在ajax里面打开新标签页，会被拦截
        win = window.open('');
    }
    Ext.Ajax.request({
        url: '/report/finereportly',
        method: 'POST',
        params: {
            reportlet: reportlet
        },
        success: function (response) {
            var html = response.responseText;
            win.document.write(html);
        }
    });
}

/**
 * 解决问题：1、IE浏览器无法打开报表问题
 *           2、参数过多无法打印
 * @param title 标题
 * @param reportname 报表名称
 * @param params 数据集
 */
XD.UReportPrint = function(title, reportname, params){
    var url = '/ureport/preview';
    var newWindow = window.open(url, 'aaa');
    if (!newWindow)
        return false;
    var html = "";
    html += "<html><head></head><body><form id='printform' method='post' accept-charset='utf-8' action='" + url + "'>";
    var params = params;
    var newportname = encodeURI(reportname);
    params['_title'] = newportname;
    params['_u'] = 'file:' + newportname + '.ureport.xml';
    params['_t'] = '1,4,5,6,7';
    params['_i'] = '1';
    for(var key in params){
        html += "<input type='hidden' name=" + key + " value=" + params[key] + " />";
    }
    html += "</form><script type='text/javascript'>document.getElementById('printform').submit();";
    html += "<\/script></body></html>".toString().replace(/^.+?\*|\\(?=\/)|\*.+?$/gi, "");
    newWindow.document.write(html);
}

XD.print = function(reportname, args){
    var reportUrl = XD.reportUrl + reportname + '.cpt' + (args.length > 0 ? '&'+args : '');
    var win = Ext.create('Ext.window.Window',{
        header:false,
        maximized:true,
        closeToolText:'关闭',
        html:'<iframe src="' + reportUrl + '" frameborder="0" style="width: 100%;height: 100%"></iframe>',
        buttons:[{
            text:'关闭',
            handler:function(){
                win.close();
            }
        }]
    });
    win.show();
}

Date.prototype.format = function (format) {
    var date = {
        "M+": this.getMonth() + 1,
        "d+": this.getDate(),
        "h+": this.getHours(),
        "m+": this.getMinutes(),
        "s+": this.getSeconds(),
        "q+": Math.floor((this.getMonth() + 3) / 3),
        "S+": this.getMilliseconds()
    };
    if (/(y+)/i.test(format)) {
        format = format.replace(RegExp.$1, (this.getFullYear() + '').substr(4 - RegExp.$1.length));
    }
    for (var k in date) {
        if (new RegExp("(" + k + ")").test(format)) {
            format = format.replace(RegExp.$1, RegExp.$1.length == 1
                ? date[k] : ("00" + date[k]).substr(("" + date[k]).length));
        }
    }
    return format;
}