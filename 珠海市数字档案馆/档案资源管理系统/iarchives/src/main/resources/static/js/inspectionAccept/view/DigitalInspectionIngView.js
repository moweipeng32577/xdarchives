/**
 * 表格与表单视图
 */
Ext.define('DigitalInspection.view.DigitalInspectionIngView',{
    extend:'Ext.panel.Panel',
    xtype:'DigitalInspectionIngView',
    items:[
        {
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
                    xtype:'DigitalInspectionIngGridView'
                },{
                    flex:2.5,
                    title:'批次条目',
                    header:false,
                     xtype:'DigitalInspectionIngSouthView',
                    collapsible:true,
                    collapseToolText:'收起',
                    expandToolText:'展开',
                    collapsed: false,
                    split:true,
                    allowDrag:true,
                    hasSearchBar:false,
                    expandOrcollapse:'expand',//默认打开
                }]
        }
        ]
});