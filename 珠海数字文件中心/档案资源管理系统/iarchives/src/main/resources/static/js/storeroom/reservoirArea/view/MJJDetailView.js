/**
 * Created by Rong on 2019-01-17.
 */
var pageSize=25;
Ext.define('ReservoirArea.view.MJJDetailView',{
    extend:'ReservoirArea.view.DeviceDetailView',
    xtype:'MJJDetail',
    views:{
        xtype:'tabpanel',
        frame:false,
        activeTab:0,
        items:[{
            title:'库区',
            layout:'border',
            items:[{
                split:true,
                autoScroll:true,
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
                layout:'fit',
                items:[{
                    autoScroll:true,
                    itemId:'ruleId',
                    region:'north',
                    tbar:['已选中单元格: ', {text:'',itemId:'change'},'------',{text:'查看档案',itemId:'showEntry'}]
                }]
            }]

        }]

    }
});