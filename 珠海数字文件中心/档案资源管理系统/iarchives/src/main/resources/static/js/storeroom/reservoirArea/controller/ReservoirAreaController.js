/**
 * create by wujy 2019.08.12
 */
Ext.define('ReservoirArea.controller.ReservoirAreaController',{
    extend: 'Ext.app.Controller',
    xtype:'reservoirAreaController',
    views:['ReservoirAreaView','ShelvesMoveView','CellDetailView','DeviceDetailView','MJJDetailView','MJJRuleView','InWareDetailView'],
    models:['DetailGridModel','RoomZoneGridModel','RoomColumnGridModel'],
    stores:['DetailGridStore','RoomZoneStore','RoomColumnStore'],
    init:function(){
        this.control({
            'reservoirAreaView [itemId=storeroomId]':{
                render:this.createStoreRoom
            },
            'reservoirAreaView [itemId=zonegrid]':{
                select : this.findColumn
            },
            'reservoirAreaView [itemId=openTBtn]':{
                click : this.openTBtn
            },

            'MJJDetail':{
                render:this.MJJGridRender
            },
            'MJJDetail [itemId=MJJRuleId]':{//点击选择单元格
                cellclick:this.cellClickHandler
            },
            'MJJDetail [itemId=showEntry]':{//点击查看档案
                click:this.showDetailHandler
            }
        });
    },
    createStoreRoom:function (panel) {
        for (var i = 0; i < floorAndRoomList.length; i++) {
            var floordisplay = floorAndRoomList[i].floordisplay;
            var roomdisplay = floorAndRoomList[i].roomdisplay;
            var fontSize = 1;
            var storeRoomPanelHeight = 190;//分辨率1920时默认高度
            var storeRoomPanelWidth = 315;//分辨率1920时默认宽度
            var screenWidth = window.screen.width;
            if (screenWidth <= 1366) {//根据识别屏幕分辨率为1366时来改变html字体大小
                fontSize = 1
                storeRoomPanelHeight = 150;
                storeRoomPanelWidth = 225;
                document.getElementsByTagName("html")[0].style.fontSize = '12px';
            } else if (screenWidth <= 1600) {//分辨率为1600
                fontSize = 1
                storeRoomPanelHeight = 170;
                storeRoomPanelWidth = 263;
                document.getElementsByTagName("html")[0].style.fontSize = '14px';
            }
            var archivestype="";//档案类别
            for(var k=0;k<deviceAreaList.length;k++){
                if(floorAndRoomList[i].roomdisplay==deviceAreaList[k].name){
                    archivestype=deviceAreaList[k].archivestype
                }
            }
            var storeRoomHtml = '<div class="parent" style="font-size: ' + fontSize + 'rem;color: white;border: red">' +
                '<div style="margin: 0.5rem">温度:14-24℃ , 湿度:45-60%</div>' +
                '<div style="margin: 0.5rem">库房位置: <span class="floor">' + floordisplay + '</span></div>' +
                '<div style="text-align: center;margin-top:1.3rem"><span class="room">' + roomdisplay + '</span></div>' +
                '<div style="text-align: center; margin: 1.3rem 1.7rem;line-height:1.3rem">' +
                '档案门类:'+archivestype+'</div></div>';
            panel.add({
                xtype: 'panel',
                width: storeRoomPanelWidth,
                height: storeRoomPanelHeight,
                border: true,
                html: storeRoomHtml,
                name:roomdisplay,
                bodyStyle: 'background:RGB(70,114,196); margin:0.5rem',
                listeners: {
                    el: {
                        click: function (e, t) {
                            var parent = Ext.get(t).getParent().dom.getElementsByClassName('room');
                            var room = parent[0].textContent;
                            var zone = panel.up('reservoirAreaView').down('[itemId=zonegrid]');
                            var store = zone.getStore();
                            store.proxy.extraParams.roomDisplay = room;
                            store.reload();
                        }
                    }
                }
            });
        }
    },

    findColumn:function (view) {
        var zoneid = view.lastSelected.data.zoneid;
        window.zoneid=zoneid;
        var store = view.view.up('reservoirAreaView').down('[itemId=colgrid]').getStore();
        store.proxy.extraParams.zoneid = zoneid;
        store.reload();
        var deviceId = view.lastSelected.data.device;
        Ext.Ajax.request({
            method: 'GET',
            async:false,
            url:'/device/' + deviceId,
            success: function (response) {
                window.device = Ext.decode(response.responseText);
            },
            failure:function () {
                XD.msg('获取该区密集架设备失败');
            }
        });
    },

    openTBtn:function (btn) {
        var win = Ext.create('Ext.window.Window',{
            closeToolText:'关闭',
            width:window.innerWidth-220,
            height:window.innerHeight-50,
            title:window.device.name,
            type:window.device.type,
            layout:'fit',
            items:[{
                device:window.device,
                xtype: 'MJJDetail'
            }]
        });
        win.show();
    },

    MJJGridRender:function (mjjview) {
        var areaPanel = mjjview.down('[itemId=reservoirArea]');
        var win = mjjview.up('window');
        areaPanel.setWidth(win.width/2);
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
            params: {zoneid: window.zoneid},
            method: 'GET',
            async: true,
            success: function (response) {
                var result = Ext.decode(response.responseText);
                var fixed = null;
                var data = result.data;
                // if (data.length > 0) {
                //     fixed = data[0].zone.fixed;//固定列
                // }
                for (var i = 0; i < data.length; i++) {
                    var obj = {
                        layout: 'absolute',
                        items: [{
                            itemId:'mjjImage',
                            xtype: 'panel',
                            html: (i + 1),
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
                                        var Images = areaPanel.down('[itemId = mjjImage]');
                                        var col = Ext.get(t).getParent().dom.textContent;
                                        recordValue.setValue(col);
                                        me.MJJRuleInit( col, areaPanel);

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
                            value: data[i].rate
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
            me.MJJRuleInit('1', areaPanel);
        });
    },

    MJJRuleInit:function (col,view) {
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
                zoneid:window.zoneid,
                col:col,
                side:side
            },
            method:'GET',
            success: function (response) {
                var data = response.responseText;//Ext.decode(response.responseText);
                var i=data.length;
                var colStr=data.substring(data.lastIndexOf(']')+1,data.length-1);
                var storeData=data.substring(1,data.lastIndexOf(']')+1);
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
                            if(num.length<3){
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

    showDetailHandler:function(btn){//EXT GridPanel获取某一单元格的值,然后获取相应的库存信息显示
        //先判断数据来源输入框是否为空，为空的话提示先指定一个单元格数据，不为空的话清空目标输入框,标记临时输入框tempShid
        var sourceText=btn.up('MJJDetail').down('[itemId=change]');
        var str=sourceText.getText();
        if (str == '' || str == null ) {XD.msg('请先选定一个需要查看的位置');return;}
        if (str.indexOf('%')<0 ) {XD.msg('选定的'+str+'没有可以查看的档案');return;}

        // var zoneText= btn.up('shelvesMoveView').down('[itemId=zoneId]');
        var zoneid=window.zoneid;
        //var msgStr='是否确定查看'+sourceText+'的库存档案信息';
        // var shelvesMoveView = btn.up('shelvesMoveView');
        var inid = str.substring(0,str.indexOf('层')+1)+zoneid;
        var win = this.getView('InWareDetailView').create({
            title:str+'&nbsp&nbsp&nbsp&nbsp&nbsp库存档案信息&nbsp&nbsp&nbsp&nbsp&nbsp绿色代表已入库&nbsp&nbsp&nbsp&nbsp&nbsp奶黄代表已出库',
            modal: true,
            //resizable: false,
            width:'85%',
            height:'75%',
            itemId:'detailView',
            baseProperty: inid
        });
        // shelvesMoveView.detailView = win;
        var button=win.down('[itemId=basicgridCloseBtn]');//关闭所有页面按钮
        button.hide();//隐藏【关闭】按钮
        win.show();
        var gridcard=win.down('[itemId=detailGridView]');
        gridcard.initGrid({nodeid:templateNodeid});

        gridcard.getStore().on('load',function(s,records){
            var rowcount=0;
            s.each(function(r){
                if(r.get('nodefullname')=='已入库'){
                    gridcard.getView().getRow(rowcount).style.backgroundColor='#9AFF9A';
                }else{
                    gridcard.getView().getRow(rowcount).style.backgroundColor='#FFFACD';
                }
                rowcount=rowcount+1;
            });
        });
    },

    //选择单元格
    cellClickHandler:function(table,td,cellIndex,record){//EXT GridPanel获取某一单元格的值
        var fileName;
        fileName=table.getHeaderAtIndex(cellIndex).dataIndex;//单元格的key
        var data = record.get(fileName);
        data=fileName+data;
        //XD.msg(data);
        //获取单元格信息后解析相应的单元格shid,放到一个位置来源，点击移动，清除位置目的存放的数据，
        // 再点击单元格，取单元格信息后解析相应的单元格shid,放到一个位置目的
        //点击放置，读取两个shid进行切换存储内容，然后刷新表格内容
        var changeText=table.up('MJJDetail').down('[itemId=change]');
        changeText.setText(data);
    },
});