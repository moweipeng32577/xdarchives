/**
 * Created by Rong on 2017/10/24.
 */
Ext.define('LongRetention.view.LongRetentionView',{
    extend:'Ext.panel.Panel',
    xtype:'longRetentionView',
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
            store:'LongRetentionTreeStore',
            collapsible:true,
            split:1,
            hideHeaders: true,
            header:false
        },{
            region:'center',
            xtype:'panel',
            layout:'border',
            items:[{
                region:'center',
                layout:'card',
                itemId:'gridcard',
                activeItem:1,
                items:[{
                    itemId:'onlygrid',
                    xtype:'longRetentionGridView'
                },{
                    xtype: 'panel',
                    itemId:'bgSelectOrgan',
                    bodyStyle: 'background:#DFE8F6;background-image:url(../../img/background/bg_select_organ.jpg);background-repeat:no-repeat;background-position:center;'
                }]
            }]
        }]
    },{
        xtype:'EntryFormView'
    }]
});