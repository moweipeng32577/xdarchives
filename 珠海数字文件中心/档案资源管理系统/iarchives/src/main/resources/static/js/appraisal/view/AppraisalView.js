/**
 * Created by Rong on 2017/10/24.
 */
Ext.define('Appraisal.view.AppraisalView',{
    extend:'Ext.panel.Panel',
    xtype:'appraisalView',
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
            store:'AppraisalTreeStore',
            collapsible:true,
            split:1,
            hideHeaders: true,
            header:false,
            listeners:{
                load:function() {
                    var str="（到期节点数："+appraisalNumber[1]+"  总到期数量："+appraisalNumber[0]+"）";
                    parent.$(".appraisalNumberTip").text(str);
                }
            }
        },{
            region:'center',
            xtype:'panel',
            layout:'border',
            items:[{
                region:'center',
                layout:'card',
                itemId:'gridcard',
                activeItem:2,
                items:[{
                    itemId:'onlygrid',
                    xtype:'appraisalGridView'
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
                        xtype:'appraisalGridView'
                    },{
                        flex:2,
                        itemId:'southgrid',
                        collapsible:true,
                        collapseToolText:'收起',
                        expandToolText:'展开',
                        collapsed: true,
                        xtype:'entrygrid',
                        tbar:[{text:'查看',itemId:'ilook'}],
                        hasSearchBar:false
                    }]
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