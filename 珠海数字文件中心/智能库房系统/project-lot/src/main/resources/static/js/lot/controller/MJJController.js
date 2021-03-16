/**
 * 密集架管理控制器
 * Created by wujy on 2019-09-23.
 */
Ext.define('Lot.controller.MJJController',{

    extend: 'Ext.app.Controller',

    views:['MJJDetailView'],
    stores:['MJJHTStore'],
    models:['MJJHTModel'],
    init:function(){
        this.control({
            'MJJDetail':{
              render:function (view) {
                  var prop = Ext.decode(view.device.get('prop'));
                  if(prop.version && prop.version == 'new'){
                      view.down('[itemId=onId]').setVisible(false);
                      view.down('[itemId=offId]').setVisible(false);
                      view.down('[itemId=stopColumnId]').setVisible(false);
                  }
              }
            },
            'MJJDetail [itemId=MJJRuleId]':{//点击选择单元格
                cellclick:this.cellClickHandler
            },

            'MJJDetail [itemId=onId]':{//打开电源
                click:function (view) {
                    var me = this;
                    me.opreate(view, '/deviceTask/openPower')
                }
            },
            'MJJDetail [itemId=offId]':{//关闭电源
                click:function (view) {
                    var me = this;
                    me.opreate(view, '/deviceTask/shutDown')
                }
            },
            'MJJDetail [itemId=stopColumnId]':{//停止所有列移动
                click:function (view) {
                    var me = this;
                    me.opreate(view, '/deviceTask/stopExercise')
                }
            },
            'MJJDetail [itemId=fobiddenId]':{//禁止移动所有的列
                click:function (view) {
                    var me = this;
                    me.opreate(view, '/deviceTask/fobiddenExercise')
                }
            },
            'MJJDetail [itemId=cleanFobiddenId]':{//解除禁止
                click:function (view) {
                    var me = this;
                    me.opreate(view, '/deviceTask/cleanFobiddenExercise')
                }
            },
            'MJJDetail [itemId=ventilationId]':{//通风
                click:function (view) {
                    var me = this;
                    // me.opreate(view, '/deviceTask/ventilation')
                    me.opreate(view, '/deviceTask/openAssginColumn',11);
                }
            },
            'MJJDetail [itemId=closeAllColumnId]':{//关闭所有列
                click:function (view) {
                    var me = this;
                    var col = view.up('MJJDetail').down('[itemId=recordColId]').getValue();//获取密集架列号
                    var deviceProp = view.up('MJJDetail').device.get('prop');
                    var layout = Ext.decode(deviceProp).layout;
                    var colValue =this.PrefixZero(col, 2) + '-0000-'+layout ;
                    me.chOpreate(view, '/deviceTask/closeColumn',"")
                }
            },
            'MJJDetail [itemId=openAssginColumnId]':{//打开指定列的密集架
                click:function (view) {
                    var me = this;
                    var col = view.up('MJJDetail').down('[itemId=recordColId]').getValue();//获取密集架列号
                    var deviceProp = view.up('MJJDetail').device.get('prop');
                    var layout = Ext.decode(deviceProp).layout;
                    var colValue =this.PrefixZero(col, 2) + '-0000-'+layout ;
                    // if(layout == 'R'){ //向右打开col减1
                    //     colValue= this.PrefixZero(col-1, 2) + '-0000-'+layout ;
                    // }
                    // if(layout == 'L'){
                    //     colValue = this.PrefixZero(col, 2) + '-0000-'+layout ;
                    // }

                    me.chOpreate(view, '/deviceTask/openAssginColumn',colValue);
                }
            }
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

    opreate:function (view,url,col) {
        var deviceId = view.up('MJJDetail').device.get('id');
        var devicePanel = view.up('MJJDetail');
        var deviceProp = devicePanel.device.get('prop');
        var code = Ext.decode(deviceProp).code;
        var version = Ext.decode(deviceProp).version;//密集架新旧版本
        Ext.Ajax.request({
            method: 'POST',
            url: url,
            params:{qNumber:code,col:col,deviceId:deviceId,version:version},
            success:function (response) {
                var obj = Ext.decode(response.responseText);
                XD.msg(obj.msg);
            }
        });
    },


    chOpreate:function (view,url,colValue) {
        var deviceCode= view.up('MJJDetail').device.get('code');
        // var devicePanel = view.up('MJJDetail');
        // var deviceProp = devicePanel.device.get('prop');
        // var code = Ext.decode(deviceProp).code;
        // var version = Ext.decode(deviceProp).version;//密集架新旧版本
        Ext.Ajax.request({
            method: 'POST',
            url: url,
            params:{model_device_deviceCode:deviceCode,model_value:colValue,model_prop_code:"MJJ_CTRL"},
            success:function (response) {
                var obj = Ext.decode(response.responseText);
                XD.msg(obj.msg);
            }
        });

    },

    /**
     * 自定义函数名：PrefixZero
     * @param num： 被操作数
     * @param n： 固定的总位数
     */
    PrefixZero:function(num, n) {
        return (Array(n).join(0) + num).slice(-n);
    }
});