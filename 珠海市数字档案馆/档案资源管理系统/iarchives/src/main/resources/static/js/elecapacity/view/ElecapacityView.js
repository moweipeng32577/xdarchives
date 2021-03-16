/**
 * Created by yl on 2017/11/3.
 */
Ext.define('Elecapacity.view.ElecapacityView', {
    extend: 'Ext.panel.Panel',
    xtype: 'elecapacityView',
    layout: 'border',
    items: [
        {   region:'north',
            height:'50%',
            layout:'column',
            items:[{
                columnWidth:0.5,
                title:'库房容量图',
                border:true,
                items:[{
                    html:'<iframe width="100%" height="250%" src="' + '/elecapacity/totalCapacity' + '"></iframe>',
                }]
            },{
                columnWidth:0.5,
                title:'容量使用趋势',
                border:true,
                items:[{
                    html:'<iframe width="100%" height="250%" src="' + '/elecapacity/usetotalCapacity' + '"></iframe>',
                }]
            }]
        },
        {   region:'south',
            height:'50%',
            layout:'column',
            items:[{
                columnWidth:0.5,
                title:'容量情况明细',
                border:true,
                items:[{
                    width:'100%',
                    height:'100%',
                    xtype:'elecapacityDetailView',
                }]
            },{
                columnWidth:0.5,
                title:'电子文件数量增长总趋势',
                border:true,
                items:[{
                    html:'<iframe width="100%" height="250%" src="' + '/elecapacity/eleusetotalCapacity' + '"></iframe>',
                }]
            }]
        }
        ]
});