/**
 * Created by SunK on 2020/5/25 0025.
 */
/**
 * Created by SunK on 2018/7/31 0031.
 */
Ext.define('MetadataManagement.view.MetadataManagementInnerGrid',{
    extend:'Comps.view.EntryGridView',
    // extend: 'Comps.view.BasicGridView',
    xtype:'entrygrid',
    dataUrl:'',
    templateUrl:'/metadataManagement/getServiceMetadataTemp',
    region: 'north',
    height: 40,
    isOpenEle:false,
    tbar:[
        {
            xtype: 'button',
            text : '增加',
            itemId:'addMetadata',
            iconCls:'fa fa-eye'
        },{
            xtype: 'button',
            text : '修改',
            itemId:'modifyMetadata',
            iconCls:'fa fa-eye'
        }, {
            xtype: 'button',
            text : '删除',
            itemId:'deleteMetadata',
            iconCls:'fa fa-eye'
        },{
            xtype: 'button',
            text : '查看',
            itemId:'lookMetadata',
            iconCls:'fa fa-eye'
        }
    ],
    title:'当前位置：元数据管理',
    searchstore:true,
    // store: 'metadataManagementGridStore',
    hasSelectAllBox:true
});