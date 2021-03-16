/**
 * Created by Administrator on 2019/6/19.
 */
Ext.define('Accept.view.SterilizingView',{
    extend:'Ext.panel.Panel',
    xtype:'SterilizingView',
    items:[{
        itemId:'pairgrid',
        layout:{
            type:'vbox',
            pack: 'start',
            align: 'stretch'
        },
        items:[
            {
                flex:2.5,
                itemId:'northgrid',
                xtype:'SterilizingGridView'
            },{
                flex:2.5,
                title:'批次信息',
                itemId:'southgrid',
                xtype:'entrygrid',
                collapsible:true,
                collapseToolText:'收起',
                expandToolText:'展开',
                collapsed: true,
                split:true,
                allowDrag:true,
                hasSearchBar:false,
                expandOrcollapse:'expand',//默认打开
                store:'SterilizingGridStore',
                columns:[
                    {text: '消毒批次号', dataIndex: 'batchcode', flex: 2, menuDisabled: true},
                    {text: '消毒员', dataIndex: 'disinfector', flex: 2, menuDisabled: true},
                    {text: '消毒时间', dataIndex: 'disinfectiontime', flex: 2, menuDisabled: true},
                    {text: '状态', dataIndex: 'state', flex: 2, menuDisabled: true},
                    {text: '备注', dataIndex: 'batchremark', flex: 2, menuDisabled: true}
                ],
                tbar:{
                    overflowHandler:'scroller',
                    items:[
                        {
                            text:'完成',
                            iconCls:'fa fa-check',
                            itemId:'finish'
                        }
                    ]
                }
            }]
    }]
});
