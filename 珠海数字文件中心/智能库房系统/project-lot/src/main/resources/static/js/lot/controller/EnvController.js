/**
 * 库房环境控制器
 * 显示温湿度设备温湿度曲线图
 *
 * Created by Rong on 2019-01-17.
 */
Ext.define('Lot.controller.EnvController',{

    extend: 'Ext.app.Controller',
    views:['HT.HTConditionFormView'],
    init:function(){
        var areaValue;
        var floorValue;
        this.control({
            '[itemId=envPanel]':{
                init:function(panel){
                    this.initialize(panel);
                }
            },
            'HTDetail [itemId=HTRecordPrint]':{
                click:this.printRecordHandler
            },
            'HTDetail [itemId=HTCruvePrint]':{
                click:this.printCruveHandler
            },
            'LEDDetail [itemId=endDate]':{
                select:this.changeRangeLED
            },
            'LEDDetail [itemId=beginDate]':{
                select:this.changeBRangeLED
            },
            'HTConditionForm [itemId = search]':{
                click:function (btn) {
                    var from = btn.up("HTConditionForm");
                    var areaCombo = from.down('[itemId=room]');
                    var foorCombo = from.down('[itemId=floor]')
                    areaValue = areaCombo.getValue();
                    floorValue = foorCombo.getValue();
                    var envPanel =btn.up('[itemId = envPanel]');
                    this.initialize(envPanel,floorValue,areaValue);

                }
            }
        });
    },

    initialize:function(panel,floorid,areaid){
        panel.removeAll();
        panel.add({
            xtype:'HTConditionForm',
            region:'north',
        },{
            xtype:'panel',
            region:'center',
            itemId:'HT',
            layout:'hbox',
            bodyStyle : 'overflow-x:scroll; overflow-y:hidden',
            autoScroll : true
        });
        var HTPanel = panel.down('[itemId=HT]');
        HTPanel.removeAll();
        var deviceAreas;
        var _this = this;
        deviceAreas = this.getStore('DeviceAreaStore').load({
            params:{
                floorid:floorid,
                areaid:areaid,
            },
            callback:function () {

                var HTForm = panel.down('HTConditionForm');
                if (floorid != undefined || floorid != null) {
                    HTForm.down('[itemId = floor]').setValue(floorid);
                }

                if (areaid != undefined || areaid != null) {
                    HTForm.down('[itemId = floor]').setValue(floorid);
                    HTForm.down('[itemId = room]').setValue(areaid);
                }


                //根据窗口大小计算温湿度面板的大小
                var width = panel.getWidth() / 2 - 50;
                var height = width * 225 / 300;
                //遍历设备列表，动态添加温湿度设备面板
                var panels = [];
                for (var i = 0; i < deviceAreas.getCount(); i++) {
                    var devicearea = deviceAreas.getAt(i);
                    if (devicearea.get('type').toLowerCase() == 'kf') {
                        var htpanel = Ext.create('Ext.panel.Panel', {
                            width: width,
                            height: height,
                            frame: true,
                            margin: '0 10 0 0',
                            deviceArea: devicearea,
                            title: devicearea.get('name') + "-" + '平均温湿度'
                        });
                        HTPanel.add(htpanel);
                        // _this.initCharts(htpanel);
                        panels.push(htpanel);
                    }
                }
                _this.initCharts(panels);
            }
        });
    },

    /**
     * 构造温湿度曲线图
     * @param htpanel
     */
    initCharts:function(htpanels){

        var connection;
        Ext.MessageBox.wait('正在操作','请稍后...');
        Ext.Ajax.request({
            method:'post',
            url:'/speed/getConnection',
            scope:this,
            timeout:10000,//解决超时问题
            async:true,
            success:function(res, opt){
                connection = Ext.decode(res.responseText).success;
                if(connection == false){
                    Ext.MessageBox.hide();
                    XD.msg('无法连接到：10.10.100.3');
                }
                else{
                    if(htpanels.length>0 && connection == true){
                        for(var index in htpanels){
                            var htpanel = htpanels[index];
                            var myChart = echarts.init(Ext.getDom(htpanel.body));
                            //温度、湿度曲线颜色
                            var colors = ['#5793f3', '#d14a61'];
                            //温湿度采集时间（x轴）
                            var data = [];
                            //温度值（y轴-左侧）
                            var data1 = [];
                            //湿度值（y轴-右侧）
                            var data2 = [];

                            Ext.Ajax.request({
                                method:'GET',
                                url:'/speed/ht/histories',
                                async:false,
                                params:{
                                    page : 1,
                                    limit : 50,
                                    timeout:5000,//解决超时问题
                                    areaId:htpanel.deviceArea.id,
                                    deviceType:"HT"
                                },
                                success:function(res, opt){
                                    var content = Ext.decode(res.responseText).content;
                                    for(var i = content.length - 1; i > 0; i--){
                                        var time = content[i].captureTime.substring(11,content[i].captureTime.indexOf("."));
                                        data.push(time);
                                        data1.push(content[i].tem);
                                        data2.push(content[i].hum);
                                    }
                                },
                                failure: function (form, action) {
                                    Ext.MessageBox.hide();
                                    XD.msg('操作失败');
                                }
                            });

                            option = {
                                color: colors,
                                tooltip: {
                                    trigger: 'axis',
                                    axisPointer: {
                                        type: 'cross'
                                    }
                                },
                                legend: {
                                    data: ['温度', '湿度']
                                },
                                xAxis: [{
                                    type: 'category',
                                    axisTick: {
                                        alignWithLabel: true
                                    },
                                    data: data
                                }],
                                yAxis: [{
                                    type: 'value',
                                    name: '湿度',
                                    min:30,
                                    max:90,
                                    position: 'right',
                                    axisLabel: {
                                        formatter: '{value} %'
                                    }
                                },{
                                    type: 'value',
                                    name: '温度',
                                    min:10,
                                    max:40,
                                    position: 'left',
                                    axisLine: {
                                        lineStyle: {
                                            color: colors[1]
                                        }
                                    },
                                    axisLabel: {
                                        formatter: '{value} °C'
                                    }
                                }],
                                series: [{
                                    name: '湿度',
                                    type: 'line',
                                    yAxisIndex: 0,
                                    data: data2
                                },{
                                    name: '温度',
                                    type: 'line',
                                    yAxisIndex: 1,
                                    data: data1
                                }]
                            };
                            myChart.setOption(option);
                            Ext.MessageBox.hide();
                        }
                    }
                }
            },
            failure: function (form, action) {
                Ext.MessageBox.hide();
                XD.msg('操作失败,请求超时！');
            }

        });

    },

    //打印記錄表
    printRecordHandler:function (btn) {
        var params = {};
        var HT = btn.ownerCt.findParentByType('HTDetail');
        var deviceid = HT.device.id;
        //通过deviceid获取设备属性
        Ext.Ajax.request({
            method: 'GET',
            async:false,
            url:'/device/' + deviceid,
            success: function (response) {
                var  device = Ext.decode(response.responseText);
                var  prop =  Ext.decode(device.prop);
                params['devicename'] = device.name;
                params['temNo'] = prop.tempno;
                params['humNo'] = prop.humino;
            }
        });
        var startValue = HT.down('[itemId=beginDate]').getValue();
        var endValue = HT.down('[itemId=endDate]').getValue();
        var startTime;
        var endTime;
        if(!startValue){
            startTime=new Date().format("yyyy-MM-dd")  + ' 00:00:00';
            params['begintime'] = startTime;
        }
        else {
            startTime = startValue.format("yyyy-MM-dd") + ' 00:00:00';
            params['begintime'] = startTime;
        }
        if(!endValue){
            Ext.toast({
                autoCloseDelay: 2000,
                minWidth: 400,
                maxWidth: 600,
                title:'提示信息',
                iconCls:'x-fa fa-exclamation-circle',
                html: "<font>打印统计结束日期不能为空</font>"
            });
            return;
        }else{
            endTime = endValue.format("yyyy-MM-dd") + ' 23:59:59';
            params['endtime'] = endTime;
        }
        UReportPrint('温湿度记录表', 'temphumi', params);
    },

    //打印曲線圖表
    printCruveHandler:function(btn){
        var params = {};
        var HT = btn.ownerCt.findParentByType('HTDetail');
        var deviceid = HT.device.id;
        //通过deviceid获取设备属性
        Ext.Ajax.request({
            method: 'GET',
            async:false,
            url:'/device/' + deviceid,
            success: function (response) {
                var  device = Ext.decode(response.responseText);
                var  prop =  Ext.decode(device.prop);
                params['devicename'] = device.name;
                params['temNo'] = prop.tempno;
                params['humNo'] = prop.humino;
            }
        });
        var startValue = HT.down('[itemId=beginDate]').getValue();
        var endValue = HT.down('[itemId=endDate]').getValue();
        var startTime;
        var endTime;
        if(!startValue){
            startTime=new Date().format("yyyy-MM-dd")  + ' 00:00:00';
            params['begintime'] = startTime;
        }
        else {
            startTime = startValue.format("yyyy-MM-dd") + ' 00:00:00';
            params['begintime'] = startTime;
        }
        if(!endValue){
            Ext.toast({
                autoCloseDelay: 2000,
                minWidth: 400,
                maxWidth: 600,
                title:'提示信息',
                iconCls:'x-fa fa-exclamation-circle',
                html: "<font>打印统计结束日期不能为空</font>"
            });
            return;
        }else{
            endTime = endValue.format("yyyy-MM-dd") + ' 23:59:59';
            params['endtime'] = endTime;
        }
        UReportPrint('温湿度圖形表', 'temphumiLine', params);
    },
    //修改结束日期，刷新数据
    changeRangeLED:function(field, value) {
        var view = field.ownerCt.findParentByType('LEDDetail');
        var store=view.down('grid').getStore();
        var begin=view.down('[itemId=beginDate]').value;
        var end=value.format("yyyy-MM-dd HH:mm:ss");
        if(begin){
            var begintime = parseInt(begin.format("yyyyMMdd"));
            var endtime = parseInt(value.format("yyyyMMdd"));
            if(begintime>=endtime){
                Ext.toast({
                    autoCloseDelay: 2000,
                    minWidth: 400,
                    maxWidth: 600,
                    title:'提示信息',
                    iconCls:'x-fa fa-exclamation-circle',
                    html: "<font>结束日期必须大于起始日期</font>"
                });
                return;
            }
            begin=begin.format("yyyy-MM-dd HH:mm:ss");
        }else{
            begin=new Date(null).format("yyyy-MM-dd HH:mm:ss");
        }
        store.proxy.url = "/device/" + view.device.id + "/historiesbysearchTime/"+begin+"/"+end;
        store.loadPage(1);
    },

    //修改开始日期
    changeBRangeLED:function(field, value) {
        var view = field.ownerCt.findParentByType('LEDDetail');
        var store=view.down('grid').getStore();
        var end=view.down('[itemId=endDate]').value;
        if(end){
            var begintime = parseInt(value.format("yyyyMMdd"));
            var endtime = parseInt(end.format("yyyyMMdd"));
            if(begintime>=endtime){
                Ext.toast({
                    autoCloseDelay: 2000,
                    minWidth: 400,
                    maxWidth: 600,
                    title:'提示信息',
                    iconCls:'x-fa fa-exclamation-circle',
                    html: "<font>起始日期必须小于结束日期</font>"
                });
                return;
            }
            end=end.format("yyyy-MM-dd HH:mm:ss");
        }else{
            end="notime";
        }
        var begin=value.format("yyyy-MM-dd HH:mm:ss");
        store.proxy.url = "/device/" + view.device.id + "/historiesbysearchTime/"+begin+"/"+end;
        store.loadPage(1);
    }
});

UReportPrint = function(title, reportname, params){
    var url = '/ureport/preview';
    var newWindow = window.open('');
    if (!newWindow)
        return false;
    var html = "";
    html += "<html><head></head><body><form id='printform' method='post' accept-charset='utf-8' action='" + url + "'>";
    var params = params;
    var newportname = encodeURI(reportname);
    params['_title'] = reportname;
    params['_u'] = 'file:' + reportname + '.ureport.xml';
    params['_t'] = '1,4,5,6,7';
    params['_i'] = '1';
    for(var key in params){
        html += "<input type='hidden' name=" + key + " value=" + params[key] + " />";
    }
    html += "</form><script type='text/javascript'>document.getElementById('printform').submit();";
    html += "<\/script></body></html>".toString().replace(/^.+?\*|\\(?=\/)|\*.+?$/gi, "");
    newWindow.document.write(html);
}