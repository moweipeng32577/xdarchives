/**
 * Created by SunK on 2018/7/31 0031.
 */
/**
 * 表格与表单视图
 */
Ext.define('Import.view.ImportFormAndGridView',{
    extend:'Ext.panel.Panel',
    xtype:'importFormAndGrid',
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
            store:'ImportStore',
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
                xtype:'importgrid'
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
                    xtype:'importgrid'
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
                    tbar:[{
                        text:'著录',
                        iconCls:'fa fa-plus-circle',
                        itemId:'isave'
                    },'-',{
                        text:'修改',
                        iconCls:'fa fa-pencil-square-o',
                        itemId:'imodify'
                    },'-',{
                        text:'删除',
                        iconCls:'fa fa-trash-o',
                        itemId:'idel'
                    },'-',{
                        text:'查看',
                        iconCls:'fa fa-eye',
                        itemId:'ilook'
                    }
                        // ,'-',{
                        //     text:'查看案卷',
                        //     itemId:'ilookfile'
                        // }
                        ,'-',{
                            text:'拆件',
                            iconCls:'fa fa-files-o',
                            itemId:'innerfileDismantle'
                        },'-',{
                            text:'插件',
                            iconCls:'fa fa-columns',
                            itemId:'innerfileInsertion'
                        }],
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
        xtype:'importformView'//指向表单视图
    },{
        itemId:'formandgrid',
        xtype:'importformAndGrid'//指向表单与表格视图
    }]
});