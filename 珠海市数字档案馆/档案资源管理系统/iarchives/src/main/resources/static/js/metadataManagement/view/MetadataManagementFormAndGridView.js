/**
 * Created by SunK on 2018/7/31 0031.
 */
/**
 * 表格与表单视图
 */
Ext.define('MetadataManagement.view.MetadataManagementFormAndGridView',{
    extend:'Ext.panel.Panel',
    xtype:'metadataManagementFormAndGrid',
    layout:'card',
    activeItem:0,
    items:[{
        layout:'border',
        xtype:'panel',
        itemId:'gridview',
        items:[{
            region:'west',
            width:XD.treeWidth,
            xtype:'treepanel',
            itemId:'treepanelId',
            rootVisible:false,
            store:'MetadataManagementStore',
            collapsible:true,
            split:1,
            header:false,
            hideHeaders: true
        },{
            region:'center',
            layout:'card',
            itemId:'gridcard',
            activeItem:2,
            items:[{
                itemId:'onlygrid',
                xtype:'metadataManagementgrid'
            },{
                itemId:'pairgrid',
                layout:{
                    type:'vbox',
                    pack: 'start',
                    align: 'stretch'
                },
                items:[{
                    flex:3,
                    itemId:'northgrid',
                    xtype:'metadataManagementgrid'
                },{
                    flex:2,
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
                    listeners: {
                        "collapse": function (view) {
                            view.expandOrcollapse='collapse';
                        },
                        "expand": function (view) {
                            view.expandOrcollapse='expand';
                        }
                    }
                }]
            },{
                xtype: 'panel',
                itemId:'bgSelectOrgan',
                bodyStyle: 'background:#DFE8F6;background-image:url(../../img/background/bg_select_organ.jpg);background-repeat:no-repeat;background-position:center;'
            }]
        }]
    },{
        itemId:'formview',
        xtype:'formView'//指向表单视图
    }]
});