/**
 * 库房管理可视化控制器
 * Created by Rong on 2019/1/16.
 */
Ext.define('Lot.controller.VisualizationController',{

    extend: 'Ext.app.Controller',

    views:['VisualizationView','DeviceListView','DeviceDetailView','KTDetailView',
        'EnvironmentSettingFormView','HWHSDetailView','HTDetailView','JKDetailView','LEDDetailView',
        'MJDetailView','MJJDetailView','XFDetailView','AFDetailView','MJJRuleView','DeviceDiagnoseView',
        'AFSettingFormView','MJJRuleView','SJDetailView'],
    stores:['FloorStore','DeviceStore','DeviceTypeStore','DeviceHistoryStore','DeviceDiagnoseLookStore',
        'SpeedHistoryStore','MJDetailStore','SJDetailStore','DevicePanelStore','DeviceByFloorCodeStore'],
    models:['DeviceModel','DeviceHistoryModel','DeviceDiagnoseLookModel','SpeedHistoryModel',
        'MJDetailModel','SJDetailModel'],

    init:function(){
        this.control({
            'visualization':{
                'tabchange':function(tab,newCard,oldCard){
                    //切换面板时调用组件的init方法进行初始化
                    newCard.fireEvent('init',newCard);
                    newCard.fireEvent('render',newCard);
                    oldCard.fireEvent('destory',oldCard);
                }
            },
            'devicelist':{
                'rowdblclick':function(grid,record){
                    this.showDeviceDetail(record);
                }
            },
            '[itemId=totalPanel]':{
                render:function(totalplane){
                    //构造可视化主界面，在设备数据加载完成后执行
                    var plane = totalplane.down('[itemId=plane]');
                    var treeList = totalplane.down('devicelist');
                    // var store=this.getStore('DeviceStore');
                    // store.reload();
                    // store.on('load', function(){
                    this.renderPlane(plane,treeList);
                    // }, this);
                }
            },
            'HTDetail':{
                render:this.HTGridRender
            },
            'LEDDetail':{
                render:this.LEDGridRender
            },
            'SJDetail':{  //水浸页面窗口初始化渲染
                render:this.SJGridRender
            },
            'MJDetail [itemId=open]': {
                click: this.MJOpen
            },
            'MJDetail [itemId=normalOpen]': {
                click: this.MJnormalOpen
            },
            'MJDetail [itemId=normalClose]': {
                click: this.MJnormalClose
            },
            'MJDetail':{
                render:this.MJGridRender
            },
            'MJJDetail':{
                render:this.MJJGridRender
            },
            'AFDetail':{
                render:this.AFGridRender
            },
            'XFDetail':{
                render:this.XFGridRender
            },
            'environmentSettingForm [itemId=temSetting]':{
                click:function (btn) {
                    var tem = btn.up('environmentSettingForm').down('[itemId=tem]').getValue();
                    this.setTempHumi(btn, tem, -1);
                }
            },
            'environmentSettingForm [itemId=humSetting]':{
                click:function (btn) {
                    var hum = btn.up('environmentSettingForm').down('[itemId=hum]').getValue();
                    this.setTempHumi(btn, -1, hum);
                }
            },
            'environmentSettingForm [itemId=start]':{
                click:this.startOrStop
            },
            'environmentSettingForm [itemId=stop]':{
                click:this.startOrStop
            },
            'AFSettingForm [itemId=start]':{
                click:this.startOrStop
            },
            'AFSettingForm [itemId=stop]':{
                click:this.startOrStop
            },
            //--设备诊断
            'HTDetail [itemId=deviceDiagnoseBtn]':{
                click:function (view) {
                    var me = this;
                    me.deviceDiagnose(view,'HTDetail');
                }
            },
            'LEDDetail [itemId=deviceDiagnoseBtn]':{
                click:function (view) {
                    var me = this;
                    me.deviceDiagnose(view,'LEDDetail');
                }
            },
            'MJDetail [itemId=deviceDiagnoseBtn]':{
                click:function (view) {
                    var me = this;
                    me.deviceDiagnose(view,'MJDetail');
                }
            },
            'MJJDetail [itemId=deviceDiagnoseBtn]':{
                click:function (view) {
                    var me = this;
                    me.deviceDiagnose(view,'MJJDetail');
                }
            },
            'AFDetail [itemId=deviceDiagnoseBtn]':{
                click:function (view) {
                    var me = this;
                    me.deviceDiagnose(view,'AFDetail');
                }
            },
            'XFDetail [itemId=deviceDiagnoseBtn]':{
                click:function (view) {
                    var me = this;
                    me.deviceDiagnose(view,'XFDetail');
                }
            }
        });
    },

    /**
     * 设置温湿度
     * 调用空调机组面板的方法
     * @param btn
     * @param temp 温度
     * @param humi 湿度
     */
    setTempHumi:function(btn, temp, humi){
        var panel = btn.up('panel').up('panel');
        panel.command(temp, humi);
    },

    /**
     * 开机停机，调用抽湿机面板的方法
     * 布防撤防，调用安防面板的方法
     * @param btn
     */
    startOrStop:function(btn){
        var opt = 0;
        if(btn.getItemId() == 'start'){
            opt = 1;
        }else if(btn.getItemId() == 'stop'){
            opt = 0;
        }
        var panel = btn.up('panel').up('panel');
        panel.command(opt);
    },

    /**
     * 可视化工作区构造
     * @param plane
     */
    renderPlane:function(plane,treeList){
        var me = this;
        plane.removeAll();
        plane.setHtml('');
        plane.add({
            xtype:'button',
            width:100,
            text:'切换3D显示',
            margin:'5 0 0 5',
            handler:function(){
                var floorValue = window.floorValue;
                if(floorValue == '3楼'){
                    plane.removeAll();
                    plane.setBodyStyle('background-image','');
                    plane.setHtml('<iframe src="/3DStroreroom/threeFloor.html" width="100%" height="100%" frameborder="none;"></iframe>');
                }
                else if(floorValue == '4楼'){
                    plane.removeAll();
                    plane.setBodyStyle('background-image','');
                    plane.setHtml('<iframe src="/3DStroreroom/fourFloor.html" width="100%" height="100%" frameborder="none;"></iframe>');
                }
                else{
                    XD.msg("该区域没有3D模型！")
                }
            }
        },{
            xtype:'combo',
            width:100,
            margin:'5 0 0 115',
            store:'FloorStore',
            forceSelection:true,
            displayField: "floorName",
            valueField: "floorCode",
            queryMode: "local",
            listeners:{
                select:function(combo,record,index){
                    plane.setBodyStyle('background-image','url(\'' + record.get('floorMap') + '\')');
                    plane.setBodyStyle('background-position-y','center');

                    window.floorValue = record.get('floorName');

                    var treeStore= treeList.getStore();
                    treeStore.load({
                        params:{floorCode:record.get('floorCode')},
                        callback: function () {
                            me.initDeviceView(plane, record.get('floorCode'));
                        }
                    });
                },
                afterrender:function(combo){
                    var store = combo.getStore();
                    store.load({
                        callback:function () {
                            combo.select(store.getAt(0));
                            combo.fireEvent('select', combo, store.getAt(0));
                        }
                    })
                }
            }
        });
    },

    /**
     * 遍历设备列表，将其动态添加到面板上
     * @param plane
     * @param floorCode
     */
    initDeviceView: function(plane, floorCode){
        var i = plane.items.length;
        while(i--){
            if(plane.items.getAt(i).xtype == 'image'){
                plane.remove(plane.items.getAt(i));
            }
        }

        var me = this;
        var devices = this.getStore('DeviceStore');
        for(var i=0;i<devices.getCount();i++){
            var device = devices.getAt(i);

            if(null == device.get('area') || null==device.get('area').floor || device.get('area').floor.floorCode != floorCode){
                continue;
            }
            //其位置大小读取coordinate配置
            var coordinates = device.get('coordinate').split(",");
            var widthscale = plane.getWidth() / 1290;
            var heightscale = plane.getWidth() * 1080 / 1920 / 745;
            if(plane.getWidth() == '1046'){
                widthscale = widthscale * 8.5 / 10;
                heightscale = heightscale * 9.7 / 10;
            }else if(plane.getWidth() == '704'){
                widthscale = widthscale * 11.7 / 10;
                heightscale = heightscale * 11.8 / 10;
            }else if(plane.getWidth() == '1280'){
                widthscale = widthscale * 10.3 / 10;
                heightscale = heightscale * 10.3 / 10;
            }
            var img = {
                xtype:'image',
                width:parseInt(coordinates[2]) * widthscale,
                height:parseInt(coordinates[3]) * heightscale,
                style:{
                    'left': (parseInt(coordinates[0]) * 8.5 / 10) * widthscale + 'px',
                    'top': (parseInt(coordinates[1]) * 9.7 /10) * heightscale + 'px',
                    'cursor':'pointer'  //鼠标改为手型，标识可点击
                },
                device:device,
                // src:'/img/equip/'+ device.get('type').typeCode.toLowerCase() +'.png',
                src:device.get('type').typeMap,
                title:device.get('name'),
                //Img组件没有click事件，使用Ext.dom.Element的click事件
                listeners:{
                    el:{
                        click:function(e, t){
                            me.showDeviceDetail(Ext.getCmp(t.id).device);
                        }
                    }
                }
            };
            if(coordinates[0] < 200){
                img.style.transform = 'rotateY(165deg)';
            }
            plane.add(img);
        }
    },

    /**
     * 显示设备详细信息
     * @param device
     */
    showDeviceDetail:function(device){

        var win = Ext.create('Ext.window.Window',{
            closeToolText:'关闭',
            //modal:true,
            width:window.innerWidth-220,
            height:window.innerHeight-50,
            title:device.get('name'),
            type:device.get('type'),
            layout:'fit',
            items:[{
                device:device,
                xtype:device.get('type').typeCode.toUpperCase() + 'Detail'
            }]
        });

        var nature= device.get('prop');

        if(nature == ""||nature == null){
            XD.msg('请检查该设备的属性是否配置正确！');
            return;
        }
        win.show();
    },

    HTGridRender:function(htview){
        var temstore = htview.down('[itemId=temgrid]').getStore();
        temstore.proxy.extraParams.deviceid = htview.device.id;
        temstore.load();
    },

    LEDGridRender:function (ledview) {
        var grid = ledview.down('grid');
        grid.getStore().proxy.url = "/device/histories";
        grid.getStore().proxy.extraParams.deviceid = ledview.device.id;
        grid.getStore().load();
    },
    MJGridRender:function (mjview) {
        var grid = mjview.down('grid');
        grid.getStore().proxy.url = "/entrance/eventList";
        grid.getStore().proxy.extraParams.deviceid = mjview.device.id;
        grid.getStore().load();
        grid.on('itemdblclick', function () {
            var devices = this.getStore('DeviceStore');
            for (var i = 0; i < devices.getCount(); i++) {
                if (devices.getAt(i).get('type') == 'MJ') {
                    this.showDeviceDetail(devices.getAt(i));
                    return;
                }
            }
        }, this);
    },

    MJJGridRender:function (mjjview) {
        var areaPanel = mjjview.down('[itemId=reservoirArea]');
        var recordValue = mjjview.down('[itemId=recordColId]');
        var deviceId = mjjview.device.id;//设备id
        var me = this;
        var screenWidth = window.screen.width;//电脑分辨率
        var imgHeight;
        var imgWidth;
        if (screenWidth <= 1366) {//根据识别屏幕分辨率为1366时来改变html字体大小
            imgHeight = 260;
            imgWidth = 40;
        } else if (screenWidth > 1366) {
            imgHeight = 355;
            imgWidth = 55;
        }
        Ext.Ajax.request({
            url: '/device/getZoneShelves',
            params: {deviceId: deviceId},
            method: 'GET',
            async: true,
            success: function (response) {
                var result = Ext.decode(response.responseText);
                var fixed = null;
                var data = result.data;
                if (data.length > 0) {
                    fixed = data[0].zone.fixed;//固定列
                }
                for (var i = 0; i < data.length; i++) {
                    //密集架列进度条。少于1% 显示1%
                    var rate = Number(data[i].rate);
                    if(rate>0&&rate<0.01){
                        rate= '0.01'
                    }
                    else {
                        rate= data[i].rate
                    }

                    var obj = {
                        layout: 'absolute',
                        items: [{
                            xtype: 'panel',
                            html: fixed==(i+1)?"固定列"+(i + 1):(i + 1),
                            items: [{
                                xtype: 'image',
                                src: '/img/equip/col.png',
                                x: 0,
                                y: 0,
                                data: i,
                                margin: '5 10 5 10',
                                width: imgWidth,
                                height: imgHeight
                            }],
                            bodyStyle: 'text-align: center; ',
                            listeners: {
                                el: {//由于图片没有事件,所以使用el元素另外添加事件
                                    click: function (e, t) {
                                        //var col = $(t).parent().text();
                                        var col = Ext.get(t).getParent().dom.textContent.replace('固定列','');
                                        recordValue.setValue(col);
                                        me.MJJRuleInit(deviceId, col, areaPanel);

                                        // 选中效果
                                        var imgs = areaPanel.body.dom.getElementsByTagName('img');
                                        for(var i = 0; i<imgs.length;i++){
                                            var img = imgs[i];
                                            if( (i+1) == fixed){
                                                img.src= '/img/equip/fixedcol.png'
                                            }
                                            else{
                                                img.src= '/img/equip/col.png'
                                            }
                                        }
                                        this.dom.getElementsByTagName('img')[0].src = '/img/equip/ccol.png';
                                    }
                                }
                            }
                        }, {//进度条组件
                            xtype: 'progressbar',
                            width: imgWidth,
                            x: 45 / 2 - 12,
                            y: 130,
                            value: rate
                        }]
                    };
                    if (fixed != null && (i + 1) == fixed) {//固定列
                        obj.items[0].items[0].src = '/img/equip/fixedcol.png';
                    }
                    if(i+1== 1){
                        obj.items[0].items[0].src = '/img/equip/ccol.png';
                    }
                    areaPanel.add(obj);
                }
                areaPanel.add({
                    xtype: 'combobox',
                    itemId: 'sideId',
                    width: 80,
                    margin: '5 0 0 5',
                    store: [['A面', 'A面'], ['B面', 'B面']],
                    value: 'A面'
                });
            }
        });
        var rule = mjjview.down('[itemId=ruleId]');
        rule.add({
            xtype: 'MJJRule'
        });
        areaPanel.on('afterrender', function () {
            me.MJJRuleInit(deviceId, '1', areaPanel);
        });

    },
    AFGridRender:function (afview) {
        // var grid = afview.down('grid');
        // grid.getStore().proxy.url = "/device/histories";
        // grid.getStore().proxy.extraParams.deviceid = afview.device.id;
        // grid.getStore().load();
    },
    XFGridRender:function (xfview) {
        var grid = xfview.down('grid');
        grid.getStore().proxy.url = "/device/histories";
        grid.getStore().proxy.extraParams.deviceid = xfview.device.id;
        grid.getStore().load();
    },

    MJJRuleInit:function (deviceId,col,view) {
        var  MJJPanel = view.up('MJJDetail');
        var  gridPanel = MJJPanel.down('MJJRule');
        var sidePanel = MJJPanel.down('[itemId=sideId]');
        var side;
        if (sidePanel) {
            side = sidePanel.getValue();
        }else {
            side = "A面";
        }
        Ext.Ajax.request({
            url: '/shelves/getCell',
            params:{
                deviceId:deviceId,
                col:col,
                side:side
            },
            method:'GET',
            success: function (response) {
                // var data = ;//Ext.decode(response.responseText);
                var data = response.responseText.replace(RegExp("\"", "g"),"");
                var colStr=data.substring(data.lastIndexOf(']')+1,data.length);
                var storeData=data.substring(0,data.lastIndexOf(']')+1);
                //重置该表格的model和store
                var tablejsondata= Ext.util.JSON.decode(storeData);//将json字符串转换为JSON对象数组
                var colArr=colStr.split(',');
                var fields = [];
                var columns = [];
                for(var i=0;i<colArr.length;i++) {
                    var colName = colArr[i];
                    columns.push({
                        text: colName,
                        dataIndex: colName,
                        menuDisabled:true,//取消箭头下拉排序
                        width:120,renderer: function(value, meta, record) {//设置自动换行
                            meta.style = 'align:center;overflow:visible;white-space:normal;';
                            return value;
                        }
                    });
                }
                fields.push(columns);
                Ext.create('Ext.data.Store', {
                    storeId:'ctlStore',
                    fields:fields,
                    data: tablejsondata,
                    proxy: {
                        type: 'memory',
                        reader: {
                            type: 'json'
                        }
                    }
                });
                gridPanel.reconfigure(Ext.data.StoreManager.lookup('ctlStore'),columns);

                var rowcount=0;
                gridPanel.store.each(function(r){//每一行
                    for(var i=0;i<colArr.length;i++) {//每一格
                        var cellStr=r.get(colArr[i]);
                        if(cellStr!=undefined && cellStr.indexOf('%')!=-1){
                            var num=cellStr.substring(cellStr.indexOf('层')+1,cellStr.indexOf('%')).trim();//截取百分比数字
                            if(Number(num)<100){
                                gridPanel.getView().getCell(rowcount,i).setStyle('background-color','#00EE00');
                            }else{
                                gridPanel.getView().getCell(rowcount,i).setStyle('background-color','#FF6347');
                            }
                        }
                    }
                    rowcount=rowcount+1;
                });
            }
        });
    },

    SJGridRender:function (sjview) {
        var grid = sjview.down('grid');
        grid.getStore().proxy.url = "/device/histories";
        // grid.getStore().proxy.url = "/water/deviceWarning";
        grid.getStore().proxy.extraParams.deviceid = sjview.device.id;
        grid.getStore().load();
        grid.on('itemdblclick', function () {
            var devices = this.getStore('DeviceStore');
            for (var i = 0; i < devices.getCount(); i++) {
                if (devices.getAt(i).get('type') == 'SJ') {
                    this.showDeviceDetail(devices.getAt(i));
                    return;
                }
            }
        }, this);
    },

    MJOpen: function (btn) {
        var MJDetailView = btn.findParentByType('MJDetail');
        var deviceid = MJDetailView.device.data.id;
        Ext.Ajax.request({
            datatype:'text',
            url: '/entrance/open',
            params:{
                deviceid:deviceid
            },
            success: function (response) {
                var text = Ext.decode(response.responseText);
                if(text==true){
                    XD.msg('操作成功');
                    return;
                }else{
                    XD.msg('操作失败');
                    return;
                }
            },
            failure: function () {
                Ext.MessageBox.hide();
                XD.msg('操作中断');
            }
        });
    },

    MJnormalOpen: function (btn) {
        var MJDetailView = btn.findParentByType('MJDetail');
        var deviceid = MJDetailView.device.data.id;
        Ext.Ajax.request({
            datatype:'text',
            url: '/entrance/NormalOpen',
            params:{
                deviceid:deviceid
            },
            success: function (response) {
                var text = Ext.decode(response.responseText);
                if(text==true){
                    XD.msg('操作成功');
                    return;
                }else{
                    XD.msg('操作失败');
                    return;
                }
            },
            failure: function () {
                Ext.MessageBox.hide();
                XD.msg('操作中断');
            }
        });
    },

    MJnormalClose: function (btn) {
        var MJDetailView = btn.findParentByType('MJDetail');
        var deviceid = MJDetailView.device.data.id;
        Ext.Ajax.request({
            datatype:'text',
            url: '/entrance/NormalClose',
            params:{
                deviceid:deviceid
            },
            success: function (response) {
                var text = Ext.decode(response.responseText);
                if(text==true){
                    XD.msg('操作成功');
                    return;
                }else{
                    XD.msg('操作失败');
                    return;
                }
            },
            failure: function () {
                Ext.MessageBox.hide();
                XD.msg('操作中断');
            }
        });
    }

});