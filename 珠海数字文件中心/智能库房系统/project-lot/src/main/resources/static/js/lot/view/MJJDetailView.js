/**
 * Created by Rong on 2019-01-17.
 */
var pageSize=25;
Ext.define('Lot.view.MJJDetailView',{
    extend:'Lot.view.DeviceDetailView',
    xtype:'MJJDetail',
    views:{
        xtype:'tabpanel',
        frame:false,
        activeTab:0,
        items:[{
            title:'库区',
            layout:'border',
            items:[{
                itemId:'reservoirArea',
                xtype:'panel',
                region:'west',
                layout: {align: 'middle',pack: 'center',type: 'hbox'}

            },{
                xtype:'textfield',
                hidden:true,
                itemId:'recordColId'
            },{
                region:'center',
                margin:'0 0 0 10',
                xtype:'panel',
                layout:'border',
                items:[{
                    autoScroll:true,
                    itemId:'ruleId',
                    region:'north',
                    flex:0.65,
                    tbar:['已选中单元格: ', {text:'',itemId:'change'}]
                },{
                    autoScroll:true,
                    itemId:'operate',
                    region:'center',
                    flex:0.35,
                    defaults: {
                        margin:'10 10 10 10',
                        width:'25%',
                        bodyStyle:'font-size:8px'
                    },
                    items:[
                        /*{
                        xtype:'button',
                        itemId:'onId',
                        text:'打开电源'
                    },{
                        xtype:'button',
                        itemId:'offId',
                        text:'关闭电源'
                    },{
                        xtype:'button',
                        itemId:'stopColumnId',
                        text:'停止移动'
                    },{
                        xtype:'button',
                        itemId:'fobiddenId',
                        text:'禁止移动所有的列'
                    },{
                        xtype:'button',
                        itemId:'cleanFobiddenId',
                        text:'解除禁止'
                    },{
                        xtype:'button',
                        itemId:'ventilationId',
                        text:'通风'
                    },
                     */{
                        xtype:'button',
                        itemId:'openAssginColumnId',
                        text:'打开指定列'
                    },{
                        xtype:'button',
                        itemId:'closeAllColumnId',
                        text:'关闭所有列'
                    }]
                }]
            }]

        },
        //     {
        //     title:'温湿度',
        //     scrollable: true,
        //     xtype: 'grid',
        //     store: 'MJJHTStore',
        //     columns: [
        //         {xtype: 'rownumberer', align: 'center', width: 40},
        //         {text: '采集时间', dataIndex: 'captureTime', flex: 2},
        //         {text: '温度(℃)', dataIndex: 'tem', flex: 1},
        //         {text: '湿度(％)', dataIndex: 'hum', flex: 1}
        //     ],
        //     listeners:{
        //         render:function (panel) {
        //             var deviceId = panel.up('MJJDetail').device.get('id');
        //             var store = panel.getStore();
        //             store.proxy.extraParams.deviceid = deviceId;
        //             store.reload();
        //         }
        //     },
        //     tbar: ['选择日期：', {
        //         xtype: 'datefield',
        //         blankText: '起始日期',
        //         itemId: 'beginDate'
        //     }, '-', {
        //         xtype: 'datefield',
        //         blankText: '结束日期',
        //         itemId: 'endDate'
        //     },'-',{
        //         text:'设备诊断',
        //         itemId:'deviceDiagnoseBtn'
        //     }, '-', {
        //         text: '打印',
        //         itemId: 'printBtn'
        //     }, '（标准温度：14-24℃，标准湿度：45-60％）'],
        //     bbar:{
        //         xtype: 'pagingtoolbar',
        //         displayInfo: true,
        //         displayMsg: '显示 {0} - {1} 条，共{2}条',
        //         emptyMsg: "没有数据显示",
        //         items:['-',{
        //             xtype:'combo',
        //             store: new Ext.data.ArrayStore({
        //                 fields: ['text', 'value'],
        //                 data: [['5', 5], ['10', 10], ['25', 25], ['50', 50], ['100', 100], ['300', 300],['1000', 1000]]
        //             }),
        //             displayField: 'text',
        //             value:pageSize,//使用默认分页大小
        //             valueField: 'value',
        //             editable: false,
        //             width: 80,
        //             listeners:{
        //                 render:function(comboBox){
        //                     var gridstore=comboBox.ownerCt.findParentByType('MJJDetail').down('grid').getStore();
        //                     gridstore.setPageSize(pageSize);
        //                     gridstore.loadPage(1);
        //                 },
        //                 change:function(comboBox){
        //                     var pSize = comboBox.getValue();
        //                     var gridstore=comboBox.ownerCt.findParentByType('MJJDetail').down('grid').getStore(); //重写加载store
        //                     comboBox.ownerCt.pageSize=parseInt(pSize);
        //                     gridstore.setPageSize(parseInt(pSize));
        //                     gridstore.loadPage(1);
        //                 }
        //             }}]
        //     }
        // }
        ]

    }
});