/**
 * Created by Administrator on 2019/6/24.
 */


Ext.define('AcceptDirectory.view.AcceptDirectoryFormAndGridView',{
    extend:'Ext.panel.Panel',
    xtype:'acceptDirectoryFormAndGridView',
    layout:'card',
    activeItem:0,
    items:[{
        layout:'border',
        itemId:'gridview',
        items:[{
            region:'west',
            width:XD.treeWidth,
            xtype:'treepanel',
            itemId:'treepanelId',
            rootVisible:false,
            store:'AcceptDirectoryStore',
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
                xtype:'acceptDirectoryGridView'
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
                    xtype:'acceptDirectoryGridView'
                },{
                    flex:2.3,
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
                    tbar:[/*{
                        text:'著录',
                        iconCls:'fa fa-plus-circle',
                        itemId:'isave'
                    },'-',{
                        text:'修改',
                        iconCls:'fa fa-pencil-square-o',
                        itemId:'imodify'
                    },'-',*/{
                        text:'删除',
                        iconCls:'fa fa-trash-o',
                        itemId:'idel'
                    },'-',{
                        text:'查看',
                        iconCls:'fa fa-eye',
                        itemId:'ilook'
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
        xtype:'formView'//指向表单视图
    },{
        itemId:'formandgrid',
        xtype:'formAndGrid'//指向表单与表格视图
    },{
        itemId:'formandinnerGrid',
        xtype:'formAndInnerGrid'//指向表单与表格视图
    }]
});
