/**
 * 设备详细视图基类
 * 构造设备名称、状态等公共内容
 * Created by Rong on 2019-01-17.
 */
Ext.define('ReservoirArea.view.DeviceDetailView',{
    device:null,
    extend:'Ext.panel.Panel',
    layout:'border',
    items:[{
        region:'north',
        itemId:'northPanel',
        height:80,
        layout:'hbox'
    },{
        region:'center',
        layout:'fit',
        itemId:'centerPanel'
    }],

    /**
     * 构造设备名称、状态等信息
     */
    onRender:function(){
        var me = this;
        var device = me.device;
        var coordinates = device.coordinate.split(",");
        var northPanel = me.down('[itemId=northPanel]');
        // 添加设备类型图片，固定高度60，宽度按设置设备大小比例
        // northPanel.add({
        //     margin:'10 10 10 50',
        //     xtype:'image',
        //     width:60 * coordinates[2] / coordinates[3],
        //     height:60,
        //    src:'/img/equip/'+ device.type + '.png'
        // });
        //添加设备名称
        northPanel.add({
            margin:'30 10 30 10',
            html:'<span style="font: 16px sans-serif">'+device.name+'</span>'
        });
        //添加设备状态，在线为绿色，离线为红色
        // northPanel.add({
        //     margin:'32 10 30 10',
        //     html:'<span style="font: 14px sans-serif;color: ' + (device.get('status')==0?'red':'green')
        //         + ';">设备状态：' + device.get('statusStr') + '</span>'
        // });
        //当前子类有设定温湿度时调用。
        if (me.setting){
            me.insert(1, Ext.apply(me.setting,{region:'north'}));
        }
        var centerPanel = me.down('[itemId=centerPanel]');
        centerPanel.add(me.views);
        me.callParent();
    }
});