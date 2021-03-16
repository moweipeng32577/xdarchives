/**
 * Created by Rong on 2019-01-17.
 */
Ext.define('Lot.view.JKDetailView',{
    extend:'Lot.view.DeviceDetailView',
    xtype:'JKDetail',
    views:{
        xtype : 'tabpanel',
        split : true,
        items:[{
            title:'实时预览',
            xtype : 'JKPanel',
            listeners:{
                beforerender:function(frame){
                    frame.device = this.up('JKDetail').device;
                }
            }
        },{
            title:'录像回放',
            xtype:'grid',
            columns:[
                {text:'开始时间', dataIndex:'startTime', flex:2},
                {text:'结束时间', dataIndex:'stopTime', flex:2},
            ],
            tbar:['选择日期：', {
                xtype : 'datefield',
                format : 'Y-m-d',
                value : new Date().format('Y-m-d'),
                itemId : 'replayDate'
            },'-',{
                text : '查看',
                itemId : 'replayBtn'
            }
                // ,'-',{
                //     text:'设备诊断',
                //     itemId:'deviceDiagnoseBtn'
                // }
            ],
            store: new Ext.data.Store({
                fields:['startTime','stopTime']
            }),
            listeners:{
                afterrender:function(grid){
                    var d = new Date();
                    var year = d.getFullYear();
                    var day = d.getDate();
                    if(day < 10){
                        day = '0' + day;
                    }
                    var month = d.getMonth()+1;
                    if(month < 10){
                        month = '0' + month;
                    }
                    var hour = d.getHours();
                    var minutes = d.getMinutes();
                    var seconds = d.getSeconds();
                    var base = year+'-'+month+'-'+day
                    var gridStore = grid.getStore();
                    gridStore.removeAll();
                    for(var i=0;i<hour;i++){
                        if(i < 10){
                            i = '0' + i;
                        }
                        date = base+' '+i+':';
                        gridStore.insert(0,{
                            //num:i+1,
                            startTime:date + '00:00',
                            stopTime:date + '59:59'
                        })
                    }
                },
                itemdblclick:function(grid, record){
                    var win = new Ext.Window({
                        modal : true,
                        width : 610,
                        height : 500,
                        title : '视频回放',
                        layout : 'border',
                        closeToolText: '关闭',
                        items : [{
                            region : 'center',
                            border : false
                        }, {
                            region : 'south',
                            border : false,
                            bodyPadding : '5',
                            height : 40,
                            layout : 'column',
                            bodyStyle : 'padding: 10px;',
                            items : [{
                                xtype : 'slider',
                                draggable:false,
                                hidden:true,
                                columnWidth : .5,
                                minValue : 0,
                                increment : 1,
                                maxValue : 3600,
                                plugins : [new Ext.slider.Tip({
                                    getText : function(thumb) {
                                        return String.format(
                                                '<b>{0}%</b>',
                                                thumb.value);
                                    }
                                })]
                            }, {
                                xtype : 'button',
                                text : '快放',
                                columnWidth : .15,
                                handler : function() {
                                    var speed = win.down('[itemId=speed]')
                                    if(speed.text < 8){
                                        speed.setText(speed.text + 1);
                                        window.frames["videoframe"].PlayFast();
                                    }
                                }
                            }, {
                                xtype : 'button',
                                text : '慢放',
                                margin : '0 0 0 5',
                                columnWidth : .15,
                                handler : function() {
                                    var speed = win.down('[itemId=speed]')
                                    if(speed.text > -8){
                                        speed.setText(speed.text - 1);
                                        window.frames["videoframe"].PlaySlow();
                                    }
                                }
                            }, {
                                xtype:'label',
                                columnWidth : .15,
                                margin : '5 0 0 5',
                                text:'播放速度：'
                            }, {
                                xtype:'label',
                                margin : '5 0 0 5',
                                itemId:'speed',
                                text:1
                            }]
                        }],
                        listeners : {
                            show : function() {
                                var prop = Ext.decode(grid.up('JKDetail').device.get('prop'));
                                var url = '/record?addr=' + prop.ip + '&port=' + prop.port + '&user=' + prop.user
                                       + '&pwd=' + prop.pwd + '&channel=' + prop.channel
                                       + '&start='+ record.get('startTime').replace(' ','T')
                                       + '&end=' + record.get('stopTime').replace(' ','T') ;
                                var tpl = new Ext.XTemplate('<iframe id="videoframe" src={url} frameborder=0 scrolling=no style="width:100%;height:100%;border:0;marginWidth:0;marginHeight:0;"></iframe>');
                                tpl.compile();
                                tpl.overwrite(win.items.get(0).body, {
                                    url : url
                                });
                                Ext.defer(function(){
                                    task = {
                                        run : function() {
                                            var slider = win.items.get(1).items.get(0);
                                            slider.setValue(slider.getValue() + 1);
                                        },
                                        interval : 10000
                                    }
                                    Ext.TaskManager.start(task);
                                },1000);
                            },
                            close : function() {
                                Ext.TaskManager.stop(task);
                            }
                        }
                    });
                    win.show();
                }
            }
        }]
    }
});