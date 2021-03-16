/**
 * Created by SunK on 2018/7/31 0031.
 */
Ext.define('MetadataManagement.view.MetadataManagementGridView',{
    extend:'Comps.view.EntryGridView',
    // extend: 'Comps.view.BasicGridView',
    xtype:'metadataManagementgrid',
    dataUrl:'/metadataManagement/entries',
    templateUrl:'/metadataManagement/getMetadataTemp',
    region: 'north',
    height: 40,
    // tbar:[
    //     {
    //         xtype: 'button',
    //         text : '查看',
    //         itemId:'look',
    //         iconCls:'fa fa-eye'
    //     }
    // ],
    tbar: functionButton,
    title:'当前位置：元数据管理',
    searchstore:{
        proxy: {
            type: 'ajax',
            url:'/template/queryName',
            extraParams:{nodeid:0},
            reader: {
                type: 'json',
                rootProperty: 'content',
                totalProperty: 'totalElements'
            }
        }
    },
    // store: 'metadataManagementGridStore',
    hasSelectAllBox:false,
    hasSelectAllBox:true
});