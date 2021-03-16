/**
 * 库房安全控制器
 * 显示视频监控实时画面
 * Created by Rong on 2019-01-17.
 */
Ext.define('Lot.controller.SecurityController',{

    extend: 'Ext.app.Controller',

    views:['JK.JKFrameView','JK.JKListView','JK.JKPanelView'],
    stores:['JKStore'],

    init:function(){
        this.control({
            '[itemId=securityPanel]':{
                init:function(view){
                    this.initialize(view);
                    var store = this.getStore('JKStore');
                    store.reload();
                }
            },
            '[itemId=comboPanel]':{
                select:function (v,c) {
                    //遍历设备列表，动态添加监控设备面板
                    window.jkPanel = null;
                    // var devices = this.getStore('JKStore');
                    // var device = devices.getAt(1);
                    var panel = v.up('jkframe');
                    panel.removeAll();
                    var type = c.data.value;
                    var num;
                    switch(type){
                        case 'p1':
                            num=1;
                            // var width = 600;
                            // var height= 400;
                            var width = panel.getWidth() / 2 - 30;
                            var height= panel.getHeight() / 2 - 30;
                            // var columnWidth = '0.45';
                            var margin = '10 10 10 10'
                            break;
                        case 'p2':
                            num=2;
                            var width = panel.getWidth() / 2 - 30;
                            var height= panel.getHeight() / 2 - 30;
                            // var columnWidth = '.45';
                            // var width = 600;
                            // var height= 400;
                            var margin = '10 10 10 10';
                            break;
                        case 'p3':
                            num=3;
                            var width = panel.getWidth() / 2 - 30;
                            var height= panel.getHeight() / 2 - 30;
                            // var columnWidth = '.45';
                            // var width = 600;
                            // var height= 400;
                            var margin = '10 10 10 10';
                            break;
                        case 'p4':
                            num=4;
                            var width = panel.getWidth() / 2 - 30;
                            var height= panel.getHeight() / 2 - 30;
                            // var columnWidth = '.45';
                            // var width = 600;
                            // var height= 400;
                            var margin = '10 10 10 10';
                            break;
                        default :
                            num=1;
                            break;
                    }
                    for(var i = 0;i<num;i++){
                        panel.add({
                            width: width,
                            height: height,
                            xtype: 'JKPanel',
                            // device: device,
                            margin:margin,
                            // columnWidth: columnWidth,
                            frame: true,
                            title:'先点击我，再双击左边列表显示监控视频！',
                            listeners : {
                                'render' : function(panel) {
                                    panel.body.on('click', function() {
                                        panel.up('jkframe').choosepanel(panel);
                                    });
                                    panel.header.on('click', function() {
                                        panel.up('jkframe').choosepanel(panel);
                                    });
                                }
                            }
                        });
                    }
                }
            },
            'JKlist':{
                'rowdblclick':function(grid,record){
                    if(window.jkPanel==null){
                        XD.msg('请先选择需要显示的监控视频的区域！');
                        return;
                    }
                    window.jkPanel.setTitle(record.data.name);
                    this.showJKDetail(record);
                }
            },
            'JKDetail':{
                render:this.JKGridRender
            },
            'JKDetail [itemId=replayDate]':{
                select:this.changeDate
            },
            'JKDetail [itemId=replayBtn]':{
                click:this.replayHandler
            },
            'JKDetail [itemId=deviceDiagnoseBtn]':{
                click:function (view) {
                    var me = this;
                    me.deviceDiagnose(view,'JKDetail');
                }
            }
        });
    },

    deviceDiagnose:function (view,text) {
        if (view.up(text).device.get('status') != 0) {
            XD.msg('设备正常！');
            return;
        }
        var id = view.up(text).device.get('id');

        var win = Ext.create('Ext.window.Window',{
            closeToolText:'关闭',
            // width:window.innerWidth-440,
            // height:window.innerHeight-110,
            width: '65%',
            height: '70%',
            title:'诊断信息',
            layout:'fit',
            items:[{xtype:'DeviceDiagnoseView'}]
        });
        var store =  win.down('DeviceDiagnoseView').getStore();
        store.proxy.extraParams.id = id;
        store.reload();
        win.show();
    },

    initialize:function(view){
        window.jkPanel = null;
        var panel = view.down('jkframe');
        var comboPanel = panel.down('[itemId = comboPanel]')
        comboPanel.setValue('p1');
        panel.removeAll();
        var width = panel.getWidth() / 2 - 30;
        var height= panel.getHeight() / 2 - 30;
        var margin = '10 10 10 10';
        panel.add({
            width: width,
            height: height,
            margin: margin,
            xtype: 'JKPanel',
            frame: true,
            title:'先点击我，再双击左边列表显示监控视频！',
            listeners : {
                'render' : function(panel) {
                    panel.body.on('click', function() {
                        panel.up('jkframe').choosepanel(panel);
                    });
                    panel.header.on('click', function() {
                        panel.up('jkframe').choosepanel(panel);
                    });
                }
            }
        });
    },

    showJKDetail:function (record) {
        var prop = Ext.decode(record.get('prop'));
        if(prop.ip.indexOf("10.10.100") != -1){
            var url = '/oldjk?path=' + 'http://10.10.100.3:8012/chznkg-ibuilding-webapp/ocx.jsp' +
                '&addr='+prop.ip+'&port='+prop.port+'&user='+prop.user+'&pwd='+prop.pwd+'&channel='+prop.channel
        }
        else{
            var url = '/jk?addr=' + prop.ip + '&port=' + prop.port + '&user=' + prop.user
                + '&pwd=' + prop.pwd + '&channel=' + prop.channel;

        }
        var tpl = new Ext.XTemplate('<iframe class="frame" src={url} frameborder=0 scrolling=no style="width:100%;height:100%;border:0;marginWidth:0;marginHeight:0;"></iframe>');
        tpl.compile();
        tpl.overwrite( window.jkPanel.body, {
            url : url
        });
    },

    JKGridRender:function(jkview){
        var grid = jkview.down('grid');
        var prop = Ext.decode(jkview.device.get('prop'));
        var start = new Date().format('Y-m-d') + ' 00:00:00';
        var end = new Date().format('Y-m-d') + ' 23:59:59';
        var ocx = Ext.getDom("NetOCX");
        if(ocx && ocx.login){
            ocx.login(prop.ip, prop.port, prop.user, prop.pwd);
            var records = ocx.SearchRemoteRecordFile(prop.channel, 0, start, end, false, false, '');
            var doc = '';
            if (window.ActiveXObject) {
                doc = new ActiveXObject("Microsoft.XMLDOM");
                doc.async = "false";
                doc.loadXML(records);
            } else {
                var parser = new DOMParser();
                doc = parser.parseFromString(records, "text/xml");
            }
            grid.getStore().loadData(doc);
        }
    },

    changeDate:function(field, value){

    },

    replayHandler:function(btn){
        console.log('replay');
    }

});