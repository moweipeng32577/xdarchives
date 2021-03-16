/**
 * Created by Rong on 2017/10/24.
 */
Ext.define('Moveware.view.ManagementView',{
    extend:'Ext.window.Window',
    xtype:'managementView',
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
            store:'ManagementStore',
            collapsible:true,
            split:1,
            hideHeaders: true,
            header:false
        },{
            region:'center',
            layout:'card',
            itemId:'gridcard',
            activeItem:0,
            items:[{
                itemId:'onlygrid',
                xtype:'managementgridView'
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
                    xtype:'managementgridView'
                },{
                    flex:2,
                    itemId:'southgrid',
                    collapsible:true,
                    collapseToolText:'收起',
                    expandToolText:'展开',
                    collapsed: true,
                    split:true,
                    xtype:'button',
                    allowDrag:true,
                    hasSearchBar:false
                },{
                    flex:1,
                    itemId:'entriesId',
                    xtype:'textfield',
                    hidden: true
                },{
                    flex:1,
                    itemId:'entriesName',
                    xtype:'textfield',
                    hidden: true
                }]
            },{
                xtype: 'panel',
                itemId:'bgSelectOrgan',
                bodyStyle: 'background:#DFE8F6;background-image:url(../../img/background/bg_select_organ.jpg);background-repeat:no-repeat;background-position:center;'
            }]
        }]
    },{
        //xtype:'managementform'
    }]
});